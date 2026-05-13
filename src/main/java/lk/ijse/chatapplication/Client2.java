package lk.ijse.chatapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client2 {
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private Button sendButton;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean isConnected = false;

    @FXML
    public void initialize() {
        sendButton.setDisable(true);
        messageField.setDisable(true);
        messageField.setOnAction(event -> sendMessage(null));
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("127.0.0.1", 3000);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                String welcomeMessage = dataInputStream.readUTF();
                javafx.application.Platform.runLater(() -> {
                    chatArea.appendText(welcomeMessage + "\n");
                    sendButton.setDisable(false);
                    messageField.setDisable(false);
                    chatArea.appendText("✓ Connected to group chat!\n");
                    chatArea.appendText("━━━━━━━━━━━━━━━━━━━━━━\n");
                });

                isConnected = true;

                while (isConnected) {
                    String serverMessage = dataInputStream.readUTF();
                    javafx.application.Platform.runLater(() -> {
                        chatArea.appendText(serverMessage + "\n");
                    });
                }

            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    chatArea.appendText("✗ Error: " + e.getMessage() + "\n");
                    reconnect();
                });
            }
        }).start();
    }

    private void reconnect() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(this::connectToServer);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @FXML
    void sendMessage(ActionEvent event) {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && dataOutputStream != null && isConnected) {
            try {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                messageField.clear();
            } catch (IOException e) {
                chatArea.appendText("✗ Failed to send message\n");
            }
        }
    }
}