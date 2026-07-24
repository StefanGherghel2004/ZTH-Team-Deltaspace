package cli.backend.repositories;

import cli.backend.loggers.Logger;

import java.sql.*;
import java.util.Map;

public class VoteRepository {
    private static DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    private static VoteRepository instance;

    private VoteRepository() {
        String query = """
                CREATE TABLE if not exists votes(
                user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                post_id    BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                value      SMALLINT NOT NULL CHECK (value IN (-1, 1)),
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (user_id, post_id)
                );
                """;

        try {
            Connection connection = databaseConnection.getDatabaseConnection();
            try (Statement statement = connection.createStatement()) {
                statement.execute(query);
                Logger.info("Community table verified/created successfully.");
            }
        } catch (Exception e) {
            Logger.severe("Failed to initialize votes table:" + e.getMessage());
        }
    }

    public static VoteRepository getInstance() {
        if (instance == null) {
            instance = new VoteRepository();
        }
        return instance;
    }

    public Integer getVoteValue(long postId, long userId) {
        String query = "Select value from votes where user_id=? and post_id=?";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("value");
                }
                return null;
            }
        } catch (SQLException e) {
            Logger.severe("Failed to fetch vote: " + e.getMessage());
            return null;
        }

    }

    public boolean modifyVote(long postId, long userId, int value) {
        String query = "update votes set value=?, created_at = CURRENT_TIMESTAMP where user_id=? and post_id=?";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, value);
            statement.setLong(2, userId);
            statement.setLong(3, postId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Logger.severe("Failed to update vote: " + e.getMessage());
            return false;
        }
    }

    public boolean insertVote(long postId, long userId, int value) {
        String query = "insert into votes (user_id,post_id,value) values(?,?,?);";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, postId);
            statement.setInt(3, value);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Logger.severe("Failed to insert vote: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVote(long postId, long userId) {
        String query = "delete from votes where user_id=? and post_id=?;";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setLong(2, postId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            Logger.severe("Failed to delete vote: " + e.getMessage());
            return false;
        }
    }

    public Integer getUserVote(long postId,long userId){
        String query="select value from votes where post_id=? and user_id=?;";
        try(Connection connection = databaseConnection.getDatabaseConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
                statement.setLong(1,postId);
                statement.setLong(2,userId);
                try(ResultSet resultSet= statement.executeQuery()){
                    if(resultSet.next()){
                        return  resultSet.getInt("Value");
                    }
                }
                return null;
        }catch (SQLException e){
            Logger.severe("Failed to get vote : "+ e.getMessage());
            return null;
        }
    }

    public Map<Long, Integer> getAllVotesForUser(long userId) {
        java.util.Map<Long, Integer> userVotes = new java.util.HashMap<>();
        String query = "SELECT post_id, value FROM votes WHERE user_id = ?";

        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    userVotes.put(resultSet.getLong("post_id"), resultSet.getInt("value"));
                }
            }
        } catch (SQLException e) {
            Logger.severe("Failed to fetch all user votes: " + e.getMessage());
        }
        return userVotes;
    }
}
