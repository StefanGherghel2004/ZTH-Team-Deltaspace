package com.example.demo.mapper;

import com.example.demo.dto.subreddit.SubredditCreateDto;
import com.example.demo.dto.subreddit.response.SubredditResponseDto;
import com.example.demo.model.Subreddit;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface SubredditMapper {
    Subreddit toEntity(SubredditCreateDto dto);

    SubredditResponseDto toResponseDto(Subreddit subreddit);


}
