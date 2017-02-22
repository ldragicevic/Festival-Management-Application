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
@Named(value = "changePassword")
@RequestScoped
public class ChangePassword {

    private String username;
    private String currentPassword;
    private String newPassword;
    private String repeat;
    private String message = "";

    public String change() {
        Users user = UsersHelper.getUserByUsername(username);
        if (user == null) {
            message = "User does not exist.";
            username = currentPassword = newPassword = repeat = "";
            return null;
        } else if (user.getPassword().equals(currentPassword) == false) {
            message = "Incorrect current password.";
            username = currentPassword = newPassword = repeat = "";
            return null;
        } else {
            user.setPassword(newPassword);
            UsersHelper.updateUser(user);
            return "index?faces-redirect=true";
        }
    }

    public ChangePassword() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
