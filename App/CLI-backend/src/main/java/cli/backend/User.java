package cli.backend;

public class User {

    private int userID;
    static int userCounter = 0;

    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        userID = userCounter++;
    }

    public String getUsername() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getUserID() {
        return userID;
    }
}
