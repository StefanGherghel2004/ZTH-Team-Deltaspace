package cli.backend.loggers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Loggable {

    private static final String DIR = "./logs/";

    BufferedWriter writer;
    private LogLevel level;

    public FileLogger(LogLevel logLevel, String filename) {
        try {
            File logDir = new File(DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            File logFile = new File(logDir, filename);
            this.writer = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            System.out.println("Error opening file: " + filename);
        }

        this.level = logLevel;
    }


    @Override
    public void log(LogLevel level, String message) {

        if (this.level != level) {
            return;
        }

        try {
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
