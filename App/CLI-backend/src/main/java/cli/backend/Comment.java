package cli.backend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long id;
    private Long postId;
    private String authorUsername;
    private String text;
    private Long idParent = 0L;

    public Comment(String text, String authorUsername, Long postId){
        this.text = text;
        this.authorUsername = authorUsername;
        this.postId = postId;
    }
}
