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
public class UsersHelper {

    // get Admin user
    public static Users getAdminUser() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Users where isAdmin = 1");
        Users user = (Users) query.uniqueResult();
        session.close();
        return user;
    }

    // get user by username
    public static Users getUserByUsername(String username) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Users where username = '" + username + "'");
        Users user = (Users) query.uniqueResult();
        session.close();
        return user;
    }

    // get user by id
    public static Users getUserById(int id) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Users where IDUser = " + id);
        Users user = (Users) query.uniqueResult();
        session.close();
        return user;
    }

    // insert user by user
    public static void insertUser(Users user) {
        Session session = HibernateUtil.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    // update user by user, newpassword 
    public static void updateUser(Users user) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    // delete user, by user
    public static void deleteUser(Users user) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        session.delete(user);
        session.getTransaction().commit();
        session.close();
    }

    // get pending requests
    public static List<Users> getPendingRequests() {
        Session session = HibernateUtil.openSession();
        Transaction transaction = HibernateUtil.beginTransaction(session);
        Query query = session.createQuery("from Users where isActive = 0");
        List<Users> result = query.list();
        session.close();
        return result;
    }

}
