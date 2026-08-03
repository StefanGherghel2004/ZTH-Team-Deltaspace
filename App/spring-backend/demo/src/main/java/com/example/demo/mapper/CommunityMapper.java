package com.example.demo.mapper;

import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.community.response.CommunityResponseDto;
import com.example.demo.model.Community;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface CommunityMapper {
    Community toEntity(CommunityCreateDto dto);

    CommunityResponseDto toResponseDto(Community community);


}
