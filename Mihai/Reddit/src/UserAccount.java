//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class UserAccount {

    private Username username;
    private Password password;
    private int userId;
    private static int userIdIncrementation = 0;


    public UserAccount (String username, String password) throws UsernameFormatException, PasswordFormatException {

        this.username = new Username(username);
        this.password = new Password(password);
        this.userId = userIdIncrementation;
        UserAccount.userIdIncrementation ++;
    }

    public String getUsername () {

        return this.username.getUsername();
    }

    public String getPassword () {

        return this.password.toString();
    }

    public int getUserId () {

        return userId;
    }

    public static void setStartingId (int startingId) {

        UserAccount.userIdIncrementation = startingId;
    }
}