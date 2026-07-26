package cli.backend.repositories;

import cli.backend.loggers.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String POSTGRES_URL = System.getenv("POSTGRES_URL");
    private static final String POSTGRES_USERNAME = System.getenv("POSTGRES_USERNAME");
    private static final String POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD");
    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";

    private static DatabaseConnection instance = null;

    private DatabaseConnection(){}

    public static DatabaseConnection getInstance() {

        if(instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    public Connection getDatabaseConnection() {
        try {
            if (POSTGRES_URL == null || POSTGRES_USERNAME == null || POSTGRES_PASSWORD == null)
                throw new IllegalArgumentException("Set your environment variables for POSTGRES!");

            Class.forName(POSTGRES_DRIVER);
            Connection databaseConnection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USERNAME, POSTGRES_PASSWORD);
            Logger.info("Database connection was successful!");
            return databaseConnection;
        } catch (Exception e ){
            Logger.warning("Database connection failed!");
            Logger.warning(e.getMessage());
            return null;
        }
    }
}
