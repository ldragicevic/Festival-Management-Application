
import entities.Users;
import utils.UsersHelper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author korisnik
 */
public class Test {
 
    public static void main(String[] args) {
        Users user = UsersHelper.getUserByUsername("luska");
        if (user == null) {
            System.out.println("null");
        } else {
            System.out.println("postoji");
        }
    }
    
}
