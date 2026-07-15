package com.example.demo.service;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final S3ImageService s3ImageService;

    public Post createPost(PostCreateDto dto) {

        User author = userService.findByUsername(dto.getAuthorUsername());

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setNsfw(dto.isNsfw());
        post.setAuthor(author);

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String imageUrl = s3ImageService.uploadImage(dto.getFile());
            post.setImageLink(imageUrl);
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
