/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.*;
import java.util.List;
import org.hibernate.*;

/**
 *
 * @author korisnik
 */
public class CommentsHelper {

    public static boolean hasRated(Users user, Festivals festival) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createSQLQuery("select * from comments where IDUser = " + user.getIduser() + "  and IDFest = " + festival.getIdfest());
        Object commented = query.uniqueResult();
        session.close();
        return commented != null;
    }

    public static void insert(Comments comment) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.save(comment);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Comments> getComments(Festivals f) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Comments where festivalName = '" + f.getName() + "'");
        List<Comments> result = query.list();
        session.close();
        return result;
    }

}
