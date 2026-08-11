package com.example.demo.controller;

import com.example.demo.dto.subreddit.SubredditCreateDto;
import com.example.demo.dto.subreddit.SubredditUpdateDto;
import com.example.demo.dto.subreddit.response.SubredditResponseDto;
import com.example.demo.mapper.SubredditMapper;
import com.example.demo.model.Subreddit;
import com.example.demo.repository.SubredditRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.SubredditService;
import com.example.demo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subreddits")
@RequiredArgsConstructor
// todo rename subreddit to subreddit
public class SubredditController {
    private final SubredditService subredditService;
    // todo delete junk
    private final SubredditMapper subredditMapper;
    private final SubredditRepository subredditRepository;
    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<SubredditResponseDto>> addSubreddit(@Valid @RequestBody SubredditCreateDto createDto){
        SubredditResponseDto responseDto = subredditService.addSubreddit(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubredditResponseDto>>> getSubreddits(){
        List<Subreddit> communities;

        communities = subredditService.listAllSubreddits();

        List<SubredditResponseDto> response = communities.stream()
                .map(subredditService::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response,response.size()));

    }
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<SubredditResponseDto>> getSubredditByName(@PathVariable String name){
        Subreddit findByNameSubreddit = subredditService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(subredditService.toDto(findByNameSubreddit)));
    }


    @DeleteMapping("/{subredditName}")
    public ResponseEntity<ApiResponse<Void>> deleteSubredditByName(@PathVariable String subredditName){
        subredditService.deleteSubredditByName(subredditName);
        return ResponseEntity.ok(ApiResponse.successMessage("subreddit deleted successfully!"));
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<SubredditResponseDto>> updateSubreddit(@PathVariable String name, @Valid @RequestBody SubredditUpdateDto updateDto){
        // todo check if u need to add the updated subreddit to the response
        // era void inainte so test daca e ok
        Subreddit subreddit = subredditService.updateSubreddit(name,updateDto);
        SubredditResponseDto responseDto = subredditService.toDto(subreddit);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PostMapping("/{name}/join")
    public ResponseEntity<ApiResponse<Void>> joinSubreddit(
            @PathVariable String name) { // Sau preiei ID-ul userului conectat
// TODO SAU PREIEI ???
        subredditService.joinSubreddit(name);
        return ResponseEntity.ok(ApiResponse.successMessage("You have successfully joined the subreddit!"));
    }

    @PostMapping("/{name}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveSubreddit(
            @PathVariable String name) {

        subredditService.leaveSubreddit(name);
        return ResponseEntity.ok(ApiResponse.successMessage("You have successfully left the subreddit :<"));
    }

}
