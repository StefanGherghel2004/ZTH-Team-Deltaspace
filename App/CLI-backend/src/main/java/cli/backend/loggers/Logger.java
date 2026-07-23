package cli.backend.loggers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private static final LogManager manager = LogManager.getInstance();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void logWithLevel(LogLevel level, String message) {
        manager.log(level, format(level, message));
    }

    private static String format(LogLevel level, String message) {
        String currentTime = LocalDateTime.now().format(TIME_FORMATTER);

        return String.format("[%s] %s: %s", currentTime, level, message);
    }

    private static String addLogLevel(LogLevel level, String message) {
        return level + ":" + message;
    }

    public static void init() {
        List<Loggable> loggers = new ArrayList<>();

        loggers.add(new FileLogger(LogLevel.DEBUG, "debug.txt"));
        loggers.add(new FileLogger(LogLevel.Logger, "severe.txt"));
        loggers.add(new FileLogger(LogLevel.INFO, "info.txt"));
        loggers.add(new FileLogger(LogLevel.WARNING, "warning.txt"));

        LogManager.getInstance().addLoggers(loggers);
    }

    public static void debug(String message) {
        logWithLevel(LogLevel.DEBUG, message);
    }

    public static void info(String message) {
        logWithLevel(LogLevel.INFO, message);
    }

    public static void warning(String message) {
        logWithLevel(LogLevel.WARNING, message);
    }

    public static void severe(String message) {
        logWithLevel(LogLevel.Logger, message);
    }

}
