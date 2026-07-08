package cli.backend.services;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostService {

    private static List<Post> posts=new ArrayList<>();
    private static PostService instance;

    public static PostService getInstance(){
        if(instance==null){
            instance = new PostService();
        }
        return instance;
    }

    public static Post addPost(User user,String postTitle, String postContents, String imageLink, Community currentCommunity){
        Community targetCommunity = currentCommunity;

        String targetName = (targetCommunity != null) ? targetCommunity.getNickname() : "u/" + user.getUsername();
        Post newPost = new Post(user, postTitle, postContents, imageLink, targetName);
        posts.add(newPost);
        if(targetCommunity != null) {
            targetCommunity.addPost(newPost);
        }
        return newPost;
    }

    public static List<Post> getRandomizedFeed(List<Post> posts){
        if(posts==null || posts.isEmpty()){
            return new ArrayList<>();
        }
        List<Post> randomizedList = new ArrayList<>(posts);
        Collections.shuffle(randomizedList);
        return randomizedList;
    }
    public static List<Post> getPosts(){
        return posts;
    }
    public static Post findPostById(int id){
        for (Post p:posts){
            if(p.getPostID()==id) {
                return p;
            }
        }
        return null;
    }

}
