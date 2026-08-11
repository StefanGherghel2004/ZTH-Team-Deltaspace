package com.example.demo.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private static final LogManager manager = LogManager.getInstance();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void logWithLevel(LogLevel level, String message, Object... args) {
        manager.addMessage(new LogMessage(level, format(level, message, args)));
    }

    private static String format(LogLevel level, String message, Object[] args) {
        String currentTime = LocalDateTime.now().format(TIME_FORMATTER);

        String formattedMessage = String.format(message, args);

        return String.format("[%s] %s: %s", currentTime, level, formattedMessage);
    }

    public static void init() {
        List<Loggable> loggers = new ArrayList<>();

        loggers.add(new FileLogger(LogLevel.DEBUG, "debug.txt"));
        loggers.add(new FileLogger(LogLevel.SEVERE, "severe.txt"));
        loggers.add(new FileLogger(LogLevel.INFO, "info.txt"));
        loggers.add(new FileLogger(LogLevel.WARNING, "warning.txt"));

        loggers.add(new ConsoleLogger(LogLevel.DEBUG));

        manager.addLoggers(loggers);
        manager.start(); // start logging thread
    }

    public static void debug(String message, Object... args) {
        logWithLevel(LogLevel.DEBUG, message, args);
    }

    public static void info(String message, Object... args) {
        logWithLevel(LogLevel.INFO, message, args);
    }

    public static void warning(String message, Object... args) {
        logWithLevel(LogLevel.WARNING, message, args);
    }

    public static void severe(String message, Object... args) {
        logWithLevel(LogLevel.SEVERE, message, args);
    }

}
