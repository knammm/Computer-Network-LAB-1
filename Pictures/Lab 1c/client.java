import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class myClient extends JFrame {
    private static final int PORT = 12345;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private JTextField messageInputField;
    private JTextArea chatDisplayArea;
    private JTextField nicknameInputField;

    public myClient() {
        setTitle("Chatroom Application");
        setSize(600, 400); // Adjusted height
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel for nickname, connect button, and quit button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel nameLabel = new JLabel("Nickname:");
        nicknameInputField = new JTextField(15);

        // Connect button
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer(connectButton);
            }
        });

        topPanel.add(nameLabel);
        topPanel.add(nicknameInputField);
        topPanel.add(connectButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Message area
        chatDisplayArea = new JTextArea();
        chatDisplayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatDisplayArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for message input and send button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageInputField = new JTextField();
        messageInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        bottomPanel.add(messageInputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void connectToServer(JButton connectButton) {
        // Check if the username is empty
        if (nicknameInputField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String nickname = nicknameInputField.getText();
            // Connect to server
            socket = new Socket("localhost", PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            // Send username to server
            writer.println(nickname);
            connectButton.setEnabled(false); // Don't give the user to click the connect button anymore
            // Display username
            chatDisplayArea.append(nickname + " has joined the room.\n");
            // Listen from server
            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageInputField.getText();
        if (!message.isEmpty()) {
            writer.println(nicknameInputField.getText() + ": " + message);
            messageInputField.setText("");
        }
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    final String message = msg;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            chatDisplayArea.append(message + "\n");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new myClient();
            }
        });
    }
}