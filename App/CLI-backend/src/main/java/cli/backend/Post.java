package cli.backend;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class Post {
    private Long id;
    private String authorUsername;
    private String postTitle;
    private String postContents;
    private String imageLink;
    private String communityName;
    private boolean NSFW;

    //Constructor
    public Post (String authorUsername, String postTitle, String postContents, String imageLink, boolean NSFW, String community) {
        this.authorUsername = authorUsername;
        this.imageLink = imageLink;
        this.postTitle = postTitle;
        this.postContents = postContents;
        this.NSFW = NSFW;
        this.communityName = community;
    }

    @Override
    public String toString () {

        return "Post{" + "id=" + id + ", user=" + this.authorUsername +
                ", title='" + postTitle + '\'' +
                ", hasImage=" + (imageLink != null) +
                ", NSFW=" + NSFW +
                '}';
    }
}
