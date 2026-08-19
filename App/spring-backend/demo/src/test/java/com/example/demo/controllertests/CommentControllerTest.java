package com.example.demo.controllertests;

import com.example.demo.controller.CommentController;
import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.CommentUpdateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteRequestDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommentControllerTest {

    private MockMvc mockMvc;
    private AutoCloseable closeableMocks;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private UUID samplePostId;
    private UUID sampleCommentId;
    private Comment sampleComment;
    private CommentResponseDto sampleCommentResponseDto;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        samplePostId = UUID.randomUUID();
        sampleCommentId = UUID.randomUUID();

        sampleComment = new Comment();
        sampleComment.setId(sampleCommentId);
        sampleComment.setContent("Sample comment content");

        sampleCommentResponseDto = new CommentResponseDto();
        sampleCommentResponseDto.setId(sampleCommentId);
        sampleCommentResponseDto.setContent("Sample comment content");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeableMocks != null) {
            closeableMocks.close();
        }
    }

    @Test
    @DisplayName("POST /posts/{postId}/comments - Should create a comment and return 201 Created")
    void addComment_Success() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto("Sample comment content",null);
        when(commentService.addComment(any(CommentCreateDto.class), eq(samplePostId))).thenReturn(sampleCommentResponseDto);

        mockMvc.perform(post("/posts/{postId}/comments", samplePostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(sampleCommentId.toString()))
                .andExpect(jsonPath("$.data.content").value("Sample comment content"));

        verify(commentService, times(1)).addComment(any(CommentCreateDto.class), eq(samplePostId));
    }

    @Test
    @DisplayName("GET /comments/{id} - Should return comment by id and 200 OK")
    void getCommentById_Success() throws Exception {
        when(commentService.findById(sampleCommentId)).thenReturn(sampleComment);
        when(commentService.getEnrichedCommentDto(sampleComment)).thenReturn(sampleCommentResponseDto);

        mockMvc.perform(get("/comments/{id}", sampleCommentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(sampleCommentId.toString()))
                .andExpect(jsonPath("$.data.content").value("Sample comment content"));

        verify(commentService, times(1)).findById(sampleCommentId);
        verify(commentService, times(1)).getEnrichedCommentDto(sampleComment);
    }

    @Test
    @DisplayName("GET /posts/{postId}/comments - Should return top-level comments and 200 OK")
    void getComments_Success() throws Exception {
        List<Comment> commentList = List.of(sampleComment);

        when(commentService.getTopLevelCommentsByPostId(samplePostId)).thenReturn(commentList);
        when(commentService.getEnrichedCommentDto(sampleComment)).thenReturn(sampleCommentResponseDto);
        when(commentService.countCommentsByPostId(samplePostId)).thenReturn(1);

        mockMvc.perform(get("/posts/{postId}/comments", samplePostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(sampleCommentId.toString()))
                .andExpect(jsonPath("$.data[0].content").value("Sample comment content"));

        verify(commentService, times(1)).getTopLevelCommentsByPostId(samplePostId);
        verify(commentService, times(1)).getEnrichedCommentDto(sampleComment);
        verify(commentService, times(1)).countCommentsByPostId(samplePostId);
    }

    @Test
    @DisplayName("DELETE /comments/{id} - Should delete comment and return success message with 200 OK")
    void deleteComment_Success() throws Exception {
        doNothing().when(commentService).deleteCommentById(sampleCommentId);

        mockMvc.perform(delete("/comments/{id}", sampleCommentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment was deleted successfully!"));

        verify(commentService, times(1)).deleteCommentById(sampleCommentId);
    }

    @Test
    @DisplayName("PUT /comments/{id} - Should update comment and return 200 OK")
    void updateComment_Success() throws Exception {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("Updated comment content");

        CommentResponseDto updatedResponseDto = new CommentResponseDto();
        updatedResponseDto.setId(sampleCommentId);
        updatedResponseDto.setContent("Updated comment content");

        when(commentService.editComment(eq(sampleCommentId), any(CommentUpdateDto.class))).thenReturn(updatedResponseDto);

        mockMvc.perform(put("/comments/{id}", sampleCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(sampleCommentId.toString()))
                .andExpect(jsonPath("$.data.content").value("Updated comment content"));

        verify(commentService, times(1)).editComment(eq(sampleCommentId), any(CommentUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /comments/{id}/vote - Should register vote on comment and return 200 OK")
    void voteComment_Success() throws Exception {
        VoteRequestDto voteDto = new VoteRequestDto();
        voteDto.setVoteType(VoteAction.UP);

        VoteResponseDto voteResponse = new VoteResponseDto();
        voteResponse.setUpvotes(1);
        voteResponse.setDownvotes(0);
        voteResponse.setScore(1);
        voteResponse.setUserVote("up");

        when(commentService.voteComment(sampleCommentId, VoteAction.UP)).thenReturn(voteResponse);

        mockMvc.perform(put("/comments/{id}/vote", sampleCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.upvotes").value(1))
                .andExpect(jsonPath("$.data.score").value(1))
                .andExpect(jsonPath("$.data.userVote").value("up"));

        verify(commentService, times(1)).voteComment(sampleCommentId, VoteAction.UP);
    }
}