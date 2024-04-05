import java.io.*;
import java.net.*;
import java.util.*;

public class server {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Set<String> usernames = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server is running on port " + PORT);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter clientWriter;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                // Get username from client
                username = reader.readLine();
                if (username == null || username.isEmpty() || usernames.contains(username)) {
                    clientWriter.println("Invalid username. Please choose another one.");
                    return;
                }
                usernames.add(username);

                clientWriters.add(clientWriter);
                System.out.println("System announcement: " + username + " has joined the chat.");

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                    broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e);
            } finally {
                if (username != null) {
                    System.out.println("System announcement: " + username + " has left the room !");
                    usernames.remove(username);
                }
                if (clientWriter != null) {
                    clientWriters.remove(clientWriter);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
