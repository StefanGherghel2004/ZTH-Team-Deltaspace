package com.example.demo.service;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.community.CommunityUpdateDto;
import com.example.demo.exception.notfound.CommunityNotFoundException;
import com.example.demo.model.Community;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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

    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    public Community addCommunity(CommunityCreateDto dto){

        User author = userService.getAuthenticatedUser();
        Topic topic = getTopicFromString(dto.getTopic());

        Community community = new Community();
        community.setAuthor(author);
        community.setName(dto.getTitle());
        community.setTopic(topic.name());
        community.setDescription(dto.getDescription());

        return communityRepository.save(community);
    }
    public List<Community> listAllCommunities(){
        return communityRepository.findAll();
    }

    public void deleteCommunityByName(String communityName) {
        Community communityToDelete=communityRepository.findByName(communityName)
            .orElseThrow(()-> new CommunityNotFoundException("Community " + communityName + " not found"));
        User user = userService.getAuthenticatedUser();
        if(!communityToDelete.getAuthor().equals(user)){
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }
        communityRepository.delete(communityToDelete);
    }

    public Community updateCommunity(String communityName, CommunityUpdateDto updateDto){
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(()->new CommunityNotFoundException("Community not found"));

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
        return communityRepository.save(community);
    }

    public Community verifyNsfwCommunities(String communityName){
        User authenticatedUser= userService.getAuthenticatedUser();
        int userAge= Period.between(authenticatedUser.getDateOfBirth(), LocalDate.now()).getYears();
        boolean isNSFW=postRepository.existsByCommunityNameAndNsfwTrue(communityName);
        if(isNSFW && userAge<18) {
            throw new AccessDeniedException("This community is marked as NSFW");
            }
        return communityRepository.findByName(communityName)
                .orElseThrow(()->new CommunityNotFoundException("Community not found"));

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
}

