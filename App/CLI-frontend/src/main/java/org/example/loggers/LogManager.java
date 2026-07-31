package org.example.loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogManager extends Thread {

    private static LogManager instance;

    private final List<Loggable> loggers = new ArrayList<>();

    private final BlockingQueue<LogMessage> logMessages = new LinkedBlockingQueue<>();

    private LogManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.interrupt(); // will wake the thread

            try {
                // plenty of time to flush remaining messages
                this.join(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

    }

    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }

        return instance;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                LogMessage msg = logMessages.take();
                log(msg.level(), msg.message());

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }
        }

        flushMessages();
    }

    private void flushMessages() {
        List<LogMessage> remainingMessages = new ArrayList<>();

        logMessages.drainTo(remainingMessages);
        log(LogLevel.DEBUG, "Remaining messages to flush - " + remainingMessages.size());

        for (LogMessage msg : remainingMessages) {
            log(msg.level(), msg.message());
        }
    }

    public void addLoggers(List<Loggable> loggers) {
        this.loggers.addAll(loggers);
    }

    public void addMessage(LogMessage message) {

        try {
            logMessages.put(message);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public void log(LogLevel level, String message) {
        for (Loggable logger : loggers) {
            logger.log(level, message);
        }
    }

}
