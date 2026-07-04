public class Password {

    String password;

    public Password(String password) throws PasswordFormatException {

        if (Password.checkPassword(password)) {

            this.password = password;
        }
        else {

            throw new PasswordFormatException();
        }
    }

    public void setPassword(String password) throws PasswordFormatException {

        if (Password.checkPassword(password)) {

            this.password = password;
        }
        else {

            throw new PasswordFormatException();
        }
    }

    public static boolean checkPassword (String password) {

        return password != null && password.matches("(?=.*\\d)(?=.*\\p{Punct}).{14,}");
    }

    public String toString () {

        return this.password;
    }
}
