package cli.backend;

public class Comment {
    private String text;
    private User user;
    private int idPost;
    private int iDComment;
    private int iDParrent;


    public Comment(String text, User user, int IdPost){
        this.text=text;
        this.user=user;
        this.idPost=IdPost;

    }

    public String getText(){
        return text;
    }

    public User getUsername(){
        return username;
    }

    public int getId(){
        return iDComment;
    }
    public int getiDParrent(){
        return iDParrent;
    }


}
