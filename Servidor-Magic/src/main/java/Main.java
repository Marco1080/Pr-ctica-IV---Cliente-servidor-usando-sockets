import model.Mensaje;
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

                entradaSocketCliente.println("Inserte su usuario:");
                String usuarioUsername = salidaSocketCliente.readLine();

                entradaSocketCliente.println("Inserte el usuario al que va a enviar mensajes:");
                String usuarioEnviarUsername = salidaSocketCliente.readLine();

                Usuario usuario = obtenerORegistrarUsuario(session, usuarioUsername);
                Usuario usuarioEnviar = obtenerORegistrarUsuario(session, usuarioEnviarUsername);

                new Thread(new ClienteEnviar(socketCliente, usuario, usuarioEnviar)).start();
                new Thread(new ClienteRecibir(socketCliente, usuarioEnviar, usuario)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Usuario obtenerORegistrarUsuario(Session session, String username) {
        Transaction transaction = session.beginTransaction();
        Usuario usuario = session.get(Usuario.class, username);

        if (usuario == null) {
            usuario = new Usuario(username);
            session.save(usuario);
        }

        transaction.commit();
        return usuario;
    }

    private static void inicializarBaseDeDatos(Session session) {
        Transaction transaction = session.beginTransaction();
        try {
            // Lógica para inicializar datos, si es necesario
            System.out.println("Base de datos inicializada correctamente.");
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    private static void limpiarRecursos(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
        HibernateUtil.shutdown();
    }
}
