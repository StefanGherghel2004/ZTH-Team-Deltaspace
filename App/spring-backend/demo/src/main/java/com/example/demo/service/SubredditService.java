package com.example.demo.service;

import com.example.demo.dto.subreddit.SubredditCreateDto;
import com.example.demo.dto.subreddit.SubredditUpdateDto;
import com.example.demo.dto.subreddit.response.SubredditResponseDto;
import com.example.demo.exception.notfound.SubredditNotFoundException;
import com.example.demo.logger.Logger;
import com.example.demo.mapper.SubredditMapper;
import com.example.demo.model.Subreddit;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.User;
import com.example.demo.repository.SubredditRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SubredditService {

    public enum Topic {
        FOOD,
        GAMING,
        ART,
        SCIENCE,
        TECH
    }

    private static final int NSFW_AGE = 18;

    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final SubredditMapper subredditMapper;
    private final UserRepository userRepository;

    public SubredditResponseDto addSubreddit(SubredditCreateDto dto){

        User user = userService.getAuthenticatedUser();

        Topic topic = null;

        Subreddit subreddit = new Subreddit();
        subreddit.setAuthor(user);
        subreddit.setName(dto.getName());
        subreddit.setTopic(null);
        subreddit.setDescription(dto.getDescription());
        subreddit.setDisplayName(dto.getDisplayName());
        subreddit.setIconUrl(dto.getIconUrl());
        subreddit.getMembers().add(user);
        Subreddit savedSubreddit = subredditRepository.save(subreddit);
        Logger.info("Subreddit %s created", savedSubreddit.getName());
        return toDto(savedSubreddit);
    }
    public List<Subreddit> listAllSubreddits(){
        return subredditRepository.findAll();
    }

    public SubredditResponseDto toDto(Subreddit subreddit){
        SubredditResponseDto dto= subredditMapper.toResponseDto(subreddit);
        int memberCount = subreddit.getMembers() != null ? subreddit.getMembers().size() : 1;
        int postCount = subreddit.getPosts() != null ? subreddit.getPosts().size() : 0;
        dto.setMemberCount(memberCount);
        dto.setPostCount(postCount);

        return dto;
    }

    public void deleteSubredditByName(String subredditName) {
        Subreddit subredditToDelete = findByName(subredditName);
        User user = userService.getAuthenticatedUser();
        if(!subredditToDelete.getAuthor().equals(user)){
            Logger.warning("You are not allowed to perform this operation! You are not the owner!");
            throw new AccessDeniedException("You are not allowed to perform this operation! You are not the owner!");
        }
        if(subredditToDelete.getPosts()==null|| subredditToDelete.getPosts().isEmpty()){
            Logger.info("Deleted %s subreddit", subredditToDelete.getName());
            subredditRepository.delete(subredditToDelete);
        }
        else{throw new AccessDeniedException("Cannot Delete! subreddit still has posts!");}

    }

    public Subreddit updateSubreddit(String subredditName, SubredditUpdateDto updateDto){
        Subreddit subreddit = findByName(subredditName);
        User authenticatedUser = userService.getAuthenticatedUser();
        if(!subreddit.getAuthor().equals(authenticatedUser)) {
            Logger.warning("%s is trying to update another user's subreddit.", authenticatedUser.getUsername());
            throw new AccessDeniedException("You are not allowed to perform this operation. You are not the owner");
        }

        Topic topic;
        if (updateDto.getTopic() != null) {
            topic = getTopicFromString(updateDto.getTopic());
            subreddit.setTopic(topic.name());
        }

        if(updateDto.getDescription()!=null) {
            subreddit.setDescription(updateDto.getDescription());
        }

        subreddit.setTopic(updateDto.getTopic());
        if(updateDto.getDisplayName()!=null) {
            subreddit.setDisplayName(updateDto.getDisplayName());
        }
        if(updateDto.getIconUrl()!=null) {
            subreddit.setIconUrl(updateDto.getIconUrl());
        }
        Logger.info("%s has been updated", subreddit.getName());
        return subredditRepository.save(subreddit);
    }

    public Subreddit findByName(String name) {
        return subredditRepository.findByName(name)
                .orElseThrow(() -> new SubredditNotFoundException("subreddit not found with name=" + name));

    }


    public Subreddit verifyNsfwCommunities(String subredditName){
        User authenticatedUser= userService.getAuthenticatedUser();
        int userAge = authenticatedUser.getAge();
//        boolean isNSFW = postRepository.existsBysubredditNameAndNsfwTrue(subredditName);
//        if (isNSFW && userAge < NSFW_AGE) {
//            throw new AccessDeniedException("This subreddit is marked as NSFW");
//        }
        return findByName(subredditName);
    }

    private Topic getTopicFromString(String topicString) {
        Topic result = null;
        for (Topic topic : Topic.values()) {
            if (topic.name().equalsIgnoreCase(topicString)) {
                result = topic;
                break;
            }
        }

        return result;
    }

    @Transactional
    public void joinSubreddit(String subredditName){
        Subreddit subreddit = findByName(subredditName);
        User user = userService.getAuthenticatedUser();
        if(subreddit.getMembers().contains(user)){
            throw new AccessDeniedException("You are already a member of this subreddit!");
        }
        else {
            subreddit.getMembers().add(user);
        }
        Logger.info("%s has joined a %s subreddit", user.getUsername(), subreddit.getName() + "");
        subredditRepository.save(subreddit);

    }

    @Transactional
    public void leaveSubreddit(String subredditName){
        Subreddit subreddit = findByName(subredditName);
        User user = userService.getAuthenticatedUser();
        if(!subreddit.getMembers().contains(user)){
            throw new IllegalStateException("You are no longer member of this subreddit");
        }
        else {
            subreddit.getMembers().remove(user);
            Logger.info("%s has left %s", user.getUsername(), subreddit.getName());
            subredditRepository.save(subreddit);
        }
    }
}

