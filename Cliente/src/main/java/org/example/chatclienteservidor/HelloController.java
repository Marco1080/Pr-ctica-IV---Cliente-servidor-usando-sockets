package org.example.chatclienteservidor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HelloController {

    @FXML
    Button sendButton;
    @FXML
    private TextField senderInput;
    @FXML
    private TextField receiverInput;
    private Socket socket;
    private String rolUsuario;

    public HelloController(Socket socket, String rolUsuario) {
        this.socket = socket;
        this.rolUsuario = rolUsuario;
    }

    @FXML
    protected void goTo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mensajes-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 420, 340);

            String cssFile = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);

            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void mensajesButtonOnClick() {


        String receptor = receiverInput.getText();
        try {
            if (socket.isConnected()) {

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(receptor);

                mostrarMensajes();
            } else {
                mostrarError("No se pudo conectar al servidor.");
            }
        } catch (IOException e) {

        }

    }


    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Conexión");
        alert.setHeaderText("No se pudo conectar al servidor");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    protected void mostrarMensajes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mensajes-view.fxml"));
            MensajesController controller = new MensajesController(socket, this.rolUsuario);
            fxmlLoader.setController(controller);

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600, 400);

            Stage stage = (Stage) senderInput.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Mensajes");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar la vista mensajes-view.fxml.");
        }
    }
}

