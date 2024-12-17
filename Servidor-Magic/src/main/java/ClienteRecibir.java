import model.Mensaje;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteRecibir implements Runnable {
    private Socket socket;
    private Usuario usuario;
    private Usuario usuarioEnviado;
    PrintWriter entradaSocketCliente;
    BufferedReader salidaSocketCliente;
    ArrayList<String> listaCartas;

    public ClienteRecibir(Socket socket, Usuario usuario, Usuario usuarioEnviado) {
        try {
            this.socket = socket;
            entradaSocketCliente = new PrintWriter(socket.getOutputStream(), true);
            salidaSocketCliente = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.usuario = usuario;
            this.usuarioEnviado = usuarioEnviado;
        } catch (IOException ex) {
            Logger.getLogger(ClienteRecibir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        List<Mensaje> mensajes;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<Mensaje> mensajeQuery = session.createQuery("FROM Mensaje WHERE (usuarioReceptor = :usuarioReceptor AND usuarioEmisor = :usuarioEmisor) OR (usuarioEmisor = :usuarioReceptor AND usuarioReceptor = :usuarioEmisor) order by fechaEmisor asc", Mensaje.class);
            mensajeQuery.setParameter("usuarioReceptor", this.usuario.getUsername());
            mensajeQuery.setParameter("usuarioEmisor", this.usuarioEnviado.getUsername());
            mensajes = mensajeQuery.getResultList();
            entradaSocketCliente.println(mensajes.size());
            for (Mensaje mensaje : mensajes){
                entradaSocketCliente.println(mensaje.getUsuarioEmisor().getUsername() + ": " + mensaje.getContenido() + " --- " + mensaje.getFechaEnvio());
            }
            boolean exit = false;
            while (!exit){
                Mensaje mensajeNuevo = recibirMensaje();

                entradaSocketCliente.println(mensajeNuevo.getUsuarioEmisor().getUsername() + ": " + mensajeNuevo.getContenido() + " --- " + mensajeNuevo.getFechaEnvio());
            }

        }

        }

    private Mensaje recibirMensaje() {
        return null;
    }
}
