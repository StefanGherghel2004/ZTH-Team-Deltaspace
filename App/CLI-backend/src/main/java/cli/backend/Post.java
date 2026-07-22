package cli.backend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Post {
    private Long id;
    private String authorUsername;
    private String postTitle;
    private String postContents;
    private String imageLink;
    private String communityName;
    private Integer upVotes;
    private Integer downVotes;
    private boolean NSFW;

    //Constructor
    public Post (String authorUsername, String postTitle, String postContents, String imageLink, boolean NSFW, String community,Integer upVotes, Integer downVotes) {
        this.authorUsername = authorUsername;
        this.imageLink = imageLink;
        this.postTitle = postTitle;
        this.postContents = postContents;
        this.NSFW = NSFW;
        this.communityName = community;
        this.upVotes=upVotes;
        this.downVotes=downVotes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", authorUsername='" + authorUsername + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", postContents='" + postContents + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", communityName='" + communityName + '\'' +
                ", upVotes=" + upVotes +
                ", downVotes=" + downVotes +
                ", NSFW=" + NSFW +
                '}';
    }
}
