public class PasswordFormatException extends Exception {

    public PasswordFormatException () {

        super("Password must be at least 14 characters long, must contain at least 1 digit\n" +
                "and must contain at least one special character.");
    }
}
