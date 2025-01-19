package org.example.chatclienteservidor;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class MensajesController {

    private Socket socket;
    private String rolUser;
    @FXML
    private TableView<MensajeView> messageTable;

    @FXML
    private TableColumn<MensajeView, String> userColumn;

    @FXML
    private TableColumn<MensajeView, String> messageColumn;

    @FXML
    private TableColumn<MensajeView, String> dateColumn;

    @FXML
    private Button sendButton;

    @FXML
    private TextField messageInput;

    @FXML
    private Button buttonVolver;

    private volatile boolean listening = true;

    public MensajesController(Socket socket, String rolUser) {
        this.socket = socket;
        this.rolUser = rolUser;
    }

    @FXML
    public void initialize() {
        sendButton.setOnMouseClicked(event -> sendMessage());

        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsuario()));
        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMensaje()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaHora()));

        startListeningForMessages();
    }

    private void startListeningForMessages() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" --- ");
                    if (parts.length == 2) {
                        String[] userMessage = parts[0].split(":");
                        if (userMessage.length == 2) {
                            String usuario = userMessage[0];
                            String mensaje = userMessage[1];
                            String fechaHora = parts[1];

                            // Añadir el mensaje a la tabla
                            Platform.runLater(() -> {
                                messageTable.getItems().add(new MensajeView(usuario, mensaje, fechaHora));
                            });
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al recibir mensajes.");
            }
        }).start();
    }

    public void stopListening() {
        listening = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cerrar el socket.");
        }
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText();

        if (message == null || message.trim().isEmpty()) {
            System.err.println("No se puede enviar un mensaje vacío.");
            return;
        }

        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(message);

            Platform.runLater(() -> {
                messageTable.getItems().add(new MensajeView("Yo", message, LocalDateTime.now().toString()));
                messageInput.clear();
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al enviar el mensaje al servidor.");
        }
    }
}
