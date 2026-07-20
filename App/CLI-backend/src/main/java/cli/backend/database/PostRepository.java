package cli.backend.database;

import cli.backend.Post;
import cli.backend.loggers.ConsoleLogger;

import java.sql.*;

import static cli.backend.loggers.LogLevel.INFO;
import static cli.backend.loggers.LogLevel.SEVERE;

public class PostRepository {

    private static DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    private static PostRepository instance = null;
    private static ConsoleLogger consoleLogger;

    private PostRepository() {
        String createTableQuery = """
        CREATE TABLE IF NOT EXISTS posts (
            id SERIAL PRIMARY KEY,
            author_username VARCHAR(100) NOT NULL,
            post_title VARCHAR(255) NOT NULL,
            post_contents TEXT,
            image_link VARCHAR(500),
            community_name VARCHAR(100),
            nsfw BOOLEAN DEFAULT FALSE,
            updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );
        """;

        try {
            Connection connection = databaseConnection.getDatabaseConnection();
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableQuery);
                consoleLogger.setLevel(INFO);
                consoleLogger.log(INFO,"Posts table verified/created successfully.");
            }
        } catch (Exception e) {
            consoleLogger.setLevel(SEVERE);
            consoleLogger.log(SEVERE,"Failed to initialize posts table:");
            consoleLogger.log(SEVERE,e.getMessage());
        }
    }

    public static PostRepository getInstance() {
        if(instance == null)
            instance = new PostRepository();
        return instance;
    }

    public void addPost(Post post) {
        String addPostQuery = """
            INSERT INTO posts (author_username, post_title, post_contents, image_link, community_name, nsfw)
            VALUES (?, ?, ?, ?, ?, ?);
            """;
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addPostQuery)) {

            preparedStatement.setString(1, post.getAuthorUsername());
            preparedStatement.setString(2, post.getPostTitle());
            preparedStatement.setString(3, post.getPostContents());
            preparedStatement.setString(4, post.getImageLink());
            preparedStatement.setString(5, post.getCommunityName());
            preparedStatement.setBoolean(6, post.isNSFW());

            preparedStatement.executeUpdate();
            consoleLogger.setLevel(INFO);
            consoleLogger.log(INFO, "Successfully added new post: " + post.getPostTitle());
        } catch (SQLException e) {
            consoleLogger.setLevel(SEVERE);
            consoleLogger.log(SEVERE, "Failed to add post: " + e.getMessage());
        }
    }
}
