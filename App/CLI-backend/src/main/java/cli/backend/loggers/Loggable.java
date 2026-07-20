package cli.backend.loggers;

public interface Loggable {
    void log(LogLevel level, String message);
}
