import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static Configuration configuration = new Configuration().configure();


    public static SessionFactory getSessionFactory() {
        Logger hibernateLogger = Logger.getLogger("org.hibernate");
        hibernateLogger.setLevel(Level.SEVERE);

        if (sessionFactory == null) {
            try {
                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return sessionFactory;
    }
    public static void shutdown() {
        if (sessionFactory != null) {
           sessionFactory.close();
        }
    }

}
