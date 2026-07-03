public class UsernameFormatException extends Exception {

    public UsernameFormatException () {

        super("Username must contain only alphanumerical characters,it must " +
                "be between 4 and 20 digits and can only contain periods, underscores, " +
                "and hyphens.");
    }
}
