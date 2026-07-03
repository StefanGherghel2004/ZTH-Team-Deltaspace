package cli.backend;

import java.util.List;
import java.util.ArrayList;

public class Post {

    private static int idIncrementation = 0;
    private int commentID = 0;
    private int postID;
    private User user;
    private String postTitle;
    private String postContents;
    private List<Comment> comments = new ArrayList<>();
    private String imageLink;

    //Constructor
    public Post (User user, String postTitle, String postContents, String imageLink) {

        this.postID = idIncrementation;
        idIncrementation ++;

        this.user = user;
        this.imageLink = imageLink;
        this.postTitle = postTitle;
        this.postContents = postContents;
    }

    //Getters
    public int getPostID () {

        return postID;
    }

    public User getUser () {

        return user;
    }

    public String getPostTitle () {

        return postTitle;
    }

    public String getPostContents () {

        return postContents;
    }

    public String getImageLink () {

        return imageLink;
    }

    public List<Comment> getComments () {

        return comments;
    }

    //Setters
    public void setUser (User user) {

        this.user = user;
    }

    public void setPostTitle (String postTitle) {

        this.postTitle = postTitle;
    }

    public void setPostContents (String postContents) {

        this.postContents = postContents;
    }

    public void setImageLink (String imageLink) {

        this.imageLink = imageLink;
    }

    @Override
    public String toString () {

        return "Post{" + "id=" + postID + ", user=" + (user != null ? user.getUsername() : "null") +
                ", title='" + postTitle + '\'' +
                ", hasImage=" + (imageLink != null) +
                '}';
    }

    public void addComment (Comment comment, int iDParrent) {

        this.comments.add(comment);
        commentID ++;
    }

}
