package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Post {
    private UUID id;
    private String author;
    private String title;
    private String postContents;
    private String imageUrl;
    private String subreddit;
    private Integer upVotes;
    private Integer downVotes;
    private boolean NSFW;

    //Constructor
    public Post (String author, String postTitle, String postContents, String imageUrl, boolean NSFW, String subreddit,Integer upVotes, Integer downVotes) {
        this.author = author;
        this.imageUrl = imageUrl;
        this.title = postTitle;
        this.postContents = postContents;
        this.NSFW = NSFW;
        this.subreddit = subreddit;
        this.upVotes=upVotes;
        this.downVotes=downVotes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", authorUsername='" + author + '\'' +
                ", postTitle='" + title + '\'' +
                ", postContents='" + postContents + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", communityName='" + subreddit + '\'' +
                ", upVotes=" + upVotes +
                ", downVotes=" + downVotes +
                ", NSFW=" + NSFW +
                '}';
    }
}
