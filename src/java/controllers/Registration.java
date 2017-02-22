/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Users;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import utils.UsersHelper;

/**
 *
 * @author korisnik
 */
@Named(value = "registration")
@RequestScoped
public class Registration {

    private String username;
    private String password;
    private String repeat;
    private String name;
    private String surname;
    private String phone;
    private String email;

    public String register() {
        Users user = new Users(name, surname, username, password, phone, email, 0, 0, 0);
        UsersHelper.insertUser(user);
        return "index?faces-redirect=true";               
    }

    public Registration() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
