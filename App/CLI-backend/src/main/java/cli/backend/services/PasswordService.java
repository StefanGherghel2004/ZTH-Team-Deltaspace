package cli.backend.services;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {

    // BCrypt cost factor (2^ROUNDS iterations).
    private final static int LOG_ROUNDS = 12;

    public static String hash(String password) {

        String salt = BCrypt.gensalt(LOG_ROUNDS);

        return BCrypt.hashpw(password, salt);
    }

    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}
