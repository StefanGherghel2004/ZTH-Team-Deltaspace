package cli.backend;

import cli.backend.database.ExcelDelete;
import cli.backend.database.ExcelRead;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Community {
    ExcelRead excelRead= ExcelRead.getInstance();
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickname;
    private String topic;
    private List<Post> posts =new ArrayList<>();
    private String description;
    private String communityCreator;


    public Community(String communityCreator, String topic,String nickname,String description){
        this.topic=topic;
        this.nickname=nickname;
        this.description =description;
        this.communityCreator = communityCreator;
    }

    public Community(Long id, String nickname, String topic, String description, String communityCreator, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id=id;
        this.nickname = nickname;
        this.topic = topic;
        this.description = description;
        this.communityCreator = communityCreator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addPost(Post post){
        posts.add(post);
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

    public void deletePost(Post post){
        posts.remove(post);
    }

    public boolean hasNSFWPost() {
        for (Post p:posts){
            if(p.isNSFW())
                return true;
        }
        return false;
    }
}
