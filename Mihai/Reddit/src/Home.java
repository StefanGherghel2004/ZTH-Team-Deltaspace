import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Home {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username.");

        Username username = null;
        while(username == null) {
            System.out.print("Username:");
            String inputUsername = scanner.nextLine();
            try {
                username = new Username(inputUsername);
            } catch (UsernameFormatException ignore) {

            }
        }

    }
}
