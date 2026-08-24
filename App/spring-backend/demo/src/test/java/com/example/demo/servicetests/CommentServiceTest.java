package com.example.demo.servicetests;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.notfound.CommentNotFoundException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.CommentVote;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.model.enums.VoteType;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentVoteRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.EmojiFormatterService;
import com.example.demo.service.ProfanityFilterService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ProfanityFilterService profanityFilterService;

    @Mock
    private EmojiFormatterService emojiFormatterService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentVoteRepository commentVoteRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private User sampleUser;
    private User otherUser;
    private Post samplePost;
    private Comment sampleComment;
    private UUID sampleCommentId;
    private UUID samplePostId;

    @BeforeEach
    void setUp() {
        sampleCommentId = UUID.randomUUID();
        samplePostId = UUID.randomUUID();

        sampleUser = new User();
        sampleUser.setId(UUID.randomUUID());
        sampleUser.setUsername("testuser");
        sampleUser.setDeleted(false);

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("otheruser");
        otherUser.setDeleted(false);

        samplePost = new Post();
        samplePost.setId(samplePostId);
        samplePost.setTitle("Sample Post");
        samplePost.setContent("Sample Post Content");
        samplePost.setAuthor(sampleUser);
        samplePost.setDeleted(false);

        sampleComment = Comment.builder()
                .id(sampleCommentId)
                .content("Valid comment content")
                .user(sampleUser)
                .post(samplePost)
                .upvotes(0)
                .downvotes(0)
                .deleted(false)
                .build();

        lenient().when(emojiFormatterService.format(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
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
    @DisplayName("Should successfully add a top-level comment and upvote it")
    void addCommentTopLevelSuccess() {
        CommentCreateDto dto = new CommentCreateDto("Great post!", null);

        CommentResponseDto responseDto = new CommentResponseDto();

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(profanityFilterService.censor("Great post!")).thenReturn("Great post!");
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(commentVoteRepository.findByCommentAndUser(any(Comment.class), eq(sampleUser))).thenReturn(Optional.empty());
        when(userService.maskIfDeleted(sampleUser)).thenReturn(sampleUser);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(responseDto);
        when(commentRepository.findByParentCommentId(sampleCommentId)).thenReturn(new ArrayList<>());

        CommentResponseDto result = commentService.addComment(dto, samplePostId);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(1);
        assertThat(sampleComment.getUpvotes()).isEqualTo(1);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentVoteRepository, times(1)).save(any(CommentVote.class));
    }

    @Test
    @DisplayName("Should successfully add a reply to an existing parent comment")
    void addCommentReplySuccess() {
        UUID parentCommentId = UUID.randomUUID();
        Comment parentComment = Comment.builder()
                .id(parentCommentId)
                .content("Parent content")
                .user(otherUser)
                .post(samplePost)
                .deleted(false)
                .build();

        CommentCreateDto dto = new CommentCreateDto("Replying to you", parentCommentId);

        Comment savedReply = Comment.builder()
                .id(UUID.randomUUID())
                .content("Replying to you")
                .user(sampleUser)
                .post(samplePost)
                .parentComment(parentComment)
                .upvotes(0)
                .downvotes(0)
                .deleted(false)
                .build();

        CommentResponseDto responseDto = new CommentResponseDto();

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(profanityFilterService.censor("Replying to you")).thenReturn("Replying to you");
        when(commentRepository.save(any(Comment.class))).thenReturn(savedReply);
        when(commentRepository.findById(savedReply.getId())).thenReturn(Optional.of(savedReply));
        when(commentVoteRepository.findByCommentAndUser(any(Comment.class), eq(sampleUser))).thenReturn(Optional.empty());
        when(userService.maskIfDeleted(sampleUser)).thenReturn(sampleUser);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(responseDto);
        when(commentRepository.findByParentCommentId(savedReply.getId())).thenReturn(new ArrayList<>());

        CommentResponseDto result = commentService.addComment(dto, samplePostId);

        assertThat(result).isNotNull();
        assertThat(result.getParentId()).isEqualTo(parentCommentId);
        verify(commentRepository, times(1)).findById(parentCommentId);
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when adding comment to non-existent post")
    void addCommentPostNotFoundThrowsPostNotFoundException() {
        CommentCreateDto dto = new CommentCreateDto("Comment on missing post", null);

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postRepository.findById(samplePostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(dto, samplePostId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with id " + samplePostId + " was not found.");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when replying to a non-existent parent comment")
    void addCommentParentNotFoundThrowsCommentNotFoundException() {
        UUID missingParentId = UUID.randomUUID();
        CommentCreateDto dto = new CommentCreateDto("Reply to ghost", missingParentId);

        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(profanityFilterService.censor("Reply to ghost")).thenReturn("Reply to ghost");
        when(commentRepository.findById(missingParentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.addComment(dto, samplePostId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Parent comment with id: " + missingParentId + " was not found.");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should return active comment when found by ID")
    void findByIdSuccess() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));

        Comment result = commentService.findById(sampleCommentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sampleCommentId);
        assertThat(result.getContent()).isEqualTo("Valid comment content");
    }

    @Test
    @DisplayName("Should return masked comment when comment found by ID is deleted")
    void findByIdDeletedReturnsMaskedComment() {
        sampleComment.setDeleted(true);
        Comment clonedMasked = Comment.builder()
                .id(sampleCommentId)
                .user(sampleUser)
                .build();

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(commentMapper.clone(sampleComment)).thenReturn(clonedMasked);

        Comment result = commentService.findById(sampleCommentId);

        assertThat(result).isNotNull();
        assertThat(result.isDeleted()).isTrue();
        assertThat(result.getContent()).isEqualTo("[DELETED]");
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when comment does not exist by ID")
    void findByIdNotFoundThrowsCommentNotFoundException() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.findById(sampleCommentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Comment with id: " + sampleCommentId + " was not found.");
    }

    @Test
    @DisplayName("Should successfully soft-delete comment when caller is the author")
    void deleteCommentByIdSuccess() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        commentService.deleteCommentById(sampleCommentId);

        assertThat(sampleComment.isDeleted()).isTrue();
        verify(commentRepository, times(1)).save(sampleComment);
        verify(commentRepository, times(1)).flush();
    }

    @Test
    @DisplayName("Should throw IllegalStateException when deleting an already deleted comment")
    void deleteCommentByIdAlreadyDeletedThrowsIllegalStateException() {
        sampleComment.setDeleted(true);
        Comment clonedMasked = Comment.builder().id(sampleCommentId).deleted(true).build();

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(commentMapper.clone(sampleComment)).thenReturn(clonedMasked);

        assertThatThrownBy(() -> commentService.deleteCommentById(sampleCommentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Comment is already deleted");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author attempts to delete comment")
    void deleteCommentByIdNotAuthorThrowsAccessDeniedException() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> commentService.deleteCommentById(sampleCommentId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not the author of this comment");

        assertThat(sampleComment.isDeleted()).isFalse();
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should successfully edit comment when caller is author and comment is active")
    void editCommentSuccess() {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("Updated content with profanity filtered");

        CommentResponseDto responseDto = new CommentResponseDto();

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(profanityFilterService.censor("Updated content with profanity filtered")).thenReturn("Updated content with profanity filtered");
        when(commentRepository.save(sampleComment)).thenReturn(sampleComment);
        when(userService.maskIfDeleted(sampleUser)).thenReturn(sampleUser);
        when(commentMapper.toDto(sampleComment)).thenReturn(responseDto);
        when(commentRepository.findByParentCommentId(sampleCommentId)).thenReturn(new ArrayList<>());

        CommentResponseDto result = commentService.editComment(sampleCommentId, updateDto);

        assertThat(result).isNotNull();
        assertThat(sampleComment.getContent()).isEqualTo("Updated content with profanity filtered");
        verify(commentRepository, times(1)).save(sampleComment);
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when editing non-existent comment")
    void editCommentNotFoundThrowsCommentNotFoundException() {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("New text");

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.editComment(sampleCommentId, updateDto))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Comment with id: " + sampleCommentId + " was not found.");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-author attempts to edit comment")
    void editCommentNotAuthorThrowsAccessDeniedException() {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("Unauthorized update");

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> commentService.editComment(sampleCommentId, updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not the author of this comment");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when editing a deleted comment")
    void editCommentDeletedThrowsIllegalStateException() {
        sampleComment.setDeleted(true);
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("Trying to edit deleted");

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);

        assertThatThrownBy(() -> commentService.editComment(sampleCommentId, updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot edit a deleted comment");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should successfully register a new upvote on comment")
    void voteCommentNewUpvoteSuccess() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.empty());

        VoteResponseDto result = commentService.voteComment(sampleCommentId, VoteAction.UP);

        assertThat(result).isNotNull();
        assertThat(result.getUpvotes()).isEqualTo(1);
        assertThat(result.getDownvotes()).isEqualTo(0);
        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getUserVote()).isEqualTo("up");
        verify(commentVoteRepository, times(1)).save(any(CommentVote.class));
    }

    @Test
    @DisplayName("Should successfully register a new downvote on comment")
    void voteCommentNewDownvoteSuccess() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.empty());

        VoteResponseDto result = commentService.voteComment(sampleCommentId, VoteAction.DOWN);

        assertThat(result).isNotNull();
        assertThat(result.getUpvotes()).isEqualTo(0);
        assertThat(result.getDownvotes()).isEqualTo(1);
        assertThat(result.getScore()).isEqualTo(-1);
        assertThat(result.getUserVote()).isEqualTo("down");
        verify(commentVoteRepository, times(1)).save(any(CommentVote.class));
    }

    @Test
    @DisplayName("Should toggle off upvote when user clicks upvote again")
    void voteCommentToggleOffUpvoteDeletesVote() {
        sampleComment.setUpvotes(1);
        CommentVote existingVote = new CommentVote();
        existingVote.setComment(sampleComment);
        existingVote.setUser(sampleUser);
        existingVote.setVoteType(VoteType.UPVOTE);

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.of(existingVote));

        VoteResponseDto result = commentService.voteComment(sampleCommentId, VoteAction.UP);

        assertThat(result).isNotNull();
        assertThat(result.getUpvotes()).isEqualTo(0);
        assertThat(result.getScore()).isEqualTo(0);
        assertThat(result.getUserVote()).isEqualTo("up");
        verify(commentVoteRepository, times(1)).delete(existingVote);
        verify(commentVoteRepository, never()).save(any(CommentVote.class));
    }

    @Test
    @DisplayName("Should switch vote from upvote to downvote")
    void voteCommentSwitchUpvoteToDownvoteSuccess() {
        sampleComment.setUpvotes(1);
        sampleComment.setDownvotes(0);
        CommentVote existingVote = new CommentVote();
        existingVote.setComment(sampleComment);
        existingVote.setUser(sampleUser);
        existingVote.setVoteType(VoteType.UPVOTE);

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.of(existingVote));

        VoteResponseDto result = commentService.voteComment(sampleCommentId, VoteAction.DOWN);

        assertThat(result).isNotNull();
        assertThat(result.getUpvotes()).isEqualTo(0);
        assertThat(result.getDownvotes()).isEqualTo(1);
        assertThat(result.getScore()).isEqualTo(-1);
        assertThat(result.getUserVote()).isEqualTo("down");
        verify(commentVoteRepository, times(1)).save(existingVote);
    }

    @Test
    @DisplayName("Should return null when user performs VoteAction.NONE with no prior vote")
    void voteCommentNoneWithNoPriorVoteReturnsNull() {
        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.empty());

        VoteResponseDto result = commentService.voteComment(sampleCommentId, VoteAction.NONE);

        assertThat(result).isNull();
        verify(commentVoteRepository, never()).save(any(CommentVote.class));
        verify(commentVoteRepository, never()).delete(any(CommentVote.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when voting on a deleted comment")
    void voteCommentDeletedThrowsIllegalStateException() {
        sampleComment.setDeleted(true);
        Comment clonedMasked = Comment.builder().id(sampleCommentId).deleted(true).build();

        when(commentRepository.findById(sampleCommentId)).thenReturn(Optional.of(sampleComment));
        when(commentMapper.clone(sampleComment)).thenReturn(clonedMasked);

        assertThatThrownBy(() -> commentService.voteComment(sampleCommentId, VoteAction.UP))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot vote on a deleted comment");

        verify(commentVoteRepository, never()).findByCommentAndUser(any(), any());
    }

    @Test
    @DisplayName("Should return top level comments for a post filtering out deleted with no active replies")
    void getTopLevelCommentsByPostIdSuccess() {
        Comment activeComment = Comment.builder().id(UUID.randomUUID()).deleted(false).build();
        Comment deletedWithNoReplies = Comment.builder().id(UUID.randomUUID()).deleted(true).build();

        when(postRepository.findById(samplePostId)).thenReturn(Optional.of(samplePost));
        when(commentRepository.findByPostIdAndParentCommentIdIsNull(samplePostId))
                .thenReturn(List.of(activeComment, deletedWithNoReplies));
        when(commentRepository.existsByParentCommentIdAndDeletedIsFalse(deletedWithNoReplies.getId())).thenReturn(false);

        List<Comment> result = commentService.getTopLevelCommentsByPostId(samplePostId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activeComment.getId());
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when fetching top level comments for non-existent post")
    void getTopLevelCommentsByPostIdNotFoundThrowsPostNotFoundException() {
        when(postRepository.findById(samplePostId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getTopLevelCommentsByPostId(samplePostId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post with id: " + samplePostId + " was not found.");

        verify(commentRepository, never()).findByPostIdAndParentCommentIdIsNull(any());
    }

    @Test
    @DisplayName("Should enrich comment DTO with userVote 'up' when user has active upvote")
    void getEnrichedCommentDtoAuthenticatedWithUpvote() {
        mockAuthentication("testuser");

        sampleComment.setUpvotes(5);
        sampleComment.setDownvotes(1);

        CommentVote vote = new CommentVote();
        vote.setVoteType(VoteType.UPVOTE);

        CommentResponseDto baseDto = new CommentResponseDto();

        when(userService.maskIfDeleted(sampleUser)).thenReturn(sampleUser);
        when(commentMapper.toDto(sampleComment)).thenReturn(baseDto);
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.of(vote));
        when(commentRepository.findByParentCommentId(sampleCommentId)).thenReturn(new ArrayList<>());

        CommentResponseDto result = commentService.getEnrichedCommentDto(sampleComment);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(4);
        assertThat(result.getUserVote()).isEqualTo("up");
    }

    @Test
    @DisplayName("Should enrich comment DTO with null userVote when unauthenticated")
    void getEnrichedCommentDtoUnauthenticatedSetsUserVoteNull() {
        SecurityContextHolder.clearContext();

        sampleComment.setUpvotes(3);
        sampleComment.setDownvotes(0);

        CommentResponseDto baseDto = new CommentResponseDto();

        when(userService.maskIfDeleted(sampleUser)).thenReturn(sampleUser);
        when(commentMapper.toDto(sampleComment)).thenReturn(baseDto);
        when(commentRepository.findByParentCommentId(sampleCommentId)).thenReturn(new ArrayList<>());

        CommentResponseDto result = commentService.getEnrichedCommentDto(sampleComment);

        assertThat(result).isNotNull();
        assertThat(result.getUserVote()).isNull();
        assertThat(result.getScore()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should enrich comment DTO recursively with replies")
    void getEnrichedCommentDtoWithNestedReplies() {
        mockAuthentication("testuser");

        Comment reply = Comment.builder()
                .id(UUID.randomUUID())
                .content("Reply text")
                .user(otherUser)
                .post(samplePost)
                .parentComment(sampleComment)
                .upvotes(1)
                .downvotes(0)
                .deleted(false)
                .build();

        CommentResponseDto parentDto = new CommentResponseDto();
        CommentResponseDto replyDto = new CommentResponseDto();

        when(userService.maskIfDeleted(sampleUser)).thenReturn(sampleUser);
        when(userService.maskIfDeleted(otherUser)).thenReturn(otherUser);
        when(commentMapper.toDto(sampleComment)).thenReturn(parentDto);
        when(commentMapper.toDto(reply)).thenReturn(replyDto);
        when(userService.getAuthenticatedUser()).thenReturn(sampleUser);
        when(commentVoteRepository.findByCommentAndUser(sampleComment, sampleUser)).thenReturn(Optional.empty());
        when(commentVoteRepository.findByCommentAndUser(reply, sampleUser)).thenReturn(Optional.empty());

        when(commentRepository.findByParentCommentId(sampleCommentId)).thenReturn(List.of(reply));
        when(commentRepository.findByParentCommentId(reply.getId())).thenReturn(new ArrayList<>());

        CommentResponseDto result = commentService.getEnrichedCommentDto(sampleComment);

        assertThat(result).isNotNull();
        assertThat(result.getReplies()).hasSize(1);
        assertThat(result.getReplies().get(0).getParentId()).isEqualTo(sampleCommentId);
    }

    @Test
    @DisplayName("Should return null when filteredComment receives null")
    void filteredCommentNullReturnsNull() {
        Comment result = commentService.filteredComment(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return comment as is when filteredComment receives non-deleted comment")
    void filteredCommentActiveReturnsSameComment() {
        Comment result = commentService.filteredComment(sampleComment);

        assertThat(result).isEqualTo(sampleComment);
    }

    @Test
    @DisplayName("Should return null when filteredComment receives deleted comment with no active replies")
    void filteredCommentDeletedWithoutActiveRepliesReturnsNull() {
        sampleComment.setDeleted(true);
        when(commentRepository.existsByParentCommentIdAndDeletedIsFalse(sampleCommentId)).thenReturn(false);

        Comment result = commentService.filteredComment(sampleComment);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return masked comment when filteredComment receives deleted comment with active replies")
    void filteredCommentDeletedWithActiveRepliesReturnsMaskedComment() {
        sampleComment.setDeleted(true);
        Comment cloned = Comment.builder().id(sampleCommentId).user(sampleUser).build();

        when(commentRepository.existsByParentCommentIdAndDeletedIsFalse(sampleCommentId)).thenReturn(true);
        when(commentMapper.clone(sampleComment)).thenReturn(cloned);

        Comment result = commentService.filteredComment(sampleComment);

        assertThat(result).isNotNull();
        assertThat(result.isDeleted()).isTrue();
        assertThat(result.getContent()).isEqualTo("[DELETED]");
    }

    @Test
    @DisplayName("Should return total comment count for given post ID")
    void countCommentsByPostIdSuccess() {
        when(commentRepository.countByPostId(samplePostId)).thenReturn(5);

        int count = commentService.countCommentsByPostId(samplePostId);

        assertThat(count).isEqualTo(5);
        verify(commentRepository, times(1)).countByPostId(samplePostId);
    }
}