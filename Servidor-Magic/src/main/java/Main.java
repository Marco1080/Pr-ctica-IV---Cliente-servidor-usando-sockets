import model.Usuario;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PUERTO = 80;

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            ServerSocket socketServidor = new ServerSocket(PUERTO);
            while (true) {

                Socket socketCliente = socketServidor.accept();
                PrintWriter entradaSocketCliente = new PrintWriter(socketCliente.getOutputStream(), true);
                BufferedReader salidaSocketCliente = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                entradaSocketCliente.println("Inserte su usuario:");
                String usuarioUsername = salidaSocketCliente.readLine();
                entradaSocketCliente.println("Inserte el usuario al que va a enviar mensajes:");
                String usuarioEnviarUsername = salidaSocketCliente.readLine();

                Usuario usuario = session.get(Usuario.class, usuarioUsername);
                Usuario usuarioEnviar = session.get(Usuario.class, usuarioEnviarUsername);

                new Thread(new ClienteEnviar(socketCliente, usuario, usuarioEnviar)).start();
                new Thread(new ClienteRecibir(socketCliente, usuario, usuarioEnviar)).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
