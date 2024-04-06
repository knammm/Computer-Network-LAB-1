import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class myServer {
    private static String host;
    private static final int PORT = 12345;

    private static final List<Socket> clients = new ArrayList<>();
    private static final List<String> nicknames = new ArrayList<>();

    private static void sendMessageToAllClients(String message) {
        for (Socket client : clients) {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(message); // sending message to clients
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void handleClient(Socket client) {
        String nickname = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            nickname = reader.readLine();
            nicknames.add(nickname);
            clients.add(client);

            System.out.println("Nickname: " + nickname);

            while (true) {
                String message = reader.readLine();
                if (message != null) {
                    sendMessageToAllClients(message);
                }
            }
        } catch (SocketException e) {
            // Client disconnected unexpectedly
            System.out.println("Client disconnected: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // When client disconnects, remove from lists
            if (nickname != null) {
                int index = nicknames.indexOf(nickname);
                if (index != -1) {
                    nicknames.remove(index);
                    clients.remove(index);
                    sendMessageToAllClients(nickname + " has left the room.");
                }
            }
        }
    }

    private static void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on " + host + ":" + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Connected with " + client.getRemoteSocketAddress());
                
                // Multi-thread programming
                Thread thread = new Thread(() -> handleClient(client));
                thread.start();
                System.out.println("[ACTIVE CONNECTIONS] " + (Thread.activeCount() - 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        startServer();
    }
}
