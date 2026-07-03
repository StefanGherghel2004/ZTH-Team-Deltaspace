import com.sun.xml.internal.ws.util.StringUtils;

public class Password {

    String password;

    public Password(String password) throws PasswordNumberException,PasswordLengthException,PasswordSpecialCharacterException{

        if (!password.matches(".*\\d.*")) {

            throw new PasswordNumberException();
        }
        else if (!password.matches(".*[\\\\p{Punct}].*")) {

            throw new PasswordSpecialCharacterException();
        }
        else if (password.length() != 14) {

            throw new PasswordLengthException();
        }
        else {
            this.password = password;
        }
    }
}
