package com.example.demo.controllertests;

import com.example.demo.controller.SubredditController;
import com.example.demo.dto.subreddit.SubredditCreateDto;
import com.example.demo.dto.subreddit.SubredditUpdateDto;
import com.example.demo.dto.subreddit.response.SubredditResponseDto;
import com.example.demo.model.Subreddit;
import com.example.demo.service.SubredditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

class SubredditControllerTest {

    private MockMvc mockMvc;
    private SubredditService subredditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Subreddit sampleSubreddit;
    private SubredditResponseDto sampleSubredditResponseDto;

    @BeforeEach
    void setUp() {
        subredditService = mock(SubredditService.class);
        SubredditController subredditController = new SubredditController(subredditService);
        mockMvc = MockMvcBuilders.standaloneSetup(subredditController).build();

        UUID sampleId = UUID.randomUUID();

        sampleSubreddit = new Subreddit();
        sampleSubreddit.setId(sampleId);
        sampleSubreddit.setName("gaming");
        sampleSubreddit.setDescription("Gaming community discussion");

        sampleSubredditResponseDto = new SubredditResponseDto();
        sampleSubredditResponseDto.setId(sampleId);
        sampleSubredditResponseDto.setName("gaming");
        sampleSubredditResponseDto.setDescription("Gaming community discussion");
    }

    @Test
    @DisplayName("POST /subreddits - Should create subreddit and return 201 Created")
    void addSubredditSuccess() throws Exception {
        SubredditCreateDto createDto = new SubredditCreateDto();
        createDto.setName("gaming");
        createDto.setDisplayName("Gaming Community");
        createDto.setDescription("Gaming community discussion");

        when(subredditService.addSubreddit(any(SubredditCreateDto.class))).thenReturn(sampleSubredditResponseDto);

        mockMvc.perform(post("/subreddits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("gaming"))
                .andExpect(jsonPath("$.data.description").value("Gaming community discussion"));

        verify(subredditService, times(1)).addSubreddit(any(SubredditCreateDto.class));
    }

    @Test
    @DisplayName("GET /subreddits - Should return all subreddits and 200 OK")
    void getSubredditsSuccess() throws Exception {
        List<Subreddit> subredditList = List.of(sampleSubreddit);

        when(subredditService.listAllSubreddits()).thenReturn(subredditList);
        when(subredditService.toDto(sampleSubreddit)).thenReturn(sampleSubredditResponseDto);

        mockMvc.perform(get("/subreddits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("gaming"))
                .andExpect(jsonPath("$.data[0].description").value("Gaming community discussion"))
                .andExpect(jsonPath("$.total").value(1));

        verify(subredditService, times(1)).listAllSubreddits();
        verify(subredditService, times(1)).toDto(sampleSubreddit);
    }

    @Test
    @DisplayName("GET /subreddits/{name} - Should return subreddit by name and 200 OK")
    void getSubredditByNameSuccess() throws Exception {
        when(subredditService.findByName("gaming")).thenReturn(sampleSubreddit);
        when(subredditService.toDto(sampleSubreddit)).thenReturn(sampleSubredditResponseDto);

        mockMvc.perform(get("/subreddits/{name}", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("gaming"))
                .andExpect(jsonPath("$.data.description").value("Gaming community discussion"));

        verify(subredditService, times(1)).findByName("gaming");
        verify(subredditService, times(1)).toDto(sampleSubreddit);
    }

    @Test
    @DisplayName("DELETE /subreddits/{subredditName} - Should delete subreddit and return 200 OK")
    void deleteSubredditByNameSuccess() throws Exception {
        doNothing().when(subredditService).deleteSubredditByName("gaming");

        mockMvc.perform(delete("/subreddits/{subredditName}", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("subreddit deleted successfully!"));

        verify(subredditService, times(1)).deleteSubredditByName("gaming");
    }

    @Test
    @DisplayName("PUT /subreddits/{name} - Should update subreddit and return 200 OK")
    void updateSubredditSuccess() throws Exception {
        SubredditUpdateDto updateDto = new SubredditUpdateDto();
        updateDto.setDescription("Updated gaming description");

        Subreddit updatedSubreddit = new Subreddit();
        updatedSubreddit.setId(sampleSubreddit.getId());
        updatedSubreddit.setName("gaming");
        updatedSubreddit.setDescription("Updated gaming description");

        SubredditResponseDto updatedResponseDto = new SubredditResponseDto();
        updatedResponseDto.setId(sampleSubreddit.getId());
        updatedResponseDto.setName("gaming");
        updatedResponseDto.setDescription("Updated gaming description");

        when(subredditService.updateSubreddit(eq("gaming"), any(SubredditUpdateDto.class))).thenReturn(updatedSubreddit);
        when(subredditService.toDto(updatedSubreddit)).thenReturn(updatedResponseDto);

        mockMvc.perform(put("/subreddits/{name}", "gaming")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("gaming"))
                .andExpect(jsonPath("$.data.description").value("Updated gaming description"));

        verify(subredditService, times(1)).updateSubreddit(eq("gaming"), any(SubredditUpdateDto.class));
        verify(subredditService, times(1)).toDto(updatedSubreddit);
    }

    @Test
    @DisplayName("POST /subreddits/{name}/join - Should join subreddit and return 200 OK")
    void joinSubredditSuccess() throws Exception {
        doNothing().when(subredditService).joinSubreddit("gaming");

        mockMvc.perform(post("/subreddits/{name}/join", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You have successfully joined the subreddit!"));

        verify(subredditService, times(1)).joinSubreddit("gaming");
    }

    @Test
    @DisplayName("POST /subreddits/{name}/leave - Should leave subreddit and return 200 OK")
    void leaveSubredditSuccess() throws Exception {
        doNothing().when(subredditService).leaveSubreddit("gaming");

        mockMvc.perform(post("/subreddits/{name}/leave", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You have successfully left the subreddit :<"));

        verify(subredditService, times(1)).leaveSubreddit("gaming");
    }
}