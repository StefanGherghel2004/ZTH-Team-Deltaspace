package com.example.demo.service;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.community.CommunityUpdateDto;
import com.example.demo.exception.CommunityNotFoundException;
import com.example.demo.model.Community;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
    public static final List<String> Topics=List.of(
    "FOOD",
    "GAMING",
    "ART",
    "SCIENCE",
    "TECH"
    );

    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    public Community addCommunity(CommunityCreateDto dto){

        User author = userService.findByUsername(dto.getAuthorUsername());
        boolean topicExist=false;
        for(String topic :Topics){
            if(topic.equalsIgnoreCase(dto.getTopic())){
                topicExist=true;
                break;
            }
        }
        if(!topicExist){
            throw new IllegalArgumentException("The Selected Topic does not exist");
        }

        Community community = new Community();
        community.setAuthor(author);
        community.setName(dto.getTitle());
        community.setTopic(dto.getTopic());
        community.setDescription(dto.getDescription());

        return communityRepository.save(community);
    }
    public List<Community> listAllCommunities(){
        return communityRepository.findAll();
    }

    public void deleteCommunityByName(String communityName) {
        Community communityToDelete=communityRepository.findByName(communityName)
            .orElseThrow(()-> new CommunityNotFoundException("Community " + communityName + " not found"));
        communityRepository.delete(communityToDelete);
    }

    public Community updateCommunity(String communityName, CommunityUpdateDto updateDto){
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(()->new CommunityNotFoundException("Community not found"));
        boolean topicExist=false;
        for(String topic:Topics){
            if(topic.equalsIgnoreCase(updateDto.getTopic()))
                topicExist = true;
                break;
        }
        if(!topicExist){
            throw new IllegalArgumentException("The Selected Topic does not exist");
        }

        community.setDescription(updateDto.getDescription());
        community.setTopic(updateDto.getTopic());
        return communityRepository.save(community);
    }

    public Community verifyNsfwCommunities(String communityName, int userAge){
        boolean isNSFW=postRepository.existsByCommunityNameAndNsfwTrue(communityName);
        if(isNSFW && userAge<18) {
            throw new AccessDeniedException("This community is marked as NSFW");
            }
        return communityRepository.findByName(communityName)
                .orElseThrow(()->new CommunityNotFoundException("Community not found"));

    }
}

