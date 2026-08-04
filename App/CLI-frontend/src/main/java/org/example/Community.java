package org.example;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Community {
    private UUID id;

    @JsonProperty("name")
    @JsonAlias({"displayName", "nickname"})
    private String nickname;

    private String topic;
    private String description;
    private String displayName;

    private String communityCreatorUsername;
    private UUID communityCreatorId;

    @JsonProperty("author")
    private void unpackAuthor(Object author) {
        if (author instanceof Map) {
            Map<?, ?> authorMap = (Map<?, ?>) author;
            if (authorMap.containsKey("id") && authorMap.get("id") != null) {
                this.communityCreatorId = UUID.fromString(authorMap.get("id").toString());
            }
            if (authorMap.containsKey("username") && authorMap.get("username") != null) {
                this.communityCreatorUsername = authorMap.get("username").toString();
            } else if (authorMap.containsKey("name") && authorMap.get("name") != null) {
                this.communityCreatorUsername = authorMap.get("name").toString();
            }
        } else if (author instanceof String) {
            this.communityCreatorUsername = (String) author;
        }
    }

    @JsonProperty("NSFW")
    @JsonAlias({"nsfw", "isNSFW", "isNsfw"})
    private boolean NSFW;

    private String createdAt;
    private String updatedAt;
    private Integer memberCount;

    public String getNickname() {
        if (nickname != null && !nickname.isBlank()) return nickname;
        if (displayName != null && !displayName.isBlank()) return displayName;
        return "Unknown";
    }

    public String getCommunityCreatorUsername() {
        return communityCreatorUsername != null ? communityCreatorUsername : "Unknown";
    }

    @Override
    public String toString() {
        return "Community{" +
                "nickname=r/"  + getNickname() + '\'' +
                ", topic='" + topic + '\'' +
                ", description='" + description + '\'' +
                ", memberCount='" + memberCount+ '\'' +
                '}';
    }
}