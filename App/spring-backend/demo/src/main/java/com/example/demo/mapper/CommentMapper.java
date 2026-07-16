package com.example.demo.mapper;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.model.Comment;
import com.example.demo.model.Community;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(CommentCreateDto dto);

    CommentCreateDto toResponseDto(Comment comment);
}
