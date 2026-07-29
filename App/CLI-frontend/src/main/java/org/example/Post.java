package org.example;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("upvotes")
    private Integer upvotes;
    @JsonProperty("downvotes")
    private Integer downvotes;
    private String userVote;


    private boolean nsfw;

    //Constructor
    public Post (String author, String postTitle, String content, String imageUrl, boolean NSFW, String subreddit,Integer upVotes, Integer downVotes) {
        this.author = author;
        this.imageUrl = imageUrl;
        this.title = postTitle;
        this.content = content;
        this.nsfw = NSFW;
        this.subreddit = subreddit;
        this.upvotes=upVotes;
        this.downvotes=downVotes;
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
                ", upVotes=" + upvotes +
                ", downVotes=" + downvotes +
                ", NSFW=" + nsfw +
                '}';
    }
}
