package cli.backend.exceptions;

public class InvalidUserAccountException extends Exception {
    public InvalidUserAccountException() {

      super("Invalid username/email or password.");
    }
}
