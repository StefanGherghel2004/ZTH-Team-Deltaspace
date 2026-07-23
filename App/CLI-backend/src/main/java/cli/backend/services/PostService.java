package cli.backend.services;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.database.PostRepository;
import cli.backend.database.VoteRepository;

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

    public Post addPost(String authorUsername, String postTitle, String postContents, String imageLink, boolean NSFW, Community currentCommunity,Integer upVotes, Integer downVotes){
        String targetName = (currentCommunity != null) ? currentCommunity.getNickname() : null;

        Post newPost = new Post(authorUsername, postTitle, postContents, imageLink, NSFW, targetName,upVotes,downVotes);

        postRepository.addPost(newPost);

        if(currentCommunity != null) {
            currentCommunity.addPost(newPost);
        }

        return newPost;
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

    public boolean canUserEditPost (User user, Post post) {
        if (user == null || post == null)
            return false;
        return post.getAuthorUsername().equals(user.getUsername());
    }

    public List<Post> getFeedFromRepository () {
        return postRepository.findAll();
    }
}