package org.example.chatclienteservidor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyStore;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private SSLSocket socket;

    @FXML
    private Button loginButton;

    @FXML
    protected void goToMenu() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            socket = configurarSSL();

            if (socket.isConnected()) {
                System.out.println("Conexión segura establecida con el servidor.");

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.println(username);
                writer.println(password);

                String response = reader.readLine();

                if ("200".equals(response)) {
                    System.out.println("Login exitoso. Código 200 recibido.");
                    String rol = reader.readLine();
                    cambiarVistaMenu(rol);
                } else {
                    mostrarError("Login fallido. Código de respuesta: " + response);
                }
            } else {
                mostrarError("No se pudo conectar al servidor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al conectar al servidor: " + e.getMessage());
        }
    }

    private SSLSocket configurarSSL() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreStream = new FileInputStream("truststore.jks")) {
            trustStore.load(trustStoreStream, "1234".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        return (SSLSocket) socketFactory.createSocket("localhost", 8080);
    }

    private void cambiarVistaMenu(String rol) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            HelloController controller = new HelloController(socket, rol);
            fxmlLoader.setController(controller);
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

