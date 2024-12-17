import model.Mensaje;
import model.Usuario;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteEnviar implements Runnable {

    private Socket socket;
    private Usuario usuario;
    private Usuario usuarioReceptor;
    PrintWriter entradaSocketCliente;
    BufferedReader salidaSocketCliente;
    ArrayList<String> listaCartas;

    public ClienteEnviar(Socket socket, Usuario usuario, Usuario usuarioReceptor) {
        try {
            this.socket = socket;
            entradaSocketCliente = new PrintWriter(socket.getOutputStream(), true);
            salidaSocketCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.usuario = usuario;
            this.usuarioReceptor = usuarioReceptor;
        } catch (IOException ex) {
            Logger.getLogger(ClienteRecibir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            while(true){
                String contenido = salidaSocketCliente.readLine();
                Mensaje mensajeNuevo = new Mensaje(usuario, usuarioReceptor, contenido, LocalDateTime.now());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
