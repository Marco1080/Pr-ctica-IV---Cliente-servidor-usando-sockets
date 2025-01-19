package org.example.chatclienteservidor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private Socket socket;

    @FXML
    private Button loginButton;

    @FXML
    protected void goToMenu() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            socket = new Socket("localhost", 8080);

            if (socket.isConnected()) {
                System.out.println("Conexión establecida con el servidor.");

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.println(username);
                writer.println(password);

                String response = reader.readLine();
                if ("200".equals(response)) {
                    System.out.println("Login exitoso. Código 200 recibido.");
                    cambiarVistaMenu();
                } else {
                    mostrarError("Login fallido. Código de respuesta: " + response);
                }
            } else {
                mostrarError("No se pudo conectar al servidor.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al conectar al servidor: " + e.getMessage());
        }
    }

    private void cambiarVistaMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 420, 340);

            String cssFile = getClass().getResource("/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(cssFile);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista del menú.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Conexión");
        alert.setHeaderText("No se pudo conectar al servidor");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
