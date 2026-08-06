package com.example.demo.service;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
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
//        if (savedPost.getNsfw() && savedPost.getCommunity() != null) {
//            Community community = savedPost.getCommunity();
//
//            if (Boolean.FALSE.equals(community.getNSFW())) {
//                community.setNSFW(true);
//                communityRepository.save(community);
//            }
//        }
        votePost(savedPost.getId(), "up");
        return savedPost;
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

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllEnrichedPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::getEnrichedPostDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getCommunityEnrichedPosts(String communityName) {
        Community community = communityService.findByName(communityName);
        return community.getPosts().stream()
                .map(this::getEnrichedPostDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponseDto getEnrichedPostById(UUID id) {
        Post post = findById(id);
        return getEnrichedPostDto(post);
    }

    public PostResponseDto getEnrichedPostDto(Post post) {
        PostResponseDto dto = postMapper.toDto(post);

        dto.setScore(post.getUpvotes() - post.getDownvotes());
        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);

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

    /*public PostResponseDto getEnrichedPostDtoForGuest(Post post) {
        PostResponseDto postResponseDto = postMapper.toDto(post);
        postResponseDto.setScore(post.getUpvotes() - post.getDownvotes());
        postResponseDto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        postResponseDto.setUserVote(null);
        return postResponseDto;
    }
*/
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

        System.out.println(updateDto.getTitle() + updateDto.getContent());

//        if (updateDto.getImage() != null && !updateDto.getImage().isEmpty()) {
//            Integer validFilter = imageEditService.getValidFilterId(updateDto.getFilter());
//            String imageUrl = s3ImageService.uploadImage(updateDto.getImage(), validFilter);
//
//            post.setImageUrl(imageUrl);
//            post.setFilter(validFilter);
//        }

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