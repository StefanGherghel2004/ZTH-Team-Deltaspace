package com.example.demo.controller;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.mapper.CommunityMapper;
import com.example.demo.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMapper communityMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityCreateDto addCommunity(@Valid @RequestBody CommunityCreateDto createDto){
        return communityMapper.toResponseDto(savedCommunity);
    }
}
