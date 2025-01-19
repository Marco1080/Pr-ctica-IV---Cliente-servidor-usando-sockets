package org.example.chatclienteservidor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MensajesController {

    private static Socket socket;
    @FXML
    private ListView<String> mensajesListView;
    @FXML
    private Button buttonVolver;


    public MensajesController(Socket socket) {
        this.socket = socket;
    }

    public static void setSocket(Socket socket) {
        MensajesController.socket = socket;
    }

    @FXML
    public void initialize() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String mensaje;
                while ((mensaje = reader.readLine()) != null) {
                    String finalMensaje = mensaje;
                    Platform.runLater(() -> mensajesListView.getItems().add(finalMensaje));
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al recibir mensajes del servidor.");
            }
        }).start();
    }

    @FXML
    protected void volverMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 320, 240);

            String cssFile = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);


            Stage stage = (Stage) buttonVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Servidor");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar la vista hello-view.fxml.");
        }
    }

}
