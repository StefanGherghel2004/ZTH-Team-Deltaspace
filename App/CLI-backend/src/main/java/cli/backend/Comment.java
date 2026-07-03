package cli.backend;

public class Comment {
    private String text;
    private User username;
    private Post idPost;
    private int iDComment;
    private static int counter=1;

    public Comment(String text, User username, Post Id){
        this.text=text;
        this.username=username;
        this.idPost=Id;
        this.iDComment =counter++;
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


}
