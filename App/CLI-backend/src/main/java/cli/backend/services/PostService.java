package cli.backend.services;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.database.PostRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostService {

    private static PostService instance;
    private final PostRepository postRepository = PostRepository.getInstance();

    public static synchronized PostService getInstance(){
        if(instance == null){
            instance = new PostService();
        }
        return instance;
    }

    public Post addPost(String authorUsername, String postTitle, String postContents, String imageLink, boolean NSFW, Community currentCommunity){
        String targetName = (currentCommunity != null) ?
                currentCommunity.getNickname() : "u/" + authorUsername;

        Post newPost = new Post(authorUsername, postTitle, postContents, imageLink, NSFW, targetName);

        postRepository.addPost(newPost);

        if(currentCommunity != null) {
            currentCommunity.addPost(newPost);
        }

        return newPost;
    }

    public List<Post> getPosts(){
        return postRepository.findAll();
    }

    public List<Post> getRandomizedFeed(List<Post> feedPosts){
        if(feedPosts == null || feedPosts.isEmpty()){
            return new ArrayList<>();
        }
        List<Post> randomizedList = new ArrayList<>(feedPosts);
        Collections.shuffle(randomizedList);
        return randomizedList;
    }

    public Post findPostById(Long id){
        if (id == null)
            return null;
        return postRepository.findById(id);
    }

    public void deletePost(Post postToDelete) {

        if (postToDelete == null || postToDelete.getId() == null) return;
        postRepository.deletePostById(postToDelete.getId());
        Community community = CommunityService.getInstance()
                .getCommunityByName(postToDelete.getCommunityName());
        if (community != null) {
            community.deletePost(postToDelete);
        }
    }

    public boolean canUserDeletePost(User user, Post post, Community community) {
        if (user == null || post == null) return false;
        if (post.getAuthorUsername().equals(user.getUsername())) return true;

        return community != null && community.getCommunityCreator().equals(user.getUsername());
    }
}