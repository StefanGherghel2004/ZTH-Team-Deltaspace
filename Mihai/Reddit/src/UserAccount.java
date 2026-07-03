//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class UserAccount {

    private Username username;
    private Password password;


    public UserAccount (String username, String password) throws UsernameFormatException, PasswordSpecialCharacterException, PasswordLengthException, PasswordNumberException{

        this.username = new Username(username);
        this.password = new Password(password);
    }

    public String getUsername () {

        return this.username.getUsername();
    }

    public void setPassword (String password) {

    }
}