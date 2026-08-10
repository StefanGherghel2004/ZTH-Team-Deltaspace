package com.example.demo.controller;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.community.CommunityUpdateDto;
import com.example.demo.dto.community.response.CommunityResponseDto;
import com.example.demo.model.Community;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subreddits")
@RequiredArgsConstructor
// todo rename community to subreddit
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CommunityResponseDto>> addCommunity(@Valid @RequestBody CommunityCreateDto createDto){
        CommunityResponseDto responseDto = communityService.addCommunity(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommunityResponseDto>>> getCommunities(){
        List<Community> communities;

        communities = communityService.listAllCommunities();

        List<CommunityResponseDto> response = communities.stream()
                .map(communityService::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response,response.size()));

    }
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<CommunityResponseDto>> getCommunityByName(@PathVariable String name){
        Community findByNameCommunity = communityService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(communityService.toDto(findByNameCommunity)));
    }


    @DeleteMapping("/{communityName}")
    public ResponseEntity<ApiResponse<Void>> deleteCommunityByName(@PathVariable String communityName){
        communityService.deleteCommunityByName(communityName);
        return ResponseEntity.ok(ApiResponse.successMessage("Community deleted successfully!"));
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> updateCommunity(@PathVariable String name, @Valid @RequestBody CommunityUpdateDto updateDto){
        // todo check if u need to add the updated community to the response
        Community community = communityService.updateCommunity(name,updateDto);
        return ResponseEntity.ok(ApiResponse.successMessage("Community successfully updated!"));
    }
    @PostMapping("/{name}/join")
    public ResponseEntity<ApiResponse<Void>> joinCommunity(@PathVariable String name) {
        communityService.joinCommunity(name);
        return ResponseEntity.ok(ApiResponse.successMessage("You have successfully joined the community!"));
    }

    @PostMapping("/{name}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveCommunity(@PathVariable String name) {
        communityService.leaveCommunity(name);
        return ResponseEntity.ok(ApiResponse.successMessage("You have successfully left the community :<"));
    }

}
