/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Loggins;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author korisnik
 */
public class LogginsHelper {

    public static List<Loggins> getLatestLoggins() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Loggins order by IDLog desc");
        List<Loggins> result = query.list();
        session.close();
        return result;
    }

    public static void newLoggin(String username) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Loggins order by IDLog asc");
        List<Loggins> result = query.list();
        // removing one loggin
        session.delete(result.get(0));
        Loggins loggin = new Loggins(username);
        // inserting new loggin 
        session.save(loggin);
        transaction.commit();
        session.close();
    }
    
}
