import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static String username;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            System.out.println("Connected to Chat Server.");

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter clientWriter = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            // Send the username to the server
            clientWriter.println(username);

            Thread serverListener = new Thread(() -> {
                try {
                    String message;
                    while ((message = serverReader.readLine()) != null) {
                        clearConsole();
                        // System.out.println(message);
                        System.out.print(username + " > ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();

            String userInput;
            while (true) {
                System.out.print(username + " > ");
                userInput = scanner.nextLine();
                // Send username and message to server
                clientWriter.println(username + " > " + userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to clear the console screen
    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
