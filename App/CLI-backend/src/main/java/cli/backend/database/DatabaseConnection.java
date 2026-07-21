package cli.backend.database;

import cli.backend.loggers.ConsoleLogger;
import cli.backend.loggers.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import static cli.backend.loggers.LogLevel.INFO;
import static cli.backend.loggers.LogLevel.WARNING;

public class DatabaseConnection {

    private static final String jdbcURL = "jdbc:postgresql://localhost:5432/deltaspacedb";
    private static final String username = System.getenv("POSTGRES_USERNAME");
    private static final String password = System.getenv("POSTGRES_PASSWORD");
    private static final String dbDriver = "org.postgresql.Driver";

    private static DatabaseConnection instance = null;

    private DatabaseConnection(){}

    public static DatabaseConnection getInstance() {

        if(instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    public Connection getDatabaseConnection() {
        try {
            Class.forName(dbDriver);
            Connection databaseConnection = DriverManager.getConnection(jdbcURL, username, password);
            Logger.info("Database connection was successful!");
            return databaseConnection;
        } catch (Exception e ){
            Logger.warning("Database connection failed!");
            Logger.warning(e.getMessage());
            return null;
        }
    }
}
