package com.example.demo.mapper;

import com.example.demo.dto.comment.CommentCreateDto;
import com.example.demo.dto.comment.response.CommentResponseDto;
import com.example.demo.dto.community.CommunityCreateDto;
import com.example.demo.dto.post.response.PostResponseDto;
import com.example.demo.model.Comment;
import com.example.demo.model.Community;
import com.example.demo.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.username", target = "author")
    CommentResponseDto toDto(Comment comment);
}
