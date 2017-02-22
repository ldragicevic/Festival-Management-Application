/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import entities.Comments;
import entities.Festivals;
import entities.Files;
import entities.Tickets;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author korisnik
 */
public class FilesHelper {

    // gets max IDFil from Files
    public static int getMaxIDFil() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Files order by IDFil desc");
        List<Files> list = query.list();
        session.close();
        return list.get(0).getIdfil();
    }

    public static void insert(Files file) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.save(file);
        session.getTransaction().commit();
        session.close();
    }

    public static void update(Files file) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.update(file);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Files> getPendingFiles() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Files where isAccepted = 0");
        List<Files> list = query.list();
        session.close();
        return list;
    }

    public static Files getFileByImageUrl(String location) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Files where location = '" + location + "'");
        Files file = (Files) query.uniqueResult();
        session.close();
        return file;
    }

    public static void remove(Files file) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.delete(file);
        session.getTransaction().commit();
        session.close();
    }

    public static List<Files> getFilesForFestival(String name) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Files where festivalName = '" + name + "'");
        List<Files> list = query.list();
        session.close();
        return list;
    }

}
