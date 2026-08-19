package com.example.demo.listener;

import com.example.demo.event.PostCreatedEvent;
import com.example.demo.event.PostUpdatedEvent;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.PostSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PostSummaryEventListener {

    private final PostRepository postRepository;
    private final PostSummaryService postSummaryService;

    @Async("tldrTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostCreated(PostCreatedEvent event) {
        Post post = postRepository.findById(event.postId()).orElse(null);
        if (post != null) {
            postSummaryService.addTldrComment(post);
        }
    }

    @Async("tldrTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostUpdated(PostUpdatedEvent event) {
        Post post = postRepository.findById(event.postId()).orElse(null);
        if (post != null) {
            postSummaryService.updateTldrComment(post, event.oldContent());
        }
    }
}