public class PasswordLengthException extends Exception {

    public PasswordLengthException () {

        super("Password must be at least 14 characters long.");
    }
}
