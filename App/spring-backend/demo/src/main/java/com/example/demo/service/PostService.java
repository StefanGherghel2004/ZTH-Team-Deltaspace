package com.example.demo.service;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.logger.Logger;
import com.example.demo.mapper.PostMapper;
import com.example.demo.model.Subreddit;
import com.example.demo.model.Post;
import com.example.demo.model.PostVote;
import com.example.demo.model.User;
import com.example.demo.model.*;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.*;

import jakarta.persistence.EntityManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final UserService userService;
    private final S3ImageService s3ImageService;
    private final SubredditService subredditService;
    private final PostVoteRepository postVoteRepository;
    private final PostMapper postMapper;
    private final SubredditRepository subredditRepository;
    private final ImageEditService imageEditService;
    private final EntityManager entityManager;
    private final PostSummaryService postSummaryService;

    @Transactional
    public Post createPost(PostCreateDto dto) {
        User author = userService.getAuthenticatedUser();

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(author);

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            Integer validFilter = imageEditService.getValidFilterId(dto.getFilter());
            String imageUrl = s3ImageService.uploadImage(dto.getImage(), validFilter);

            post.setImageUrl(imageUrl);
            post.setFilter(validFilter);
        }

        if (dto.getSubreddit() != null && !dto.getSubreddit().isBlank()) {
            Subreddit subreddit = subredditService.findByName(dto.getSubreddit());
            post.setSubreddit(subreddit);
        }


        Post savedPost = postRepository.save(post);
        votePost(savedPost.getId(), VoteAction.UP);
        Logger.info("Post %s created by %s", savedPost.getTitle(), author.getUsername());

        if(dto.getContent().length() > 1500) {
            String tldr = postSummaryService.generateTldr(dto.getTitle(), dto.getContent());
            Comment tldrComment = new Comment();
            tldrComment.setParentComment(null);
            tldrComment.setPost(post);
            tldrComment.setUser(author);
            tldrComment.setContent("TL;DR " + tldr);
            commentRepository.save(tldrComment);
        }

        return findById(savedPost.getId());
    }

    @Transactional
    public VoteResponseDto votePost(UUID postId, VoteAction action) {
        Post post = findById(postId);
        User user = userService.getAuthenticatedUser();

        Optional<PostVote> existingVoteOpt = postVoteRepository.findByPostAndUser(post, user);

        VoteAction finalAction = (action != null) ? action : VoteAction.NONE;

        VoteType requestedVoteType = switch(finalAction){
            case UP -> VoteType.UPVOTE;
            case DOWN -> VoteType.DOWNVOTE;
            case NONE -> null;
        };

        VoteType currentVoteType = existingVoteOpt.map(PostVote::getVoteType).orElse(null);
        VoteType newVoteType = (currentVoteType == requestedVoteType) ? null:requestedVoteType;

        if(currentVoteType==null && newVoteType==null){
            return null;
        }
        existingVoteOpt.ifPresent(existingVote->{
            removeVoteFromPost(postId,existingVote.getVoteType());
            if(newVoteType==null){
                postVoteRepository.delete(existingVote);
            }
        });

        if(newVoteType!=null){
            PostVote voteToSave=existingVoteOpt.orElseGet(()->{
                PostVote vote = new PostVote();
                vote.setPost(post);
                vote.setUser(user);
                return vote;
            });
        voteToSave.setVoteType(newVoteType);
        postVoteRepository.save(voteToSave);
        addVoteToPost(postId,newVoteType);
        }

        entityManager.flush();
        entityManager.clear();

        Post updatedPost = findById(postId);

        return VoteResponseDto.builder()
                .upvotes(updatedPost.getUpvotes())
                .downvotes(updatedPost.getDownvotes())
                .score(updatedPost.getUpvotes() - updatedPost.getDownvotes())
                .userVote(finalAction == VoteAction.NONE ? null : finalAction.getValue())
                .build();
    }

    private void addVoteToPost(UUID postId, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            Logger.info("User %s upvoted post %s", userService.getAuthenticatedUser().getUsername(), postId);
            postRepository.incrementUpvotes(postId);
        } else {
            Logger.info("User %s downvoted post %s", userService.getAuthenticatedUser().getUsername(), postId);
            postRepository.incrementDownvotes(postId);
        }
    }

    private void removeVoteFromPost(UUID postId, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            Logger.info("User %s unvoted post %s", userService.getAuthenticatedUser().getUsername(), postId);
            postRepository.decrementUpvotes(postId);
        } else {
            Logger.info("User %s unvoted post %s", userService.getAuthenticatedUser().getUsername(), postId);
            postRepository.decrementDownvotes(postId);
        }
    }

    public PostResponseDto getEnrichedPostDto(Post post) {
        PostResponseDto dto = postMapper.toDto(post);

        dto.setScore(post.getUpvotes() - post.getDownvotes());
        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        if(post.getAuthor().isDeleted()) {
            dto.setAuthor("[deleted]");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                User currentUser = userService.getAuthenticatedUser();
                Optional<PostVote> voteOpt = postVoteRepository.findByPostAndUser(post, currentUser);

                if (voteOpt.isPresent()) {
                    VoteType type = voteOpt.get().getVoteType();
                    dto.setUserVote(type == VoteType.UPVOTE ? "up" : "down");
                } else {
                    dto.setUserVote(null);
                }
            } catch (Exception ignored) {
                dto.setUserVote(null);
            }
        } else {
            dto.setUserVote(null);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post findById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts(String subredditName) {
        if(subredditName != null && !subredditName.isBlank()){
            Subreddit subreddit = subredditService.findByName(subredditName);
            return subreddit.getPosts();
        }else{
            return getAllPosts();
        }
    }

    @Transactional
    public Post updatePost(UUID id, PostUpdateDto updateDto) {
        Post post = findById(id);
        User authenticatedUser = userService.getAuthenticatedUser();

        if (!post.getAuthor().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }

        if (updateDto.getTitle() != null && !updateDto.getTitle().isBlank()) {
            post.setTitle(updateDto.getTitle());
        }

        if (updateDto.getContent() != null && !updateDto.getContent().isBlank()) {
            post.setContent(updateDto.getContent());
        }

        Logger.info("Post %s updated by %s", post.getTitle(), authenticatedUser.getUsername());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePostById(UUID id) {
        Post post = findById(id);
        if (!post.getAuthor().equals(userService.getAuthenticatedUser()))
            throw new AccessDeniedException("You are not the author of this post.");

        Logger.info("Post %s deleted by %s", post.getTitle(), userService.getAuthenticatedUser().getUsername());
        post.setTitle("[DELETED]");
        post.setContent("[DELETED]");
        post.setImageUrl(null);

        postRepository.save(post);
        postRepository.flush();
    }
}