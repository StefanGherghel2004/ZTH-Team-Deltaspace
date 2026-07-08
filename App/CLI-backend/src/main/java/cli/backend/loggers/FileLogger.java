package cli.backend.loggers;

import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Loggable {
    FileWriter writer;
    private LogLevel level;

    public FileLogger(LogLevel logLevel, String filename) {
        try {
            writer = new FileWriter(filename);
        } catch (IOException e) {
            System.out.println("Error opening file: " + filename);
        }

        this.level = logLevel;
    }


    @Override
    public void log(LogLevel level, String message) {
        try {
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
