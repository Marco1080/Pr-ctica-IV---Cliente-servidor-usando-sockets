module org.example.chatclienteservidor {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.chatclienteservidor to javafx.fxml;
    exports org.example.chatclienteservidor;
}