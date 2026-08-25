package com.example.demo.controller;

import com.example.demo.annotation.RateLimit;
import com.example.demo.annotation.RequireVerified;
import com.example.demo.dto.subreddit.SubredditCreateDto;
import com.example.demo.dto.subreddit.SubredditUpdateDto;
import com.example.demo.dto.subreddit.response.SubredditResponseDto;
import com.example.demo.model.Subreddit;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.SubredditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subreddits")
@RequiredArgsConstructor

public class SubredditController {
    private final SubredditService subredditService;

    @RateLimit(requests = 25)
    @RequireVerified
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<SubredditResponseDto>> addSubreddit(@Valid @RequestBody SubredditCreateDto createDto){
        SubredditResponseDto responseDto = subredditService.addSubreddit(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @RateLimit(requests = 100)
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubredditResponseDto>>> getSubreddits(){
        List<Subreddit> communities;

        communities = subredditService.listAllSubreddits();

        List<SubredditResponseDto> response = communities.stream()
                .map(subredditService::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response,response.size()));

    }

    @RateLimit(requests = 100)
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<SubredditResponseDto>> getSubredditByName(@PathVariable String name){
        Subreddit findByNameSubreddit = subredditService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(subredditService.toDto(findByNameSubreddit)));
    }


    @DeleteMapping("/{subredditName}")
    @RequireVerified
    public ResponseEntity<ApiResponse<Void>> deleteSubredditByName(@PathVariable String subredditName){
        subredditService.deleteSubredditByName(subredditName);
        return ResponseEntity.ok(ApiResponse.successMessage("subreddit deleted successfully!"));
    }

    @RateLimit(requests = 25)
    @RequireVerified
    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<SubredditResponseDto>> updateSubreddit(@PathVariable String name, @Valid @RequestBody SubredditUpdateDto updateDto){
        Subreddit subreddit = subredditService.updateSubreddit(name,updateDto);
        SubredditResponseDto responseDto = subredditService.toDto(subreddit);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PostMapping("/{name}/join")
    @RequireVerified
    public ResponseEntity<ApiResponse<Void>> joinSubreddit(
            @PathVariable String name) {
        subredditService.joinSubreddit(name);
        return ResponseEntity.ok(ApiResponse.successMessage("You have successfully joined the subreddit!"));
    }

    @PostMapping("/{name}/leave")
    @RequireVerified
    public ResponseEntity<ApiResponse<Void>> leaveSubreddit(
            @PathVariable String name) {

        subredditService.leaveSubreddit(name);
        return ResponseEntity.ok(ApiResponse.successMessage("You have successfully left the subreddit :<"));
    }

}
