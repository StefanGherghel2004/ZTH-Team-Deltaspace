package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    private UUID id;
    private UUID postId;
    private String authorUsername;
    @JsonProperty("user")
    private void unpackUser(Map<String, Object> user) {
        if (user != null && user.containsKey("username")) {
            this.authorUsername = String.valueOf(user.get("username"));
        }
    }

    private String text;
    private UUID idParent;
    @JsonProperty("upvotes")
    private int upvotes;
    @JsonProperty("downvotes")
    private int downvotes;
    private String userVote;
    @JsonProperty("parentComment")
    private void unpackParentComment(Map<String, Object> parentComment) {
        if (parentComment != null && parentComment.containsKey("id")) {
            this.idParent = UUID.fromString(String.valueOf(parentComment.get("id")));
        }
    }

    public Comment(String text, String authorUsername, UUID postId){
        this.text = text;
        this.authorUsername = authorUsername;
        this.postId = postId;
    }
}
