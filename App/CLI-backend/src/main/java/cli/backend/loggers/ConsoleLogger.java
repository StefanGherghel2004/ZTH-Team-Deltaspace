package cli.backend.loggers;

public class ConsoleLogger implements Loggable {
    @Override
    public void log(LogLevel level, String message) {

        System.out.println(message);
    }
}
