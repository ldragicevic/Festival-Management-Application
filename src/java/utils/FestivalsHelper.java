/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Festivals;
import entities.Files;
import entities.Loggins;
import entities.Performers;
import entities.Tickets;
import entities.Users;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author korisnik
 */
public class FestivalsHelper {

    public static Festivals getFestivalById(int id) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals where IDFest = " + id);
        Festivals fest = (Festivals) query.uniqueResult();
        session.close();
        return fest;
    }

    // insert festival (with performers, files) by festival
    public static void insertFestival(Festivals festival) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.save(festival);
        for (Performers p : festival.getPerformerses()) {
            p.setFestivals(festival);
            session.save(p);
        }
        for (Files f : festival.getFileses()) {
            f.setFestivals(festival);
            session.save(f);
        }
        session.getTransaction().commit();
        session.close();
    }

    // gets max IDFest from Festivals
    public static int getMaxIDFest() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals order by IDFest desc");
        List<Festivals> list = query.list();
        session.close();
        return list.get(0).getIdfest();
    }

    // gets 5 earliest
    public static List<Festivals> get5UpcomingFestivals() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = df.format(date);
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals as f where '" + today + "' between f.begin and f.end or '" + today + "' <= f.begin order by f.begin asc");
        query.setMaxResults(5);
        List<Festivals> list = query.list();
        session.close();
        return list;
    }

    // gets 5 best rated
    // gets 5 earliest
    public static List<Festivals> get5BestRated() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = df.format(date);
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals as f where '" + today + "' between f.begin and f.end or '" + today + "' <= f.begin order by totalRating desc");
        query.setMaxResults(5);
        List<Festivals> list = query.list();
        session.close();
        return list;
    }

    // gets 5 earliest
    public static List<Festivals> getAllUpcomingFestivals() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = df.format(date);
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals as f where '" + today + "' between f.begin and f.end or '" + today + "' <= f.begin order by f.begin asc");
        List<Festivals> list = query.list();
        session.close();
        return list;
    }

    // Convert from: yyyy-MM-dd  ===>>> dd-MM-yyyy
    public static Date convertFormat(Date first) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        String firstString[] = df.format(first).split("-");
        Date result = df2.parse(firstString[2] + "-" + firstString[1] + "-" + firstString[0]);
        return result;
    }

    // gets Festival's list of performers
    public static List<Performers> getPerformersList(Festivals festival) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        String queryContent = "from Performers where IDFest = " + festival.getIdfest();
        Query query = session.createQuery(queryContent);
        List<Performers> list = query.list();
        session.close();
        return list;
    }

    // +1 VISIT
    public static void visited(Festivals festival) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        festival.setVisited(festival.getVisited() + 1);
        session.update(festival);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Festivals> getMostVisited() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals order by visited desc");
        query.setMaxResults(10);
        List<Festivals> result = query.list();
        session.close();
        return result;
    }

    public static List<Festivals> getMostSold() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals order by income desc");
        query.setMaxResults(10);
        List<Festivals> result = query.list();
        session.close();
        return result;
    }

    // insert user by user
    public static void updateFestival(Festivals f) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.update(f);
        session.getTransaction().commit();
        session.close();
    }

    // insert user by user
    public static void updateFestivalWithPerformers(Festivals f) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.update(f);
        for (Performers p : f.getPerformerses()) {
            session.update(p);
        }
        session.getTransaction().commit();
        session.close();
    }

    public static void delete(Festivals f) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.delete(f);
        session.getTransaction().commit();
        session.close();
    }

    public static double getAvarageMark(Festivals festival) {
        double avarageSum = 0;
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Festivals where name = '" + festival.getName() + "'");
        List<Festivals> resultingList = query.list();
        for (Festivals f : resultingList) {
            double temp = 0;
            if (f.getNumberRatings() != 0) {
                temp = f.getTotalRating() / f.getNumberRatings();
                if (avarageSum == 0) {
                    avarageSum += temp;
                } else {
                    avarageSum += temp;
                    avarageSum /= 2;
                }
            }
        }
        session.close();
        return avarageSum;
    }

}
