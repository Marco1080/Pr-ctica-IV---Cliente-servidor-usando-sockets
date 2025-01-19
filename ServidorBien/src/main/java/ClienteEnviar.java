import model.Mensaje;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteEnviar implements Runnable {

    private final Socket socket;
    private final Usuario usuario;
    private final Usuario usuarioReceptor;
    private final BufferedReader salidaSocketCliente;

    public ClienteEnviar(Socket socket, Usuario usuario, Usuario usuarioReceptor) {
        try {
            this.socket = socket;
            this.salidaSocketCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.usuario = usuario;
            this.usuarioReceptor = usuarioReceptor;
        } catch (IOException ex) {
            throw new RuntimeException("Error al inicializar ClienteEnviar", ex);
        }
    }

    @Override
    public void run() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            while (true) {
                String mensaje = salidaSocketCliente.readLine();

                if (mensaje == null || mensaje.equalsIgnoreCase("exit")) break;

                Transaction transaction = session.beginTransaction();
                try {
                    Mensaje mensajeNuevo = new Mensaje(usuario, usuarioReceptor, mensaje);
                    session.persist(mensajeNuevo);
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    Logger.getLogger(ClienteEnviar.class.getName()).log(Level.SEVERE, "Error al enviar mensaje", e);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(ClienteEnviar.class.getName()).log(Level.SEVERE, "Error en la comunicación", e);
        } finally {
            cerrarRecursos();
        }
    }

    private void cerrarRecursos() {
        try {
            salidaSocketCliente.close();
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(ClienteEnviar.class.getName()).log(Level.SEVERE, "Error al cerrar los recursos", e);
        }
    }
}

