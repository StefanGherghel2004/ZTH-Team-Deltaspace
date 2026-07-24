package cli.backend.services;

import cli.backend.Post;
import cli.backend.User;
import cli.backend.repositories.PostRepository;
import cli.backend.repositories.VoteRepository;

public class VoteService {
    private static VoteService instance;
    private static PostRepository postRepository = PostRepository.getInstance();
    private static VoteRepository voteRepository = VoteRepository.getInstance();

    public static VoteService getInstance(){
        if(instance==null){
            instance=new VoteService();
        }
        return  instance;
    }

    public void upVote(Post currentPost, User currentUser) {
        Integer currentVote = voteRepository.getVoteValue(currentPost.getId(),currentUser.getId());
        if(currentVote==null){
            currentPost.setUpVotes(currentPost.getUpVotes()+1);
            voteRepository.insertVote(currentPost.getId(),currentUser.getId(),1);
        }
        else if(currentVote==1){
            currentPost.setUpVotes(currentPost.getUpVotes()-1);
            voteRepository.deleteVote(currentPost.getId(),currentUser.getId());
        }

        else {
            currentPost.setUpVotes(currentPost.getUpVotes()+1);
            currentPost.setDownVotes(currentPost.getDownVotes()-1);
            voteRepository.modifyVote(currentPost.getId(),currentUser.getId(),1);
        }
        postRepository.modifyUpVote(currentPost.getId(),currentPost.getUpVotes());
        postRepository.modifyDownVote(currentPost.getId(),currentPost.getDownVotes());
    }

    public void downVote(Post currentPost, User currentUser) {
        Integer currentVote = voteRepository.getVoteValue(currentPost.getId(), currentUser.getId());

        if (currentVote == null) {
            currentPost.setDownVotes(currentPost.getDownVotes() + 1);
            voteRepository.insertVote(currentPost.getId(), currentUser.getId(), -1);

        } else if (currentVote == -1) {

            currentPost.setDownVotes(currentPost.getDownVotes()-1);
            voteRepository.deleteVote(currentPost.getId(),currentUser.getId());
        } else {
            currentPost.setUpVotes(currentPost.getUpVotes() - 1);
            currentPost.setDownVotes(currentPost.getDownVotes() + 1);
            voteRepository.modifyVote(currentPost.getId(), currentUser.getId(), -1);
        }

        postRepository.modifyUpVote(currentPost.getId(), currentPost.getUpVotes());
        postRepository.modifyDownVote(currentPost.getId(), currentPost.getDownVotes());
    }

    public Integer getUserVoteOnPost(Post post, User user) {
        return voteRepository.getUserVote(post.getId(), user.getId());
    }

    public java.util.Map<Long, Integer> getAllUserVotes(User user) {
        return voteRepository.getAllVotesForUser(user.getId());
    }


}
