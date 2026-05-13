package lk.ijse.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Open Server Window
        FXMLLoader serverLoader = new FXMLLoader(HelloApplication.class.getResource("server.fxml"));
        Scene serverScene = new Scene(serverLoader.load(), 500, 300);
        Stage serverStage = new Stage();
        serverStage.setTitle("Chat Server - Group Chat");
        serverStage.setScene(serverScene);
        serverStage.show();

        // Open Client 1 Window
        FXMLLoader clientLoader = new FXMLLoader(HelloApplication.class.getResource("client.fxml"));
        Scene clientScene = new Scene(clientLoader.load(), 500, 300);
        Stage clientStage = new Stage();
        clientStage.setTitle("Client 1 - Group Chat");
        clientStage.setScene(clientScene);
        clientStage.show();

        // Open Client 2 Window
        FXMLLoader client2Loader = new FXMLLoader(HelloApplication.class.getResource("client2.fxml"));
        Scene client2Scene = new Scene(client2Loader.load(), 500, 300);
        Stage client2Stage = new Stage();
        client2Stage.setTitle("Client 2 - Group Chat");
        client2Stage.setScene(client2Scene);
        client2Stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}