import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente implements Runnable {
    private Socket socket;
    PrintWriter entradaSocketCliente;
    BufferedReader salidaSocketCliente;
    ArrayList<String> listaCartas;

    public Cliente(Socket socket) {
        try {
            this.socket = socket;
            entradaSocketCliente = new PrintWriter(socket.getOutputStream(), true);
            salidaSocketCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    @Override
    public void run() {

    }
}
