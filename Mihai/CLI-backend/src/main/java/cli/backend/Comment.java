package cli.backend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private String text;
    private User user;
    private int idPost;
    private int idComment;
    private int idParent = -1;

    public Comment(String text, User user, int IdPost){
        this.text=text;
        this.user=user;
        this.idPost=IdPost;
    }

    public String getUsername(){

        return user.getUsername();
    }

    public int getId(){

        return idComment;
    }

    public int getCommentPostId () {

        return idPost;
    }
}
