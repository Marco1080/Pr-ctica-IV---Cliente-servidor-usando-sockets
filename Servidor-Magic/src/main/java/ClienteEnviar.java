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
    private final PrintWriter entradaSocketCliente;
    private final BufferedReader salidaSocketCliente;

    public ClienteEnviar(Socket socket, Usuario usuario, Usuario usuarioReceptor) {
        try {
            this.socket = socket;
            this.entradaSocketCliente = new PrintWriter(socket.getOutputStream(), true);
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
                entradaSocketCliente.println("Escriba su mensaje (o 'exit' para salir):");
                String contenido = salidaSocketCliente.readLine();

                if (contenido == null || contenido.equalsIgnoreCase("exit")) {
                    entradaSocketCliente.println("Cerrando conexión...");
                    break;
                }

                Transaction transaction = session.beginTransaction();
                try {
                    Mensaje mensajeNuevo = new Mensaje(usuario, usuarioReceptor, contenido);
                    session.save(mensajeNuevo);
                    transaction.commit();

                    entradaSocketCliente.println("Mensaje enviado correctamente.");
                } catch (Exception e) {
                    transaction.rollback();
                    entradaSocketCliente.println("Error al enviar el mensaje: " + e.getMessage());
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
            entradaSocketCliente.close();
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(ClienteEnviar.class.getName()).log(Level.SEVERE, "Error al cerrar los recursos", e);
        }
    }
}

