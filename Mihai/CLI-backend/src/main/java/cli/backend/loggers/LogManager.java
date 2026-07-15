package cli.backend.loggers;

import java.util.ArrayList;
import java.util.List;

public class LogManager {

    private static LogManager instance;

    private final List<Loggable> loggers = new ArrayList<>();

    private LogManager() {

    }

    public static LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }

        return instance;
    }

    public void addLoggers(List<Loggable> loggers) {
        this.loggers.addAll(loggers);
    }

    public void log(LogLevel level, String message) {
        for (Loggable logger : loggers) {
            logger.log(level, message);
        }
    }

}
