package lk.ijse.chatapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    @FXML private TextArea logArea;
    @FXML private TextField messageField;
    @FXML private Button sendButton;

    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private List<ClientHandler> clients = new ArrayList<>(); // Store all connected clients
    private int clientId = 1; // Give each client a number

    @FXML
    public void initialize() {
        sendButton.setDisable(true);
        messageField.setDisable(true);
        messageField.setOnAction(event -> sendMessage(null));
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3000);
                isRunning = true;

                javafx.application.Platform.runLater(() -> {
                    logArea.appendText(" Server Started on port 3000\n");
                    logArea.appendText(" Waiting for clients...\n");
                    sendButton.setDisable(false);
                    messageField.setDisable(false);
                });

                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();

                    // Create a new handler for each client
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientId++);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();

                    int clientCount = clients.size();
                    javafx.application.Platform.runLater(() -> {
                        logArea.appendText(" New client connected! Total clients: " + clientCount + "\n");
                    });
                }

            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    logArea.appendText(" Error: " + e.getMessage() + "\n");
                });
            }
        }).start();
    }

    // Broadcast message to ALL clients except sender
    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Broadcast server message to ALL clients
    private void broadcastToAll(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    @FXML
    void sendMessage(ActionEvent event) {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && isRunning) {
            String serverMessage = "Server: " + message;
            logArea.appendText(serverMessage + "\n");
            broadcastToAll(serverMessage); // Send to all clients
            messageField.clear();
        }
    }

    // Inner class to handle each client in its own thread
    private class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        private int clientId;
        private String clientName;

        public ClientHandler(Socket socket, int id) {
            this.socket = socket;
            this.clientId = id;
            this.clientName = "Client " + id;

            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Welcome message for this client
                output.writeUTF("Welcome " + clientName + "!");
                output.flush();

                // Notify everyone that new client joined
                broadcast(clientName + " joined the chat!", this);

                // Listen for messages from this client
                while (true) {
                    String message = input.readUTF();
                    String formattedMessage = clientName + ": " + message;

                    javafx.application.Platform.runLater(() -> {
                        logArea.appendText(formattedMessage + "\n");
                    });

                    // Send to ALL other clients
                    broadcast(formattedMessage, this);
                }

            } catch (IOException e) {
                // Client disconnected
                javafx.application.Platform.runLater(() -> {
                    logArea.appendText("✗ " + clientName + " disconnected\n");
                    logArea.appendText("✓ Total clients: " + clients.size() + "\n");
                });
                // Notify everyone that client left
                broadcast(clientName + " left the chat!", this);
                clients.remove(this);
            }
        }

        public void sendMessage(String message) {
            try {
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}