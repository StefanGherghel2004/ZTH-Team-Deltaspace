package com.example.demo.logger;

import com.example.demo.logger.LogManager;

import java.util.ArrayList;
import java.util.List;

public class Logger {

    private static final LogManager manager = LogManager.getInstance();

    private static void logWithLevel(LogLevel level, String message) {
        manager.log(level, addLogLevel(level, message));
    }

    private static String addLogLevel(LogLevel level, String message) {
        return level + ":" + message;
    }

    public static void init() {
        List<Loggable> loggers = new ArrayList<>();

        loggers.add(new FileLogger(LogLevel.DEBUG, "debug.txt"));
        loggers.add(new FileLogger(LogLevel.SEVERE, "severe.txt"));
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
        logWithLevel(LogLevel.SEVERE, message);
    }

}
