import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            while (true) {
                Socket socketCliente = socketServidor.accept();
                PrintWriter entradaSocketCliente = new PrintWriter(socketCliente.getOutputStream(), true);
                BufferedReader salidaSocketCliente = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

                String usuarioUsername = salidaSocketCliente.readLine();

                String usuarioEnviarUsername = salidaSocketCliente.readLine();

                Usuario usuario = getUsuario(session, usuarioUsername);
                Usuario usuarioEnviar = getUsuario(session, usuarioEnviarUsername);

                new Thread(new ClienteEnviar(socketCliente, usuario, usuarioEnviar)).start();
                new Thread(new ClienteRecibir(socketCliente, usuarioEnviar, usuario)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Usuario getUsuario(Session session, String username) {
        Transaction transaction = session.beginTransaction();
        Usuario usuario = session.get(Usuario.class, username);

        if (usuario == null) {
            usuario = new Usuario(username);
            session.persist(usuario);
        }

        transaction.commit();
        return usuario;
    }
}
