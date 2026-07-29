package com.example.demo.service;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostFeedDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.CommunityNotFoundException;
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
import jakarta.transaction.Transactional;
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

    public Post createPost(PostCreateDto dto) {

        User author = userService.getAuthenticatedUser();

        // TO DO add check if user is deleted

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setNsfw(dto.isNsfw());
        post.setAuthor(author);

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String imageUrl = s3ImageService.uploadImage(dto.getImage(), dto.getFilter());
            post.setImageUrl(imageUrl);
        }

        if (dto.getCommunityName() != null && !dto.getCommunityName().isBlank()) {
            Community community = communityService.findByName(dto.getCommunityName());
            post.setCommunity(community);
        }

        return postRepository.save(post);
    }

    @Transactional
    public VoteResponseDto votePost(UUID postId, String voteTypeStr) {
        Post post = findById(postId);
        User user = userService.getAuthenticatedUser();

        Optional<PostVote> existingVoteOpt = postVoteRepository.findByPostAndUser(post, user);

        if (voteTypeStr.equals("none")) {
            if (existingVoteOpt.isPresent()) {
                PostVote existingVote = existingVoteOpt.get();
                removeVoteFromPost(post, existingVote.getVoteType());
                postVoteRepository.delete(existingVote);
            }
        } else {
            VoteType newVoteType = voteTypeStr.equals("up") ? VoteType.UPVOTE : VoteType.DOWNVOTE;

            if (existingVoteOpt.isPresent()) {
                PostVote existingVote = existingVoteOpt.get();

                if (existingVote.getVoteType() == newVoteType) {
                    removeVoteFromPost(post, existingVote.getVoteType());
                    postVoteRepository.delete(existingVote);
                    voteTypeStr = "none";
                } else {
                    removeVoteFromPost(post, existingVote.getVoteType());
                    addVoteToPost(post, newVoteType);
                    existingVote.setVoteType(newVoteType);
                    postVoteRepository.save(existingVote);
                }
            } else {
                PostVote newVote = new PostVote();
                newVote.setPost(post);
                newVote.setUser(user);
                newVote.setVoteType(newVoteType);
                postVoteRepository.save(newVote);

                addVoteToPost(post, newVoteType);
            }
        }

        postRepository.save(post);

        return VoteResponseDto.builder()
                .upvotes(post.getUpvotes())
                .downvotes(post.getDownvotes())
                .score(post.getUpvotes() - post.getDownvotes())
                .userVote(voteTypeStr.equals("none") ? null : voteTypeStr)
                .build();
    }

    private void addVoteToPost(Post post, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            post.setUpvotes(post.getUpvotes() + 1);
        } else {
            post.setDownvotes(post.getDownvotes() + 1);
        }
    }

    private void removeVoteFromPost(Post post, VoteType voteType) {
        if (voteType == VoteType.UPVOTE) {
            post.setUpvotes(post.getUpvotes() - 1);
        } else {
            post.setDownvotes(post.getDownvotes() - 1);
        }
    }

    public PostResponseDto getEnrichedPostDto(Post post) {
        PostResponseDto dto = postMapper.toDto(post);

        // this is filled according to the docs from the frontend not used in CLI
        dto.setScore(post.getUpvotes() - post.getDownvotes());

        try {
            User currentUser = userService.getAuthenticatedUser();
            Optional<PostVote> voteOpt = postVoteRepository.findByPostAndUser(post, currentUser);

            if (voteOpt.isPresent()) {
                VoteType type = voteOpt.get().getVoteType();
                dto.setUserVote(type == VoteType.UPVOTE ? "up" : "down");
            } else {
                dto.setUserVote(null);
            }

        } catch (Exception e) {
            dto.setUserVote(null);
        }

        return dto;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public PostFeedDto getRandomizedFeed(String seed,int page,int size){
        int offset=size*page;
        List<Post> feedPosts=postRepository.getRandomizedFeed(seed,size,offset);

        PostFeedDto postFeedDto = new PostFeedDto();
        postFeedDto.setPosts(feedPosts);
        postFeedDto.setSeed(seed);
        return postFeedDto;
    }

    public Post findById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id=" + id));
    }

    @Transactional
    public List<Post> getCommunityPosts(String communityName) {
        Community community = communityService.findByName(communityName);
        return community.getPosts();
    }

    public Post updatePost(UUID id, PostUpdateDto updateDto) {
        Post post = findById(id);
        User authenticatedUser = userService.getAuthenticatedUser();
        if(!post.getAuthor().equals(authenticatedUser)){
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }

        post.setTitle(updateDto.getTitle());
        post.setContent(updateDto.getContent());
        post.setNsfw(updateDto.isNsfw());
        if(updateDto.getImage()!=null && !updateDto.getImage().isEmpty()){
            String imageLink = s3ImageService.uploadImage(updateDto.getImage(), updateDto.getFilter());
            post.setImageUrl(imageLink);
        }


        return postRepository.save(post);
    }

    @Transactional
    public void deletePostById (UUID id) {
        Post post = findById(id);
        if(!post.getAuthor().equals(userService.getAuthenticatedUser()))
            throw new AccessDeniedException("You are not the author of this post.");

        postRepository.delete(post);
    }
}
