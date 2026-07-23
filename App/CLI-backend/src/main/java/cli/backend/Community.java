package cli.backend;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Community {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickname;
    private String topic;
    private String description;
    private Long communityCreator;


    public Community(Long communityCreator, String topic,String nickname,String description){
        this.topic=topic;
        this.nickname=nickname;
        this.description =description;
        this.communityCreator = communityCreator;
    }

    public Community(Long id, String nickname, String topic, String description, Long communityCreator, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id=id;
        this.nickname = nickname;
        this.topic = topic;
        this.description = description;
        this.communityCreator = communityCreator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString() {
        return "Community{" +
                "nickname=r/"  + nickname + '\'' +
                ", topic='" + topic + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
