package cli.backend.exceptions;

public class DeletedAccountException extends Exception {
    public DeletedAccountException() {
        super("This account is deleted.");
    }
}
