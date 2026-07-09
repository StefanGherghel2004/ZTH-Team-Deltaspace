package cli.backend;

import java.util.List;
import java.util.ArrayList;

public class Post {

    private static int postIdIncrementation = 0;
    private int commentIdIncrementation;
    private int postID;
    private User user;
    private String postTitle;
    private String postContents;
    private List<Comment> comments = new ArrayList<>();
    private String imageLink;
    private String communityName;
    private boolean NSFW=false;
    //Constructor
    public Post (User user, String postTitle, String postContents, String imageLink, boolean NSFW, String community) {

        this.postID = postIdIncrementation;
        postIdIncrementation++;

        this.user = user;
        this.imageLink = imageLink;
        this.postTitle = postTitle;
        this.postContents = postContents;
        this.NSFW = NSFW;
        this.commentIdIncrementation = 0;
        this.communityName =community;
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

    public int getCommentIdIncrementation () {

        return commentIdIncrementation;
    }

    public String getCommunityName () {

        return communityName;
    }

    public boolean getNSFW(){
        return NSFW;
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

    public void setCommunityName (String communityName) {

        this.communityName = communityName;
    }

    public void SetNSFW(boolean NSFW) {
        this.NSFW = NSFW;
    }

    @Override
    public String toString () {

        return "Post{" + "id=" + postID + ", user=" + (user != null ? user.getUsername() : "null") +
                ", title='" + postTitle + '\'' +
                ", hasImage=" + (imageLink != null) +
                ", NSFW=" + NSFW +
                '}';
    }

    public void addComment (Comment comment, int idParent) {

        this.comments.add(comment);
        comment.setIdComment(commentIdIncrementation);
        comment.setIdParent(idParent);
        commentIdIncrementation++;
    }

    public Comment findCommentById(int id) {

        for (Comment comment : comments) {
            if (comment.getId() == id) {
                return comment;
            }
        }

        return null;
    }

    public int getCommentsCount() {
        return comments.size();
    }


}
