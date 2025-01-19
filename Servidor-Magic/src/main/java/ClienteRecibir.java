import model.Mensaje;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteRecibir implements Runnable {

    private final Socket socket;
    private final Usuario usuario;
    private final Usuario usuarioEmisor;
    private final PrintWriter entradaSocketCliente;
    private final BufferedReader salidaSocketCliente;

    public ClienteRecibir(Socket socket, Usuario usuario, Usuario usuarioEmisor) {
        try {
            this.socket = socket;
            this.entradaSocketCliente = new PrintWriter(socket.getOutputStream(), true);
            this.salidaSocketCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.usuario = usuario;
            this.usuarioEmisor = usuarioEmisor;
        } catch (IOException ex) {
            throw new RuntimeException("Error al inicializar ClienteRecibir", ex);
        }
    }

    @Override
    public void run() {
        getMensajes(session);
        while (true) {
            leerMensajes();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Logger.getLogger(ClienteRecibir.class.getName()).log(Level.SEVERE, "Error en el hilo de escucha", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void leerMensajes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Mensaje> mensajes = getMensajesNoLeidos(session);

            for (Mensaje mensaje : mensajes) {
                entradaSocketCliente.println(
                        mensaje.getUsuarioEmisor().getUsername() + ": " + mensaje.getContenido() + " --- " + mensaje.getFechaEnvio()
                );

                marcarMensajeComoLeido(session, mensaje);
            }
        } catch (Exception e) {
            Logger.getLogger(ClienteRecibir.class.getName()).log(Level.SEVERE, "Error al leer mensajes", e);
        }
    }

    private List<Mensaje> getMensajesNoLeidos(Session session) {
        Query<Mensaje> query = session.createQuery(
                "FROM Mensaje WHERE usuarioReceptor = :usuarioReceptor AND usuarioEmisor = :usuarioEmisor AND leido = false ORDER BY fechaEnvio ASC",
                Mensaje.class
        );
        query.setParameter("usuarioReceptor", usuario);
        query.setParameter("usuarioEmisor", usuarioEmisor);

        return query.getResultList();
    }

    private void marcarMensajeComoLeido(Session session, Mensaje mensaje) {
        Transaction transaction = session.beginTransaction();
        try {
            mensaje.setLeido(true);
            session.merge(mensaje);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            Logger.getLogger(ClienteRecibir.class.getName()).log(Level.SEVERE, "Error al marcar mensaje como leído", e);
        }
    }

    private void cerrarRecursos() {
        try {
            salidaSocketCliente.close();
            entradaSocketCliente.close();
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(ClienteRecibir.class.getName()).log(Level.SEVERE, "Error al cerrar los recursos", e);
        }
    }
}
