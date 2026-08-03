package com.example.demo.controller;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.community.CommunityUpdateDto;
import com.example.demo.dto.community.response.CommunityResponseDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.mapper.CommunityMapper;
import com.example.demo.model.Community;
import com.example.demo.model.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subreddits")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMapper communityMapper;
    private final CommunityRepository communityRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CommunityResponseDto>> addCommunity(@Valid @RequestBody CommunityCreateDto createDto){
        CommunityResponseDto responseDto = communityService.addCommunity(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CommunityResponseDto>>> getCommunities(){
        List<Community> communities;

        communities = communityService.listAllCommunities();

        List<CommunityResponseDto> response = communities.stream()
                .map(communityService::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(response,response.size()));

    }

    @DeleteMapping("/{communityName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommunity(@PathVariable String communityName){
        communityService.deleteCommunityByName(communityName);
    }

    @PutMapping("{communityName}")
    public Community updateCommunity(@PathVariable String communityName, @Valid @RequestBody CommunityUpdateDto updateDto){
        return communityService.updateCommunity(communityName,updateDto);
    }

    @GetMapping("{communityName}")
    public Community getCommunity(@PathVariable String communityName){
        return communityService.verifyNsfwCommunities(communityName);

    }

    @PostMapping("/{name}/join")
    public ResponseEntity<ApiResponse<Void>> joinCommunity(
            @PathVariable String name) { // Sau preiei ID-ul userului conectat

        communityService.joinCommunity(name);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{name}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveCommunity(
            @PathVariable String name,
            @AuthenticationPrincipal User currentUser) {

        communityService.leaveCommunity(name, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
