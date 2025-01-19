import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Main {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try (SSLServerSocket serverSocket = configurarSSL()) {
            System.out.println("Servidor seguro escuchando en el puerto " + PUERTO);

            while (true) {
                try{
                    SSLSocket socket = (SSLSocket) serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    String usuarioUsername = in.readLine();
                    String usuarioPassword = in.readLine();

                    byte[] passwordHash = hashPassword(usuarioPassword);

                    Usuario usuario = autenticarUsuario(session, usuarioUsername, passwordHash);
                    if (usuario == null) {
                        out.println("404");
                        socket.close();
                        continue;
                    }

                    out.println("200");
                    out.println(usuario.getRole());
                    String usuarioReceptorUsername = in.readLine();
                    Usuario usuarioReceptor = getUsuario(session, usuarioReceptorUsername);
                    if ("admin".equalsIgnoreCase(usuario.getRole())) {


                        new Thread(new ClienteEnviar(socket, usuario, usuarioReceptor)).start();
                        new Thread(new ClienteRecibir(socket, usuarioReceptor, usuario)).start();
                    } else {
                        new Thread(new ClienteRecibir(socket, usuarioReceptor, usuario)).start();
                    }
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SSLServerSocket configurarSSL() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreStream = new FileInputStream("src/main/java/keystore.jks")) {
                keyStore.load(keyStoreStream, "1234".toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "1234".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
            return (SSLServerSocket) socketFactory.createServerSocket(PUERTO);
        } catch (Exception e) {
            throw new RuntimeException("Error al configurar SSL", e);
        }
    }

    private static byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear:", e);
        }
    }

    private static Usuario autenticarUsuario(Session session, String username, byte[] passwordHash) {
        Transaction transaction = session.beginTransaction();
        Usuario usuario = session.get(Usuario.class, username);

        if (usuario == null || usuario.getPassword() == null) {
            Usuario newUser = new Usuario(username, passwordHash, "user");
            session.persist(newUser);
            transaction.commit();
            return newUser;
        } else if (!Arrays.equals(usuario.getPassword(), passwordHash)) {
            transaction.commit();
            return null;
        }
        transaction.commit();
        return usuario;
    }

    private static Usuario getUsuario(Session session, String username) {
        Transaction transaction = session.beginTransaction();
        Usuario usuario = session.get(Usuario.class, username);

        if (usuario == null) {
            usuario = new Usuario(username, null, "user");
            session.persist(usuario);
        }

        transaction.commit();
        return usuario;
    }
}
