package com.example.demo.controller;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.mapper.CommunityMapper;
import com.example.demo.model.Community;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMapper communityMapper;
    private final CommunityRepository communityRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Community addCommunity(@Valid @RequestBody CommunityCreateDto createDto){

        return communityService.addCommunity(createDto);
    }

    @GetMapping
    public List<Community> listAllCommunities(){
        return communityService.listAllCommunities();
    }

    @DeleteMapping("/{communityName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommunity(@PathVariable String communityName){
        communityService.deleteCommunityByName(communityName);
    }


}
