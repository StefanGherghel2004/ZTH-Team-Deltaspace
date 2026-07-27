package cli.backend.exceptions;

public class BackNavigationException extends RuntimeException {
    public BackNavigationException() {
        super("\nAction cancelled. Returning to previous menu...");
    }
}
