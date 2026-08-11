package com.example.demo.mapper;

import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface PostMapper {

    @Mapping(source = "subreddit.name", target = "subreddit")
    @Mapping(source = "author.username", target = "author")
    PostResponseDto toDto(Post post);

}

