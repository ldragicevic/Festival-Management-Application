/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Day;
import entities.Festivals;
import entities.Users;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author korisnik
 */
public class DaysHelper {

    //public static vo
    // get Day by ID
    public static Day getDayById(int IDDay) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Day where IDDay = " + IDDay);
        Day day = (Day) query.uniqueResult();
        session.close();
        return day;
    }

    public static Day getDayByFestivalDate(Festivals festival, Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);        
        String queryContent = "from Day where date = '" + df.format(date) + "' and IDFest = " + festival.getIdfest();
        Query query = session.createQuery(queryContent);
        Day day = (Day) query.uniqueResult();
        session.close();
        return day;
    }
    
    // gets Possible Days reservation for user on festival
    public static List<Day> getDaysFestivalByUser(Festivals festival) throws ParseException {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Day where IDFest = " + festival.getIdfest());
        List<Day> list = query.list();
        session.close();
        return list;
    }

    // update Day
    public static void updateDay(Day day) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        session.update(day);
        session.getTransaction().commit();
        session.close();
    }

    public static boolean possibleFullTicket(Festivals festival) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Day where IDFest = " + festival.getIdfest());
        List<Day> list = query.list();
        session.close();
        for (Day d : list) {
            if (d.getLeftTickets() == 0) {
                return false;
            }
        }
        return true;
    }

    public static void fullTicketOrdered(Festivals festival) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Day where IDFest = " + festival.getIdfest());
        List<Day> list = query.list();
        for (Day d : list) {
            d.setLeftTickets(d.getLeftTickets() - 1);
            session.update(d);
        }
        session.close();
    }

}
