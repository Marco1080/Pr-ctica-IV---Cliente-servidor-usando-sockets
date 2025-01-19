package org.example.chatclienteservidor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MensajesController {

    @FXML
    private Button buttonVolver; // Asegúrate de que el botón tiene un fx:id en el FXML

    @FXML
    protected void volverMenu() {
        try {
            // Cargar el archivo FXML de la vista principal
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();

            // Crear la nueva escena
            Scene scene = new Scene(root, 320, 240);

            // Cargar y aplicar el archivo CSS
            String cssFile = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);

            // Obtener el Stage actual desde el botón
            Stage stage = (Stage) buttonVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Servidor");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar la vista hello-view.fxml.");
        }
    }
}
