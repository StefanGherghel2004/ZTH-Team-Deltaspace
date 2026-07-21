package cli.backend.database;
import cli.backend.Comment;
import cli.backend.loggers.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    private static CommentRepository instance = null;
    private static final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    private static final String createTableQuery = """
        CREATE TABLE IF NOT EXISTS comments (
            id SERIAL PRIMARY KEY,
            post_id BIGINT NOT NULL,
            author_username VARCHAR(100) NOT NULL,
            comment_text TEXT NOT NULL,
            parent_id BIGINT DEFAULT 0,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT fk_post FOREIGN KEY(post_id) REFERENCES posts(id) ON DELETE CASCADE
        );
        """;

    private static final String addCommentQuery = """
        INSERT INTO comments (post_id, author_username, comment_text, parent_id)
        VALUES (?, ?, ?, ?);
        """;

    private static final String updateCommentQuery = "UPDATE comments SET comment_text = ? WHERE id = ?;";

    private static final String deleteCommentQuery = "DELETE FROM comments WHERE id = ?;";
    private static final String selectByPostQuery = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC;";

    private CommentRepository() {
        try (Connection connection = databaseConnection.getDatabaseConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
            Logger.info("Comments table verified/created successfully.");
        } catch (Exception e) {
            Logger.severe("Failed to initialize comments table: " + e.getMessage());
        }
    }

    public static CommentRepository getInstance() {
        if (instance == null)
            instance = new CommentRepository();
        return instance;
    }

    public void addComment(Comment comment) {
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addCommentQuery)) {

            preparedStatement.setLong(1, comment.getPostId());
            preparedStatement.setString(2, comment.getAuthorUsername());
            preparedStatement.setString(3, comment.getText());
            preparedStatement.setLong(4, comment.getIdParent());

            preparedStatement.executeUpdate();
            Logger.info("Successfully added comment by " + comment.getAuthorUsername());
        } catch (SQLException e) {
            Logger.severe("Failed to add comment: " + e.getMessage());
        }
    }

    public List<Comment> findCommentsByPostId(Long postId) {
        List<Comment> comments = new ArrayList<>();
        if (postId == null) return comments;

        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectByPostQuery)) {

            preparedStatement.setLong(1, postId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(mapRowToComment(resultSet));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to fetch comments for post ID " + postId + ": " + e.getMessage());
        }
        return comments;
    }

    public void deleteCommentById(Long commentId) {
        if (commentId == null) {
            Logger.severe("Cannot delete a comment using a null ID.");
            return;
        }
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteCommentQuery)) {

            preparedStatement.setLong(1, commentId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Logger.info("Successfully deleted comment ID: " + commentId);
            } else {
                Logger.info("No comment found with ID: " + commentId + " to delete.");
            }
        } catch (SQLException e) {
            Logger.severe("Failed to delete comment: " + e.getMessage());
        }
    }

    private Comment mapRowToComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment(
                resultSet.getString("comment_text"),
                resultSet.getString("author_username"),
                resultSet.getLong("post_id")
        );

        comment.setId(resultSet.getLong("id"));
        comment.setIdParent(resultSet.getLong("parent_id"));

        return comment;
    }

    public Comment findCommentById(Long commentId) {
        if (commentId == null) {
            return null;
        }

        String selectByIdQuery = "SELECT * FROM comments WHERE id = ?;";

        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectByIdQuery)) {

            preparedStatement.setLong(1, commentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToComment(resultSet);
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to fetch comment by ID " + commentId + ": " + e.getMessage());
        }

        return null;
    }

    public void updateComment(Comment comment) {
        if (comment == null || comment.getId() == null) {
            Logger.severe("Cannot update a comment without a valid ID.");
            return;
        }

        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateCommentQuery)) {

            preparedStatement.setString(1, comment.getText());
            preparedStatement.setLong(2, comment.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Logger.info("Successfully updated comment ID: " + comment.getId());
            } else {
                Logger.info("No comment found with ID: " + comment.getId() + " to update.");
            }
        } catch (SQLException e) {
            Logger.severe("Failed to update comment ID " + comment.getId() + ": " + e.getMessage());
        }
    }
}