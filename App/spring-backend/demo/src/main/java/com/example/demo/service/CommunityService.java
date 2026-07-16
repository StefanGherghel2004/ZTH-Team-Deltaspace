package com.example.demo.service;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.model.Community;
import com.example.demo.model.User;
import com.example.demo.repository.CommunityRepository;
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
}
