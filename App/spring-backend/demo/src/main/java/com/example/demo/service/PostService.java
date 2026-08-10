package com.example.demo.service;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.model.Community;
import com.example.demo.model.Post;
import com.example.demo.model.PostVote;
import com.example.demo.model.User;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.PostVoteRepository;

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
    private final UserService userService;
    private final S3ImageService s3ImageService;
    private final CommunityService communityService;
    private final PostVoteRepository postVoteRepository;
    private final PostMapper postMapper;
    private final CommunityRepository communityRepository;
    private final ImageEditService imageEditService;
    private final EntityManager entityManager;

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
            Community community = communityService.findByName(dto.getSubreddit());
            post.setCommunity(community);
        }


        Post savedPost = postRepository.save(post);
        votePost(savedPost.getId(), VoteAction.UP);
        return findById(savedPost.getId());
    }

    @Transactional
    public VoteResponseDto votePost(UUID postId, VoteAction action) {
        Post post = findById(postId);
        User user = userService.getAuthenticatedUser();

        Optional<PostVote> existingVoteOpt = postVoteRepository.findByPostAndUser(post, user);

        VoteAction finalAction = action;

        if (action == VoteAction.NONE) {
            // Explicit unvote request
            if (existingVoteOpt.isPresent()) {
                PostVote existingVote = existingVoteOpt.get();
                removeVoteFromPost(postId, existingVote.getVoteType());
                postVoteRepository.delete(existingVote);
            }
        } else {
            VoteType targetVoteType = (action == VoteAction.UP) ? VoteType.UPVOTE : VoteType.DOWNVOTE;

            if (existingVoteOpt.isPresent()) {
                PostVote existingVote = existingVoteOpt.get();

                if (existingVote.getVoteType() == targetVoteType) {
                    removeVoteFromPost(postId, existingVote.getVoteType());
                    postVoteRepository.delete(existingVote);
                    finalAction = VoteAction.NONE;
                } else {
                    removeVoteFromPost(postId, existingVote.getVoteType());
                    addVoteToPost(postId, targetVoteType);
                    existingVote.setVoteType(targetVoteType);
                    postVoteRepository.save(existingVote);
                }
            } else {
                PostVote newVote = new PostVote();
                newVote.setPost(post);
                newVote.setUser(user);
                newVote.setVoteType(targetVoteType);
                postVoteRepository.save(newVote);

                addVoteToPost(postId, targetVoteType);
            }
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
            postRepository.incrementUpvotes(postId);
        } else {
            postRepository.incrementDownvotes(postId);
        }
    }

    private void removeVoteFromPost(UUID postId, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            postRepository.decrementUpvotes(postId);
        } else {
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
    public List<Post> getCommunityPosts(String communityName) {
        Community community = communityService.findByName(communityName);
        return community.getPosts();
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

        return postRepository.save(post);
    }

    @Transactional
    public void deletePostById(UUID id) {
        Post post = findById(id);
        if (!post.getAuthor().equals(userService.getAuthenticatedUser()))
            throw new AccessDeniedException("You are not the author of this post.");

        postRepository.delete(post);
        postRepository.flush();

//        Community community = post.getCommunity();
////        if (NSFW && community != null) {
////            updateCommunityNSFWStatus(community);
////        }
    }

//    private void updateCommunityNSFWStatus(Community community) {
//        if (community == null) return;
//
//        boolean stillHasNsfw = postRepository.existsByCommunityNameAndNsfwTrue(community.getName());
//
//        if (Boolean.TRUE.equals(community.getNSFW()) != stillHasNsfw) {
//            community.setNSFW(stillHasNsfw);
//            communityRepository.save(community);
//        }
//    }
}