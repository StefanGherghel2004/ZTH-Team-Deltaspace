package cli.backend.database;

import cli.backend.Post;
import cli.backend.loggers.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private static final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    private static PostRepository instance = null;

    private static final String createTableQuery = """
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

    private static final String addPostQuery = """
            INSERT INTO posts (author_username, post_title, post_contents, image_link, community_name, nsfw)
            VALUES (?, ?, ?, ?, ?, ?);
            """;

    private static final String updatePostQuery = """
        UPDATE posts 
        SET post_title = ?, 
            post_contents = ?, 
            image_link = ?, 
            community_name = ?, 
            nsfw = ?, 
            updated_at = CURRENT_TIMESTAMP
        WHERE id = ?;
        """;

    private static final String deletePostByIdQuery = "DELETE FROM posts WHERE id = ?;";

    private PostRepository() {
        try {
            Connection connection = databaseConnection.getDatabaseConnection();
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableQuery);
                Logger.info("Posts table verified/created successfully.");
            }
        } catch (Exception e) {
            Logger.severe("Failed to initialize posts table:" + e.getMessage());
        }
    }

    public static PostRepository getInstance() {
        if(instance == null)
            instance = new PostRepository();
        return instance;
    }

    public void addPost(Post post) {
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addPostQuery,
                     Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, post.getAuthorUsername());
            preparedStatement.setString(2, post.getPostTitle());
            preparedStatement.setString(3, post.getPostContents());
            preparedStatement.setString(4, post.getImageLink());
            preparedStatement.setString(5, post.getCommunityName());
            preparedStatement.setBoolean(6, post.isNSFW());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getLong(1));
                }
            }

            Logger.info("Successfully added new post: " + post.getPostTitle());

        } catch (SQLException e) {
            Logger.severe("Failed to add post: " + e.getMessage());
        }
    }

    public void updatePost(Post post) {
        if (post.getId() == null) {
            Logger.severe("Cannot update a post without an ID.");
            return;
        }

        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updatePostQuery)) {

            preparedStatement.setString(1, post.getPostTitle());
            preparedStatement.setString(2, post.getPostContents());
            preparedStatement.setString(3, post.getImageLink());
            preparedStatement.setString(4, post.getCommunityName());
            preparedStatement.setBoolean(5, post.isNSFW());

            preparedStatement.setLong(6, post.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Logger.info("Successfully updated post ID: " + post.getId());
            } else {
                Logger.info("No post found with ID: " + post.getId() + " to update.");
            }
        } catch (SQLException e) {
            Logger.severe("Failed to update post: " + e.getMessage());
        }
    }

    public void deletePostById(Long postId) {
        if (postId == null) {
            Logger.severe("Cannot delete a post using a null ID.");
            return;
        }
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deletePostByIdQuery)) {

            preparedStatement.setLong(1, postId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Logger.info("Successfully deleted post ID: " + postId);
            } else {
                Logger.info("No post found with ID: " + postId + " to delete.");
            }
        } catch (SQLException e) {
            Logger.severe("Failed to delete post: " + e.getMessage());
        }
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM posts ORDER BY created_at DESC;";

        try (Connection connection = databaseConnection.getDatabaseConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectAllQuery)) {

            while (resultSet.next()) {
                Post post = new Post(
                        resultSet.getString("author_username"),
                        resultSet.getString("post_title"),
                        resultSet.getString("post_contents"),
                        resultSet.getString("image_link"),
                        resultSet.getBoolean("nsfw"),
                        resultSet.getString("community_name")
                );

                post.setId(resultSet.getLong("id"));
                posts.add(post);
            }
        } catch (SQLException e) {
            Logger.severe("Failed to fetch all posts: " + e.getMessage());
        }
        return posts;
    }

    public Post findById(Long id) {
        if (id == null) {
            return null;
        }
        String selectQuery = "SELECT * FROM posts WHERE id = ?;";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Post post = new Post(
                            resultSet.getString("author_username"),
                            resultSet.getString("post_title"),
                            resultSet.getString("post_contents"),
                            resultSet.getString("image_link"),
                            resultSet.getBoolean("nsfw"),
                            resultSet.getString("community_name")
                    );

                    post.setId(resultSet.getLong("id"));
                    return post;
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to fetch post by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Post> getCommunityPosts(String communityName){
        String searchPosts= "Select * from public.posts where community_name=?;";
        List<Post> communityPosts = new ArrayList<>();
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(searchPosts)) {
            preparedStatement.setString(1,communityName);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                   Post post =new Post(
                            resultSet.getString("author_username"),
                            resultSet.getString("post_title"),
                            resultSet.getString("post_contents"),
                            resultSet.getString("image_link"),
                            resultSet.getBoolean("nsfw"),
                            resultSet.getString("community_name")

                    );
                    post.setId(resultSet.getLong("id"));
                    communityPosts.add(post);
                }

            }
        }catch (SQLException e) {
            Logger.severe("Failed to find Post: " + e.getMessage());
        }
        return communityPosts;
    }
}
