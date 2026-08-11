package com.example.demo.mapper;

import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.username", target = "author")
    CommentResponseDto toDto(Comment comment);

    Comment clone(Comment comment);
}
