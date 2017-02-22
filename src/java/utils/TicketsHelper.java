/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Festivals;
import entities.Tickets;
import entities.Users;
import java.text.DateFormat;
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
public class TicketsHelper {

    public static List<Tickets> getEndedFestivalTicketsByUser(Users user) {
        Date now = new Date();
        List<Tickets> resultingList = new LinkedList<>();
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        String queryContent = "from Tickets where isBought = 2 and IDUser = " + user.getIduser();
        Query query = session.createQuery(queryContent);
        List<Tickets> tickets = query.list();
        for (Tickets t : tickets) {
            Date endTicket = (t.getDate() == null) ? t.getFestivals().getEnd() : t.getDate();
            if (now.after(endTicket) == true) {
                resultingList.add(t);
            }
        }
        session.close();
        return resultingList;
    }

    public static List<Tickets> getActiveFestivalTicketsByUser(Users user) {
        Date now = new Date();
        List<Tickets> resultingList = new LinkedList<>();
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        String queryContent = "from Tickets where isBought = 2 and IDUser = " + user.getIduser();
        Query query = session.createQuery(queryContent);
        List<Tickets> tickets = query.list();
        for (Tickets t : tickets) {
            Date endTicket = (t.getDate() == null) ? t.getFestivals().getEnd() : t.getDate();
            if (now.after(endTicket) == false) {
                resultingList.add(t);
            }
        }
        session.close();
        return resultingList;
    }

    public static Festivals getTicketsFestival(Tickets ticket) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals where IDFest = " + ticket.getFestivals().getIdfest());
        Festivals f = (Festivals) query.uniqueResult();
        session.close();
        return f;
    }

    // insert user by user
    public static void insertTicket(Tickets ticket) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.save(ticket);
        session.getTransaction().commit();
        session.close();
    }

    // insert user by user
    public static void updateTicket(Tickets ticket) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.update(ticket);
        session.getTransaction().commit();
        session.close();
    }

    // get number of tickets for user for festival
    public static int getNumberTicketsUserFestival(Users user, Festivals festival) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Tickets where IDUser = :user and IDFest = :fest");
        query.setParameter("user", user.getIduser());
        query.setParameter("fest", festival.getIdfest());
        List<Tickets> tickets = query.list();
        session.close();
        return tickets.size();
    }

    // gets users reservations
    public static List<Tickets> getReservationsForUser(Users user) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Tickets where isBought = 0 and IDUser = :user");
        query.setParameter("user", user.getIduser());
        List<Tickets> tickets = query.list();
        session.close();
        return tickets;
    }

    // gets all reservations
    public static List<Tickets> getAllReservations() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Tickets where isBought = 0");
        List<Tickets> tickets = query.list();
        session.close();
        return tickets;
    }

    public static void delete(Tickets ticket) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        session.delete(ticket);
        transaction.commit();
        session.close();
    }

    public static List<Tickets> getPendingTickets() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Tickets where isBought = 1 or isBought = 0");
        List<Tickets> tickets = query.list();
        session.close();
        return tickets;
    }

    public static List<Tickets> getsTicketsForFestival(Festivals festival) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Tickets where IDFest = " + festival.getIdfest());
        List<Tickets> tickets = query.list();
        session.close();
        return tickets;
    }

    public static void deleteFestivalsTickets(Festivals f) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        String queryContent = "DELETE FROM tickets where IDFest = " + f.getIdfest();
        Query query = session.createSQLQuery(queryContent);
        transaction.commit();
        session.close();
    }

}
