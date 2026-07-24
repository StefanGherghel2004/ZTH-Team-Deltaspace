package cli.backend.services;
import cli.backend.Community;
import cli.backend.Post;
import cli.backend.User;
import cli.backend.commands.CheckImage;
import cli.backend.config.S3ClientFactory;
import cli.backend.repositories.PostRepository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.List;

public class PostService {

    private static PostService instance;
    private final PostRepository postRepository = PostRepository.getInstance();

    private final ImageEditService imageEditService;
    private final S3ImageService s3ImageService;

    private PostService() {
        S3Client s3Client = S3ClientFactory.createS3Client();
        this.imageEditService = new ImageEditService();
        this.s3ImageService = new S3ImageService(s3Client, imageEditService);
    }

    public static synchronized PostService getInstance(){
        if(instance == null){
            instance = new PostService();
        }
        return instance;
    }

    public Post addPost(String authorUsername, String postTitle, String postContents,
                        String imagePath, String imageFilter, boolean NSFW, Community
                                currentCommunity) throws IOException {
        String targetName = (currentCommunity != null) ? currentCommunity.getNickname() : null;

        MultipartFile file = CheckImage.getInstance().convertToMultipartFile(imagePath);
        String imageLink = s3ImageService.uploadImage(file, imageFilter);
        Post newPost = new Post(authorUsername, postTitle, postContents,
                imageLink, NSFW, targetName, 0, 0);

        postRepository.addPost(newPost);
        return newPost;
    }

    public Post findPostById(Long id){
        if (id == null)
            return null;
        return postRepository.findById(id);
    }

    public void deletePost(Post post) {
        if (post == null || post.getId() == null) return;
        postRepository.deletePostById(post.getId());
    }

    public boolean canUserDeletePost(User user, Post post, Community community) {
        if (user == null || post == null) return false;
        if (post.getAuthorUsername().equals(user.getUsername())) return true;

        return community != null && community.getCommunityCreator().equals(user.getUsername());
    }

    public boolean canUserEditPost (User user, Post post) {
        if (user == null || post == null)
            return false;
        return post.getAuthorUsername().equals(user.getUsername());
    }

    public List<Post> getFeedFromRepository () {
        return postRepository.findAll();
    }
}