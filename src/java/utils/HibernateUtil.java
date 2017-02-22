/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author korisnik
 */
public class HibernateUtil {

    private static final SessionFactory factory;

    static {
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();
        factory = new Configuration().configure().buildSessionFactory(standardRegistry);
    }

    public static Session openSession() {
        return factory.openSession();
    }

    public static Transaction beginTransaction(Session session) {
        return session.beginTransaction();
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }

}
