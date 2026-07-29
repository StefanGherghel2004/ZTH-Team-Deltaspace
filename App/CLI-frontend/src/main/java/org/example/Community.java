package org.example;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Community {
    private UUID id;
    @JsonProperty("name")
    private String nickname;
    private String topic;
    private String description;

    private String communityCreatorUsername;
    @JsonProperty("author")
    private void unpackAuthor(Map<String, Object> author) {
        if (author != null) {
            if (author.containsKey("id")) {
                this.communityCreatorId = UUID.fromString(author.get("id").toString());
            }
            if (author.containsKey("username")) {
                this.communityCreatorUsername = author.get("username").toString();
            }
        }
    }
    private UUID communityCreatorId;

    @JsonProperty("NSFW")
    @JsonAlias({"nsfw", "isNSFW", "isNsfw"})
    private boolean NSFW;

    private String createdAt;
    private String updatedAt;


    public Community(UUID communityCreator, String topic, String nickname, String description, boolean NSFW){
        this.topic = topic;
        this.nickname = nickname;
        this.description = description;
        this.communityCreatorId = communityCreator;
        this.NSFW = NSFW;
    }

    public Community(UUID id, String nickname, String topic, String description, UUID communityCreator, String createdAt, String updatedAt,boolean NSFW){
        this.id = id;
        this.nickname = nickname;
        this.topic = topic;
        this.description = description;
        this.communityCreatorId = communityCreator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.NSFW = NSFW;
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