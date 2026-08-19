package com.example.demo.servicetests;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.event.PostCreatedEvent;
import com.example.demo.event.PostUpdatedEvent;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.PostVote;
import com.example.demo.model.Subreddit;
import com.example.demo.model.User;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.PostVoteRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.ImageEditService;
import com.example.demo.service.ImageUploadService;
import com.example.demo.service.PostService;
import com.example.demo.service.PostSummaryService;
import com.example.demo.service.ProfanityFilterService;
import com.example.demo.service.SubredditService;
import com.example.demo.service.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostVoteRepository postVoteRepository;

    @Mock
    private UserService userService;

    @Mock
    private ImageUploadService imageUploadService;

    @Mock
    private SubredditService subredditService;

    @Mock
    private ProfanityFilterService profanityFilterService;

    @Mock
    private ImageEditService imageEditService;

    @Mock
    private PostSummaryService postSummaryService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CommentService commentService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User sampleUser;
    private User otherUser;
    private Post samplePost;
    private UUID samplePostId;
    private Subreddit sampleSubreddit;

    @BeforeEach
    void setUp() {
        samplePostId = UUID.randomUUID();

        sampleUser = new User();
        sampleUser.setId(UUID.randomUUID());
        sampleUser.setUsername("testuser");
        sampleUser.setDeleted(false);

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("otheruser");
        otherUser.setDeleted(false);

        sampleSubreddit = new Subreddit();
        sampleSubreddit.setId(UUID.randomUUID());
        sampleSubreddit.setName("gaming");
        sampleSubreddit.setPosts(new ArrayList<>());

        samplePost = new Post();
        samplePost.setId(samplePostId);
        samplePost.setTitle("Sample Post Title");
        samplePost.setContent("Sample post content");
        samplePost.setAuthor(sampleUser);
        samplePost.setSubreddit(sampleSubreddit);
        samplePost.setUpvotes(0);
        samplePost.setDownvotes(0);
        samplePost.setDeleted(false);
        samplePost.setComments(new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(String username) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("Should successfully create a post with image and subreddit, auto-upvote it, and publish PostCreatedEvent")
    void createPostWithImageAndSubredditSuccess() {
        MultipartFile mockImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});

        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("My New Post");
        dto.setContent("Content here");
        dto.setImage(mockImage);
        dto.setFilter(1);
        dto.setSubreddit("gaming");

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(profanityFilterService.censor("Content here")).thenReturn("Content here");
        when(imageEditService.getValidFilterId(1)).thenReturn(1);
        when(imageUploadService.upload(mockImage, 1)).thenReturn("https://s3.example.com/test.jpg");
        when(subredditService.findByName("gaming")).thenReturn(sampleSubreddit);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(samplePostId);
            return post;
        });

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.empty());

        Post result = postService.createPost(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(samplePostId);
        verify(postRepository, times(1)).incrementUpvotes(samplePostId);
        verify(postVoteRepository, times(1)).save(any(PostVote.class));
        verify(eventPublisher, times(1)).publishEvent(any(PostCreatedEvent.class));
    }

    @Test
    @DisplayName("Should successfully create a text-only post without image and subreddit and publish PostCreatedEvent")
    void createPostTextOnlySuccess() {
        PostCreateDto dto = new PostCreateDto();
        dto.setTitle("Text Post");
        dto.setContent("Just plain text");
        dto.setImage(null);
        dto.setSubreddit(null);

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(profanityFilterService.censor("Just plain text")).thenReturn("Just plain text");
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(samplePostId);
            return post;
        });
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.empty());

        Post result = postService.createPost(dto);

        assertThat(result).isNotNull();
        verify(imageUploadService, never()).upload(any(), anyInt());
        verify(subredditService, never()).findByName(anyString());
        verify(eventPublisher, times(1)).publishEvent(any(PostCreatedEvent.class));
    }

    @Test
    @DisplayName("Should successfully register a new upvote on a post")
    void votePostNewUpvoteSuccess() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.empty());

        samplePost.setUpvotes(1);
        samplePost.setDownvotes(0);

        VoteResponseDto result = postService.votePost(samplePostId, VoteAction.UP);

        assertThat(result).isNotNull();
        assertThat(result.getUpvotes()).isEqualTo(1);
        assertThat(result.getDownvotes()).isEqualTo(0);
        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getUserVote()).isEqualTo("up");

        verify(postRepository, times(1)).incrementUpvotes(samplePostId);
        verify(postVoteRepository, times(1)).save(any(PostVote.class));
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }

    @Test
    @DisplayName("Should successfully register a new downvote on a post")
    void votePostNewDownvoteSuccess() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.empty());

        samplePost.setUpvotes(0);
        samplePost.setDownvotes(1);

        VoteResponseDto result = postService.votePost(samplePostId, VoteAction.DOWN);

        assertThat(result).isNotNull();
        assertThat(result.getUpvotes()).isEqualTo(0);
        assertThat(result.getDownvotes()).isEqualTo(1);
        assertThat(result.getScore()).isEqualTo(-1);
        assertThat(result.getUserVote()).isEqualTo("down");

        verify(postRepository, times(1)).incrementDownvotes(samplePostId);
        verify(postVoteRepository, times(1)).save(any(PostVote.class));
    }

    @Test
    @DisplayName("Should toggle off existing upvote when upvote is clicked again")
    void votePostToggleOffUpvoteDeletesVote() {
        PostVote existingVote = new PostVote();
        existingVote.setPost(samplePost);
        existingVote.setUser(sampleUser);
        existingVote.setVoteType(VoteType.UPVOTE);

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.of(existingVote));

        samplePost.setUpvotes(0);
        samplePost.setDownvotes(0);

        VoteResponseDto result = postService.votePost(samplePostId, VoteAction.UP);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(0);
        assertThat(result.getUserVote()).isEqualTo("up");
        verify(postRepository, times(1)).decrementUpvotes(samplePostId);
        verify(postVoteRepository, times(1)).delete(existingVote);
        verify(postVoteRepository, never()).save(any(PostVote.class));
    }

    @Test
    @DisplayName("Should switch vote from upvote to downvote")
    void votePostSwitchUpvoteToDownvoteSuccess() {
        PostVote existingVote = new PostVote();
        existingVote.setPost(samplePost);
        existingVote.setUser(sampleUser);
        existingVote.setVoteType(VoteType.UPVOTE);

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.of(existingVote));

        samplePost.setUpvotes(0);
        samplePost.setDownvotes(1);

        VoteResponseDto result = postService.votePost(samplePostId, VoteAction.DOWN);

        assertThat(result).isNotNull();
        assertThat(result.getUserVote()).isEqualTo("down");
        verify(postRepository, times(1)).decrementUpvotes(samplePostId);
        verify(postRepository, times(1)).incrementDownvotes(samplePostId);
        verify(postVoteRepository, times(1)).save(existingVote);
    }

    @Test
    @DisplayName("Should return null when voting NONE with no existing vote")
    void votePostActionNoneWithNoExistingVoteReturnsNull() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.empty());

        VoteResponseDto result = postService.votePost(samplePostId, VoteAction.NONE);

        assertThat(result).isNull();
        verify(postVoteRepository, never()).save(any(PostVote.class));
        verify(postVoteRepository, never()).delete(any(PostVote.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when voting on a deleted post")
    void votePostDeletedThrowsIllegalStateException() {
        samplePost.setDeleted(true);
        Post maskedPost = new Post();
        maskedPost.setId(samplePostId);
        maskedPost.setDeleted(true);

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(postMapper.clone(samplePost)).thenReturn(maskedPost);

        assertThatThrownBy(() -> postService.votePost(samplePostId, VoteAction.UP))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot vote on a deleted post");

        verify(postVoteRepository, never()).findByPostAndUser(any(), any());
    }

    @Test
    @DisplayName("Should return active post by ID")
    void findByIdSuccess() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));

        Post result = postService.findById(samplePostId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(samplePostId);
        assertThat(result.getTitle()).isEqualTo("Sample Post Title");
    }

    @Test
    @DisplayName("Should return masked post by ID when post is deleted")
    void findByIdDeletedReturnsMaskedPost() {
        samplePost.setDeleted(true);
        Post clonedPost = new Post();
        clonedPost.setId(samplePostId);

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(postMapper.clone(samplePost)).thenReturn(clonedPost);

        Post result = postService.findById(samplePostId);

        assertThat(result).isNotNull();
        assertThat(result.isDeleted()).isTrue();
        assertThat(result.getTitle()).isEqualTo("[DELETED]");
        assertThat(result.getContent()).isEqualTo("[DELETED]");
        assertThat(result.getImageUrl()).isNull();
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when post ID does not exist")
    void findByIdNotFoundThrowsPostNotFoundException() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.findById(samplePostId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post not found with id=" + samplePostId);
    }

    @Test
    @DisplayName("Should return all posts filtering out deleted posts without active comments")
    void getAllPostsSuccess() {
        Post activePost = new Post();
        activePost.setId(UUID.randomUUID());
        activePost.setDeleted(false);

        Post deletedPostWithoutComments = new Post();
        deletedPostWithoutComments.setId(UUID.randomUUID());
        deletedPostWithoutComments.setDeleted(true);

        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(activePost, deletedPostWithoutComments));
        when(commentRepository.existsByPostIdAndDeletedIsFalse(deletedPostWithoutComments.getId())).thenReturn(false);

        List<Post> result = postService.getAllPosts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activePost.getId());
    }

    @Test
    @DisplayName("Should return posts by subreddit name when subreddit exists")
    void getAllPostsBySubredditSuccess() {
        Post postInSubreddit = new Post();
        postInSubreddit.setId(UUID.randomUUID());
        postInSubreddit.setDeleted(false);

        sampleSubreddit.setPosts(new ArrayList<>(List.of(postInSubreddit)));

        when(subredditService.findByName("gaming")).thenReturn(sampleSubreddit);

        List<Post> result = postService.getAllPosts("gaming");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(postInSubreddit.getId());
    }

    @Test
    @DisplayName("Should fallback to getAllPosts when subreddit name is blank")
    void getAllPostsBlankSubredditDelegatesToGetAllPosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(samplePost));

        List<Post> result = postService.getAllPosts("   ");

        assertThat(result).hasSize(1);
        verify(subredditService, never()).findByName(anyString());
    }

    @Test
    @DisplayName("Should successfully update post when user is the author and post is active and publish PostUpdatedEvent")
    void updatePostSuccess() {
        PostUpdateDto updateDto = new PostUpdateDto();
        updateDto.setTitle("Updated Title");
        updateDto.setContent("Updated clean content");

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(profanityFilterService.censor("Updated clean content")).thenReturn("Updated clean content");
        when(postRepository.save(samplePost)).thenReturn(samplePost);

        Post result = postService.updatePost(samplePostId, updateDto);

        assertThat(result).isNotNull();
        assertThat(samplePost.getTitle()).isEqualTo("Updated Title");
        assertThat(samplePost.getContent()).isEqualTo("Updated clean content");
        verify(eventPublisher, times(1)).publishEvent(any(PostUpdatedEvent.class));
        verify(postRepository, times(1)).save(samplePost);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author attempts to update post")
    void updatePostNotAuthorThrowsAccessDeniedException() {
        PostUpdateDto updateDto = new PostUpdateDto();
        updateDto.setTitle("Updated Title");

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> postService.updatePost(samplePostId, updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not allowed to perform this operation");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a deleted post")
    void updatePostDeletedThrowsIllegalStateException() {
        samplePost.setDeleted(true);
        Post maskedPost = new Post();
        maskedPost.setId(samplePostId);
        maskedPost.setAuthor(sampleUser);
        maskedPost.setDeleted(true);

        PostUpdateDto updateDto = new PostUpdateDto();
        updateDto.setTitle("Updated Title");

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(postMapper.clone(samplePost)).thenReturn(maskedPost);
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        assertThatThrownBy(() -> postService.updatePost(samplePostId, updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot edit a deleted post");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should successfully soft-delete post and clean up tldr bot comment")
    void deletePostByIdSuccess() {
        User tldrBot = new User();
        tldrBot.setId(UUID.randomUUID());
        tldrBot.setUsername("tldr_bot");

        Comment tldrComment = new Comment();
        tldrComment.setId(UUID.randomUUID());
        tldrComment.setUser(tldrBot);

        List<Comment> comments = new ArrayList<>();
        comments.add(tldrComment);
        samplePost.setComments(comments);

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postSummaryService.getOrCreateTldrBotUser()).thenReturn(tldrBot);
        when(commentRepository.findByPostIdAndUserId(samplePostId, tldrBot.getId())).thenReturn(tldrComment);

        postService.deletePostById(samplePostId);

        assertThat(samplePost.isDeleted()).isTrue();
        verify(commentRepository, times(1)).delete(tldrComment);
        verify(commentRepository, times(1)).flush();
        verify(postRepository, times(1)).save(samplePost);
        verify(postRepository, times(1)).flush();
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when deleting non-existent post")
    void deletePostByIdNotFoundThrowsPostNotFoundException() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePostById(samplePostId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post not found with id=" + samplePostId);

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author attempts to delete post")
    void deletePostByIdNotAuthorThrowsAccessDeniedException() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> postService.deletePostById(samplePostId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not the author of this post.");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when deleting an already deleted post")
    void deletePostByIdAlreadyDeletedThrowsIllegalStateException() {
        samplePost.setDeleted(true);

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        assertThatThrownBy(() -> postService.deletePostById(samplePostId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Post already deleted");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should enrich post DTO with user vote 'up' and calculate comment count and score")
    void getEnrichedPostDtoAuthenticatedWithUpvote() {
        mockAuthentication("testuser");

        samplePost.setUpvotes(10);
        samplePost.setDownvotes(2);

        Comment activeComment = new Comment();
        samplePost.setComments(new ArrayList<>(List.of(activeComment)));

        PostVote vote = new PostVote();
        vote.setVoteType(VoteType.UPVOTE);

        PostResponseDto dto = new PostResponseDto();

        when(postMapper.toDto(samplePost)).thenReturn(dto);
        when(commentService.filteredComment(activeComment)).thenReturn(activeComment);
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postVoteRepository.findByPostAndUser(samplePost, sampleUser)).thenReturn(Optional.of(vote));

        PostResponseDto result = postService.getEnrichedPostDto(samplePost);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(8);
        assertThat(result.getCommentCount()).isEqualTo(1);
        assertThat(result.getUserVote()).isEqualTo("up");
    }

    @Test
    @DisplayName("Should enrich post DTO with author '[deleted]' when author is deleted")
    void getEnrichedPostDtoAuthorDeletedSetsMaskedAuthor() {
        SecurityContextHolder.clearContext();

        sampleUser.setDeleted(true);
        PostResponseDto dto = new PostResponseDto();

        when(postMapper.toDto(samplePost)).thenReturn(dto);

        PostResponseDto result = postService.getEnrichedPostDto(samplePost);

        assertThat(result).isNotNull();
        assertThat(result.getAuthor()).isEqualTo("[deleted]");
        assertThat(result.getUserVote()).isNull();
    }

    @Test
    @DisplayName("Should return null when filteredPost receives null")
    void filteredPostNullReturnsNull() {
        Post result = postService.filteredPost(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return post as is when filteredPost receives non-deleted post")
    void filteredPostActiveReturnsSamePost() {
        Post result = postService.filteredPost(samplePost);

        assertThat(result).isEqualTo(samplePost);
    }

    @Test
    @DisplayName("Should return null when filteredPost receives deleted post without active comments")
    void filteredPostDeletedWithoutActiveCommentsReturnsNull() {
        samplePost.setDeleted(true);
        when(commentRepository.existsByPostIdAndDeletedIsFalse(samplePostId)).thenReturn(false);

        Post result = postService.filteredPost(samplePost);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return masked post when filteredPost receives deleted post with active comments")
    void filteredPostDeletedWithActiveCommentsReturnsMaskedPost() {
        samplePost.setDeleted(true);
        Post cloned = new Post();
        cloned.setId(samplePostId);

        when(commentRepository.existsByPostIdAndDeletedIsFalse(samplePostId)).thenReturn(true);
        when(postMapper.clone(samplePost)).thenReturn(cloned);

        Post result = postService.filteredPost(samplePost);

        assertThat(result).isNotNull();
        assertThat(result.isDeleted()).isTrue();
        assertThat(result.getTitle()).isEqualTo("[DELETED]");
        assertThat(result.getContent()).isEqualTo("[DELETED]");
    }
}