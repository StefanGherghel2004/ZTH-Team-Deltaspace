public class Username {

    private String username;
    private boolean usernameFormatError;

    public Username (String username) {

        //Checking if the username passed in the constructor is the correct format
        if (!Username.checkUsername(username)) {

            this.usernameFormatError = true;
        }
        else {

            this.username = username;
            this.usernameFormatError = false;
        }
    }

    public void setUsername(String username) {

        if (!Username.checkUsername(username)) {

            this.usernameFormatError = true;
        }
        else {
            this.username = username;
            this.usernameFormatError = false;
        }
    }

    public String getUsername () {

        if (this.username != null) {
            return this.username;
        }
        else
            return null;
    }

    public boolean getUsernameFormatError () {

        return this.usernameFormatError;
    }

    public static boolean checkUsername (String username) {

        if (!username.matches("^[a-zA-Z0-9._-]{4,20}$")) {
            System.out.println("Username must contain only alphanumerical characters,it must" +
                    "be between 4 and 20 digits and can only contain periods, underscores, " +
                    "and hyphens.");
            return false;
        }
        else {
            return true;
        }
    }
}
