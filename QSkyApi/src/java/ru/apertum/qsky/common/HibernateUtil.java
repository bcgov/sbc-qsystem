/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.common;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utility class to setup Hibernate sessions.
 * @author George Walker
 */

/*
This file will automatically load the configuration stored in <projectdir>/src/java/hibernate.cfg.xml
*/

public class HibernateUtil {
    
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {             
            Configuration cfg = new Configuration();
            // get configuration from hibernate.cfg.xml
            cfg.configure();
            // override hibernate configuration with environment variables.
            String url = "jdbc:mysql://" + System.getenv ("MYSQL_SERVICE") + ":3306/qsky" ;
            String user = System.getenv ("MYSQL_USER");
            String password = System.getenv ("MYSQL_PASSWORD");

            cfg.setProperty("hibernate.connection.url", url);
            cfg.setProperty("hibernate.connection.username", user);
            cfg.setProperty("hibernate.connection.password", password);
            
            return cfg.buildSessionFactory();
            
        }
        catch (Throwable ex) {
            System.err.println("SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
