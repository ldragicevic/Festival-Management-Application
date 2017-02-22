/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.*;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author korisnik
 */
public class MessagesHelper {

    public static void insert(Messages message) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.save(message);
        session.getTransaction().commit();
        session.close();
    }

    public static Messages getUsersMessage(Users user) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Messages where IDUser = " + user.getIduser());
        Messages message = (Messages) query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return message;
    }

    public static void removeUsersMessage(Users user) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        Query query = session.createQuery("from Messages where IDUser = " + user.getIduser());
        Messages message = (Messages) query.uniqueResult();
        session.delete(message);
        session.getTransaction().commit();
        session.close();
    }

}
