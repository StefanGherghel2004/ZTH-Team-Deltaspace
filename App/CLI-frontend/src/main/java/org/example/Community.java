package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class Community {
    private UUID id;
    private String nickname;
    private String topic;
    private String description;
    private UUID communityCreatorId;

    // Schimbat din OffsetDateTime în String
    private String createdAt;
    private String updatedAt;

    public Community(UUID communityCreator, String topic, String nickname, String description){
        this.topic = topic;
        this.nickname = nickname;
        this.description = description;
        this.communityCreatorId = communityCreator;
    }

    public Community(UUID id, String nickname, String topic, String description, UUID communityCreator, String createdAt, String updatedAt){
        this.id = id;
        this.nickname = nickname;
        this.topic = topic;
        this.description = description;
        this.communityCreatorId = communityCreator;
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