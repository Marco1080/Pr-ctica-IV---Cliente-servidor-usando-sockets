package org.example.chatclienteservidor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HelloController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField recipientField;

    @FXML
    private Button connectButton;

    private Socket socket;
    private PrintWriter socketWriter;

    @FXML
    protected void mensajesButtonOnClick() {
        String username = usernameField.getText();
        String recipient = recipientField.getText();

        try {
            socket = new Socket("localhost", 80);
            socketWriter = new PrintWriter(socket.getOutputStream(), true);

            socketWriter.println(username);
            socketWriter.println(recipient);

            mostrarMensajes();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al conectar al servidor o enviar datos.");
        }
    }

    @FXML
    protected void mostrarMensajes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mensajes-view.fxml"));
            MensajesController controller = new MensajesController(socket); // Pasar el socket al controlador
            fxmlLoader.setController(controller);

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600, 400);

            Stage stage = (Stage) connectButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Mensajes");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar la vista mensajes-view.fxml.");
        }
    }

}

