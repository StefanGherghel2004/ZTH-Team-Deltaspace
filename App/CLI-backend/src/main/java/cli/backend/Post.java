package cli.backend;

import cli.backend.database.ExcelWrite;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
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
    private boolean NSFW;

    //Constructor
    public Post (User user, String postTitle, String postContents, String imageLink, boolean NSFW, String community) {

        this.user = user;
        this.imageLink = imageLink;
        this.postTitle = postTitle;
        this.postContents = postContents;
        this.NSFW = NSFW;
        this.commentIdIncrementation = 0;
        this.communityName = community;
        postIdIncrementation = ExcelWrite.getCurrentId(ExcelWrite.getInstance().postDatabasePath);
        this.postID = ++postIdIncrementation;
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
