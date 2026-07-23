package cli.backend;
import cli.backend.database.*;
import cli.backend.handlers.AppHandler;
import cli.backend.loggers.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        AppHandler app = AppHandler.getInstance();

        // create tables in order
        UserRepository.getInstance();
        CommunityRepository.getInstance();
        PostRepository.getInstance();
        CommentRepository.getInstance();
        VoteRepository.getInstance();

        Logger.init();
        app.run();
    }
}
