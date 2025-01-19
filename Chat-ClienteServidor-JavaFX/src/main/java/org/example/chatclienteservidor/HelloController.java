package org.example.chatclienteservidor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void mostrarMensajes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mensajes-view.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 600, 400);

            String cssFile = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);

            Stage stage = (Stage) welcomeText.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Mensajes");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar la vista Mensajes-view.fxml.");
        }
    }

}
