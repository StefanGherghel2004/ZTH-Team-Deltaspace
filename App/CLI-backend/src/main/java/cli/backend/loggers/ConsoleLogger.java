package cli.backend.loggers;

public class ConsoleLogger implements Loggable {

    private LogLevel level;

    public ConsoleLogger(LogLevel logLevel) {
        this.level = logLevel;
    }

    @Override
    public void log(LogLevel level, String message) {

        if (this.level != level) {
            return;
        }
        System.out.println(message);
    }
}
