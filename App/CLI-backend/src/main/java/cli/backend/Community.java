package cli.backend;

import java.util.ArrayList;
import java.util.List;

public class Community {
    private String nickname;
    private String topic;
    private List<Post> posts =new ArrayList<>();
    private String description;
    private int postId=0;


    public Community(String topic,String nickname,String description){
        this.topic=topic;
        this.nickname=nickname;
        this.description =description;

    }

    public String getDescription() {
        return description;
    }

    public String getNickname() {
        return nickname;
    }

    public List<Post> getPosts(){
        return posts;
    }


    public void addPost(Post post){
    this.posts.add(post);
    postId++;
    }

    public Post findPostById(int id) {
        for (Post post : posts) {
            if (post.getPostID() == id) {
                return post;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Community{" +
                "nickname=r/"  + nickname + '\'' +
                ", topic='" + topic + '\'' +
                ", posts=" + posts +
                ", description='" + description + '\'' +
                '}';
    }
}
