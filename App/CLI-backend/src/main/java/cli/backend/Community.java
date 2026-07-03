package cli.backend;

import java.util.ArrayList;
import java.util.List;

public class Community {
    private String nickname;
    private String topic;
    private List<Post> listaPostari=new ArrayList<>();
    private String descriere;
    private int postId=0;
    private Post post;

    public Community(String topic,String nickname,String descriere, Post post){
        this.topic=topic;
        this.nickname=nickname;
        this.descriere=descriere;
        this.post=post;
    }

    public String getDescriere() {
        return descriere;
    }

    public String getNickname() {
        return nickname;
    }

    public List<Post> getListaPostari(){
        return listaPostari;
    }

    public String descriere(){
        return descriere;
    }

    public void addPost(Post post){
    this.listaPostari.add(post);
    postId++;
    }
}
