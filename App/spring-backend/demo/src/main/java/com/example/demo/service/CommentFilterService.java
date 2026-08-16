package com.example.demo.service;

import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentFilterService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public Comment filteredComment(Comment comment) {
        if (comment == null) {
            return null;
        }

        if (!comment.isDeleted()) {
            return comment;
        }

        boolean hasActiveReplies = commentRepository.existsByParentCommentIdAndDeletedIsFalse(comment.getId());
        if (!hasActiveReplies) {
            return null;
        }

        return maskIfDeleted(comment);
    }

    public Comment maskIfDeleted(Comment comment) {
        if (!comment.isDeleted()) {
            return comment;
        }

        Comment masked = commentMapper.clone(comment);
        masked.setDeleted(true);
        masked.setContent("[DELETED]");
        return masked;
    }
}