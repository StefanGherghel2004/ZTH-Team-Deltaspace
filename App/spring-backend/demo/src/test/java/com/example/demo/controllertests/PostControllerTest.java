package com.example.demo.controllertests;

import com.example.demo.controller.PostController;
import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.dto.post.PostUpdateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.dto.vote.VoteAction;
import com.example.demo.dto.vote.VoteRequestDto;
import com.example.demo.dto.vote.VoteResponseDto;
import com.example.demo.model.Post;
import com.example.demo.service.ApiResponseService;
import com.example.demo.service.PostService;
import com.example.demo.service.PostShuffleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PostService postService;

    @Mock
    private ApiResponseService apiResponseService;

    @Mock
    private PostShuffleService postShuffleService;

    @InjectMocks
    private PostController postController;

    private UUID samplePostId;
    private Post samplePost;
    private PostResponseDto samplePostResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        samplePostId = UUID.randomUUID();

        samplePost = new Post();
        samplePost.setId(samplePostId);
        samplePost.setTitle("Sample Post Title");
        samplePost.setContent("Sample post content");

        samplePostResponseDto = new PostResponseDto();
        samplePostResponseDto.setId(samplePostId);
        samplePostResponseDto.setTitle("Sample Post Title");
        samplePostResponseDto.setContent("Sample post content");
    }

    @Test
    @DisplayName("POST /posts - Should create a post with multipart form data and return 201 Created")
    void createPostSuccess() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3}
        );

        when(postService.createPost(any(PostCreateDto.class))).thenReturn(samplePost);
        when(postService.getEnrichedPostDto(samplePost)).thenReturn(samplePostResponseDto);

        mockMvc.perform(multipart("/posts")
                        .file(imageFile)
                        .param("title", "Sample Post Title")
                        .param("content", "Sample post content")
                        .param("subreddit", "gaming"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(samplePostId.toString()))
                .andExpect(jsonPath("$.data.title").value("Sample Post Title"))
                .andExpect(jsonPath("$.data.content").value("Sample post content"));

        verify(postService, times(1)).createPost(any(PostCreateDto.class));
        verify(postService, times(1)).getEnrichedPostDto(samplePost);
    }

    @Test
    @DisplayName("GET /posts/{id} - Should return post details and 200 OK")
    void getPostByIdSuccess() throws Exception {
        when(postService.findById(samplePostId)).thenReturn(samplePost);
        when(postService.getEnrichedPostDto(samplePost)).thenReturn(samplePostResponseDto);

        mockMvc.perform(get("/posts/{id}", samplePostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(samplePostId.toString()))
                .andExpect(jsonPath("$.data.title").value("Sample Post Title"));

        verify(postService, times(1)).findById(samplePostId);
        verify(postService, times(1)).getEnrichedPostDto(samplePost);
    }

    @Test
    @DisplayName("GET /posts - Should return list of shuffled posts and 200 OK")
    void getPostsSuccess() throws Exception {
        List<Post> postList = List.of(samplePost);
        List<PostResponseDto> responseList = List.of(samplePostResponseDto);

        when(postShuffleService.getShuffledPosts("gaming")).thenReturn(postList);
        when(apiResponseService.getPostListResponse(postList)).thenReturn(responseList);

        mockMvc.perform(get("/posts")
                        .param("subreddit", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(samplePostId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Sample Post Title"));

        verify(postShuffleService, times(1)).getShuffledPosts("gaming");
        verify(apiResponseService, times(1)).getPostListResponse(postList);
    }

    @Test
    @DisplayName("GET /subreddits/{name}/posts - Should return posts belonging to subreddit and 200 OK")
    void getPostsBySubredditSuccess() throws Exception {
        List<Post> postList = List.of(samplePost);
        List<PostResponseDto> responseList = List.of(samplePostResponseDto);

        when(postService.getAllPosts("gaming")).thenReturn(postList);
        when(apiResponseService.getPostListResponse(postList)).thenReturn(responseList);

        mockMvc.perform(get("/subreddits/{name}/posts", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(samplePostId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Sample Post Title"));

        verify(postService, times(1)).getAllPosts("gaming");
        verify(apiResponseService, times(1)).getPostListResponse(postList);
    }

    @Test
    @DisplayName("PUT /posts/{id}/vote - Should register vote and return 200 OK")
    void votePostSuccess() throws Exception {
        VoteRequestDto voteDto = new VoteRequestDto();
        voteDto.setVoteType(VoteAction.UP);

        VoteResponseDto voteResponse = new VoteResponseDto();
        voteResponse.setUpvotes(1);
        voteResponse.setDownvotes(0);
        voteResponse.setScore(1);
        voteResponse.setUserVote("up");

        when(postService.votePost(samplePostId, VoteAction.UP)).thenReturn(voteResponse);

        mockMvc.perform(put("/posts/{id}/vote", samplePostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.upvotes").value(1))
                .andExpect(jsonPath("$.data.score").value(1))
                .andExpect(jsonPath("$.data.userVote").value("up"));

        verify(postService, times(1)).votePost(samplePostId, VoteAction.UP);
    }

    @Test
    @DisplayName("PUT /posts/{id} - Should update post and return 200 OK")
    void updatePostSuccess() throws Exception {
        PostUpdateDto updateDto = new PostUpdateDto();
        updateDto.setTitle("Updated Title");
        updateDto.setContent("Updated content");

        Post updatedPost = new Post();
        updatedPost.setId(samplePostId);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated content");

        PostResponseDto updatedResponseDto = new PostResponseDto();
        updatedResponseDto.setId(samplePostId);
        updatedResponseDto.setTitle("Updated Title");
        updatedResponseDto.setContent("Updated content");

        when(postService.updatePost(eq(samplePostId), any(PostUpdateDto.class))).thenReturn(updatedPost);
        when(postService.getEnrichedPostDto(updatedPost)).thenReturn(updatedResponseDto);

        mockMvc.perform(put("/posts/{id}", samplePostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(samplePostId.toString()))
                .andExpect(jsonPath("$.data.title").value("Updated Title"))
                .andExpect(jsonPath("$.data.content").value("Updated content"));

        verify(postService, times(1)).updatePost(eq(samplePostId), any(PostUpdateDto.class));
        verify(postService, times(1)).getEnrichedPostDto(updatedPost);
    }

    @Test
    @DisplayName("DELETE /posts/{id} - Should delete post and return success message with 200 OK")
    void deletePostByIdSuccess() throws Exception {
        doNothing().when(postService).deletePostById(samplePostId);

        mockMvc.perform(delete("/posts/{id}", samplePostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post deleted successfully"));

        verify(postService, times(1)).deletePostById(samplePostId);
    }
}