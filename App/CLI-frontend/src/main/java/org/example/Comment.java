package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Comment {
    private UUID id;
    private UUID postId;
    private String authorUsername;
    private String text;
    private UUID idParent;

    public Comment(String text, String authorUsername, UUID postId){
        this.text = text;
        this.authorUsername = authorUsername;
        this.postId = postId;
    }
}
