package cli.backend;

import java.util.ArrayList;
import java.util.List;

public class Community {
    private String nickname;
    private String topic;
    private List<Post> listaPostari=new ArrayList<>();
    private String descriere;
    private int postId=0;


    public Community(String topic,String nickname,String descriere){
        this.topic=topic;
        this.nickname=nickname;
        this.descriere=descriere;

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


    public void addPost(Post post){
    this.listaPostari.add(post);
    postId++;
    }
}
