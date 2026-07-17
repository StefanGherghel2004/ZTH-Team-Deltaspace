package com.example.demo.service;

import com.example.demo.dto.post.PostCreateDto;
import com.example.demo.exception.notfound.CommunityNotFoundException;
import com.example.demo.exception.notfound.PostNotFoundException;
import com.example.demo.model.Community;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final S3ImageService s3ImageService;

    public Post createPost(PostCreateDto dto) {

        User author = userService.getAuthenticatedUser();

        // TO DO add check if user is deleted

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setNsfw(dto.isNsfw());
        post.setAuthor(author);

        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String imageUrl = s3ImageService.uploadImage(dto.getFile());
            post.setImageLink(imageUrl);
        }

        if (dto.getCommunityName() != null && !dto.getCommunityName().isBlank()) {
            Community community = communityRepository.findByName(dto.getCommunityName())
                    .orElseThrow(() -> new CommunityNotFoundException("Community not found with name " + dto.getCommunityName()));
            post.setCommunity(community);
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id=" + id));
    }

    public List<Post> getCommunityPost(String communityName) {
        Community community = communityRepository.findByName(communityName)
                .orElseThrow(()->new CommunityNotFoundException("Community not found"));
        return postRepository.findByCommunityName(communityName);
    }
}
