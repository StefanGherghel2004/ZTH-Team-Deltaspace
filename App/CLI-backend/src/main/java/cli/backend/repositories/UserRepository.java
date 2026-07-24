package cli.backend.repositories;

import cli.backend.User;
import cli.backend.loggers.Logger;

import java.sql.*;
import java.util.Optional;

public class UserRepository {

    private static DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    private static UserRepository instance = null;

    private UserRepository() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(100) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                date_of_birth DATE NOT NULL,
                deleted BOOLEAN DEFAULT FALSE,
                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection connection = databaseConnection.getDatabaseConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            Logger.severe("Failed to initialize users table: " + e.getMessage());
        }
    }

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    public void addUser(User user) {
        String query = "INSERT INTO users (username, email, password, date_of_birth) VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setDate(4, Date.valueOf(user.getDateOfBirth()));

            ps.executeUpdate();
        } catch (SQLException e) {
            Logger.warning("Error adding user: " + e.getMessage());
        }
    }

    public void deleteUserById(Long id) {
        String query = "UPDATE users SET deleted = true WHERE id = ?";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            Logger.warning("Error deleting user: " + e.getMessage());
        }
    }

    public Optional<User> findByUsernameOrEmail(String identifier) {
        String query = "SELECT * FROM users WHERE (username = ? OR email = ?)";
        try (Connection connection = databaseConnection.getDatabaseConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("date_of_birth").toLocalDate()
                    );
                    user.setId(rs.getLong("id"));
                    user.setDeleted(rs.getBoolean("deleted"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            Logger.severe("Error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean updateUser (User user) {
        if(user.getId() == null){
            Logger.severe("Cannot update a user without an ID.");
            return false;
        }
        String updateUserQuery = """
            UPDATE posts 
            SET username = ?,
                email = ?, 
                password = ?,
                date_of_birth = ?, 
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?;
            """;

        try (Connection connection = databaseConnection.getDatabaseConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(updateUserQuery)) {

            preparedStatement.setString(1,user.getUsername());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setDate(4,Date.valueOf(user.getDateOfBirth()));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Logger.info("Successfully updated user with ID: " + user.getId());
                return true;
            } else {
                Logger.info("No user found with ID: " + user.getId() + " to update.");
                return false;
            }

        } catch (SQLException e) {
            Logger.severe("Failed to update user: " + e.getMessage());
            return false;
        }
    }
}