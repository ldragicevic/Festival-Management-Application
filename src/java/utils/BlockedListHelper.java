/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Day;
import entities.Festivals;
import entities.Users;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author korisnik
 */
public class BlockedListHelper {

    public static boolean isBanned(Festivals festival, Users user) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createSQLQuery("select * from blocked where IDUser = " + user.getIduser() + "  and IDFest = " + festival.getIdfest());
        Object blocked = query.uniqueResult();
        session.close();
        if (blocked == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void insertBan(int IDUser, int IDFest) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createSQLQuery("INSERT INTO blocked VALUES(" + IDUser + ", " + IDFest + ")");
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

}
