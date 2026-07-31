package com.example.demo.logger;

public class ConsoleLogger implements Loggable {

    private LogLevel level;

    public ConsoleLogger(LogLevel logLevel) {
        this.level = logLevel;
    }

    @Override
    public void log(LogLevel level, String message) {

        if (level.ordinal() >= this.level.ordinal()) {
            System.out.println(message);
        }
    }
}
