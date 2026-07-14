package cli.backend.services;

import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.database.ExcelWrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostService {

    private final List<Post> posts=new ArrayList<>();
    private static PostService instance;
    private final ExcelWrite excelWrite = ExcelWrite.getInstance();

    public static PostService getInstance(){
        if(instance==null){
            instance = new PostService();
        }
        return instance;
    }

    public Post addPost(User user,String postTitle, String postContents, String imageLink, boolean NSFW, Community currentCommunity){
        Community targetCommunity = currentCommunity;

        String targetName = (targetCommunity != null) ? targetCommunity.getNickname() : "u/" + user.getUsername();
        Post newPost = new Post(user, postTitle, postContents, imageLink, NSFW, targetName);
        posts.add(newPost);
        if(targetCommunity != null) {
            targetCommunity.addPost(newPost);
        }

        String targetCommunityChecked = (targetCommunity != null) ? targetCommunity.getNickname() : "None";
        String imageLinkChecked = (imageLink != null) ? imageLink : "No image link";

        excelWrite.write(excelWrite.postDatabasePath, List.of(
                String.valueOf(newPost.getPostID()),
                user.getUsername(),
                postTitle,postContents,
                imageLinkChecked,
                targetCommunityChecked,
                String.valueOf(NSFW)));

        return newPost;
    }

    public List<Post> getRandomizedFeed(List<Post> posts){
        if(posts==null || posts.isEmpty()){
            return new ArrayList<>();
        }
        List<Post> randomizedList = new ArrayList<>(posts);
        Collections.shuffle(randomizedList);
        return randomizedList;
    }
    public List<Post> getPosts(){
        return posts;
    }

    public Post findPostById(int id){
        for (Post p:posts){
            if(p.getPostID()==id) {
                return p;
            }
        }
        return null;
    }

    public void deletePost(Post postToDelete) {
        posts.remove(postToDelete);

        Community community = CommunityService.getInstance()
                .getCommunityByName(postToDelete.getCommunityName());
        if (community != null) {
            community.deletePost(postToDelete);
        }
    }


    public boolean canUserDeletePost(User user, Post post, Community community) {
        if (user == null || post == null) {
            return false;
        }

        if (post.getUser().equals(user)) {
            return true;
        }

        if (community != null && community.getCommunityCreator().equals(user)) {
            return true;
        }
        
        return false;
    }
}
