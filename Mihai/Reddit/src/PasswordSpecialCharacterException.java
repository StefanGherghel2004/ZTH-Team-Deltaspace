public class PasswordSpecialCharacterException extends Exception {

    public PasswordSpecialCharacterException () {

        super("Password must contain at least one special character.");
    }
}
