package cli.backend;

public class Comment {
    private static int nextId = 1;
    private String text;
    private User user;
    private int idPost;
    private int idComment;
    private int idParent = -1;


    public Comment(String text, User user, int IdPost){
        this.idComment = nextId++;
        this.text=text;
        this.user=user;
        this.idPost=IdPost;
    }

    public String getText(){
        return text;
    }

    public String getUsername(){
        return user.getUsername();
    }

    public int getId(){
        return idComment;
    }
    public int getIdParent(){
        return idParent;
    }

    public int getCommentPostId () {

        return idPost;
    }

    public void setIdParent(int idParent){
        this.idParent=idParent;
    }

    public void setIdComment (int idComment) {

        this.idComment = idComment;
    }

}
