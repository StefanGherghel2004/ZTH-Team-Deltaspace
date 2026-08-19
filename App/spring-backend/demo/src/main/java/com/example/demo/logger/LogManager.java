package com.example.demo.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An asynchronous, thread-based log manager.
 * It uses a producer-consumer model where log messages are queued in a {@link BlockingQueue}
 * and processed by this background thread. It also guarantees that all queued messages
 * are flushed to their destinations when the JVM shuts down.
 */
public class LogManager extends Thread {

    private static LogManager instance;

    private final List<Loggable> loggers = new ArrayList<>();

    private final BlockingQueue<LogMessage> logMessages = new LinkedBlockingQueue<>();

    /**
     * Private constructor to enforce the Singleton pattern.
     * Registers a JVM shutdown hook to ensure that the logging thread is interrupted
     * gracefully and has time to flush any remaining messages in the queue before exit.
     */
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

    /**
     * The main execution loop of the logging thread.
     * Continuously takes messages from the blocking queue and dispatches them to the registered loggers.
     * If the thread is interrupted (e.g., during JVM shutdown), it breaks the loop and flushes
     * any remaining messages.
     */
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

    /**
     * Drains all remaining messages from the queue and logs them immediately.
     * This method is typically called when the thread is interrupted during application shutdown
     * to prevent log loss.
     */
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
