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
    private String content;
    private String imageUrl;
    private String subreddit;
    private Integer upVotes;
    private Integer downVotes;
    private boolean NSFW;

    //Constructor
    public Post (String author, String postTitle, String content, String imageUrl, boolean NSFW, String subreddit,Integer upVotes, Integer downVotes) {
        this.author = author;
        this.imageUrl = imageUrl;
        this.title = postTitle;
        this.content = content;
        this.NSFW = NSFW;
        this.subreddit = subreddit;
        this.upVotes=upVotes;
        this.downVotes=downVotes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", communityName='" + subreddit + '\'' +
                ", upVotes=" + upVotes +
                ", downVotes=" + downVotes +
                ", NSFW=" + NSFW +
                '}';
    }
}
