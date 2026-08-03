package com.example.demo.service;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.community.CommunityUpdateDto;
import com.example.demo.dto.community.response.CommunityResponseDto;
import com.example.demo.exception.notfound.CommunityNotFoundException;
import com.example.demo.mapper.CommunityMapper;
import com.example.demo.model.Community;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    public enum Topic {
        FOOD,
        GAMING,
        ART,
        SCIENCE,
        TECH
    }

    private static final int NSFW_AGE = 18;

    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommunityMapper communityMapper;
    private final UserRepository userRepository;

    public CommunityResponseDto addCommunity(CommunityCreateDto dto){

        User user = userService.getAuthenticatedUser();

        Topic topic = getTopicFromString(dto.getTopic());

        Community community = new Community();
        community.setAuthor(user);
        community.setName(dto.getName());
        community.setTopic(topic.name());
        community.setDescription(dto.getDescription());
        community.setDisplayName(dto.getDisplayName());
        community.setIconUrl(dto.getIconUrl());
        community.getMembers().add(user);
        Community savedCommunity=communityRepository.save(community);
        return toDto(savedCommunity);
    }
    public List<Community> listAllCommunities(){
        return communityRepository.findAll();
    }

    public CommunityResponseDto toDto(Community community){
        CommunityResponseDto dto= communityMapper.toResponseDto(community);
        int memberCount = community.getMembers() != null ? community.getMembers().size() : 1;
        int postCount = community.getPosts() != null ? community.getPosts().size() : 0;
        dto.setMemberCount(memberCount);
        dto.setPostCount(postCount);

        return dto;
    }

    public void deleteCommunityByName(String communityName) {
        Community communityToDelete = findByName(communityName);
        User user = userService.getAuthenticatedUser();
        if(!communityToDelete.getAuthor().equals(user)){
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }
        communityRepository.delete(communityToDelete);
    }

    public Community updateCommunity(String communityName, CommunityUpdateDto updateDto){
        Community community = findByName(communityName);
        User authenticatedUser = userService.getAuthenticatedUser();
        if(!community.getAuthor().equals(authenticatedUser)) {
            throw new AccessDeniedException("You are not allowed to perform this operation. You are not the owner");
        }

        Topic topic;
        if (updateDto.getTopic() != null) {
            topic = getTopicFromString(updateDto.getTopic());
            community.setTopic(topic.name());
        }

        community.setDescription(updateDto.getDescription());
        community.setTopic(updateDto.getTopic());
        community.setName(updateDto.getName());
        return communityRepository.save(community);
    }

    public Community findByName(String name) {
        return communityRepository.findByName(name)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with name=" + name));

    }


    public Community verifyNsfwCommunities(String communityName){
        User authenticatedUser= userService.getAuthenticatedUser();
        int userAge = authenticatedUser.getAge();
        boolean isNSFW = postRepository.existsByCommunityNameAndNsfwTrue(communityName);
//        if (isNSFW && userAge < NSFW_AGE) {
//            throw new AccessDeniedException("This community is marked as NSFW");
//        }
        return findByName(communityName);
    }

    private Topic getTopicFromString(String topicString) {
        Topic result = null;
        for (Topic topic : Topic.values()) {
            if (topic.name().equalsIgnoreCase(topicString)) {
                result = topic;
                break;
            }
        }

        if(result == null){
            throw new IllegalArgumentException("The Selected Topic does not exist");
        }

        return result;
    }

    @Transactional
    public void joinCommunity(String communityName){
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(()-> new RuntimeException("Community Not Found"));
        User user = userService.getAuthenticatedUser();
        community.getMembers().add(user);
        communityRepository.save(community);

    }

    @Transactional
    public void leaveCommunity(String communityName, UUID userId){
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(()-> new RuntimeException("Community Not Found"));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User Not Found"));

        community.getMembers().remove(user);
        communityRepository.save(community);

    }
}

