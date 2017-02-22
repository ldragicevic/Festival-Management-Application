/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.*;
import java.io.File;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import utils.*;

/**
 *
 * @author korisnik
 */
@Named(value = "admin")
@SessionScoped
public class Admin implements Serializable {

    private List<Loggins> loggins;
    private List<Festivals> mostVisited;
    private List<Festivals> mostSold;

    private List<Users> requests;

    // pending reservations
    private List<Tickets> orders;

    // pending files
    private List<String> images;
    private List<String> videos;

    // guests tickets
    private List<Festivals> guestsFestivals;
    private String ticketMessage = "";
    private Date date;
    private Festivals festival;
    private List<Day> days;
    private String number = "1";

    // festival cancelation
    private List<Festivals> cancelFestival;

    // edit festivals
    private List<Festivals> editFestivals;
    private Festivals editFestival;

    // --
    // -- Ticket Sell to guest
    // --
    public String ticketSell() throws ParseException {
        guestsFestivals = FestivalsHelper.getAllUpcomingFestivals();
        return "guestticket?faces-redirect=true";
    }

    public String showFestival(Festivals f) throws ParseException {
        festival = f;
        date = f.getBegin();
        days = DaysHelper.getDaysFestivalByUser(f);
        return "admindetails?faces-redirect=true";
    }

    public String reserveTicket(String type) throws ParseException {
        // SINGLE TICKET SELL
        if (type.equals("single")) {
            Day day = DaysHelper.getDayByFestivalDate(festival, date);
            if (day.getLeftTickets() < Integer.parseInt(number)) {
                ticketMessage = "Not enough tickets to perform this action.";
                return null;
            }

            for (int i = 0; i < Integer.parseInt(number); i++) {
                Tickets t = new Tickets(festival, UsersHelper.getAdminUser(), 2, festival.getPriceDay(), day.getDate(), new Date(), festival.getName());
                TicketsHelper.insertTicket(t);
            }

            festival.setIncome(festival.getIncome() + Integer.parseInt(number) * festival.getPriceDay());
            FestivalsHelper.updateFestival(festival);

            day.setLeftTickets(day.getLeftTickets() - Integer.parseInt(number));
            DaysHelper.updateDay(day);

            return adminPanelPage();

        } else {

            List<Day> days = DaysHelper.getDaysFestivalByUser(festival);
            for (Day d : days) {
                if (d.getLeftTickets() < Integer.parseInt(number)) {
                    ticketMessage = "Not enough tickets to perform this action.";
                    return null;
                }
            }

            for (int i = 0; i < Integer.parseInt(number); i++) {
                Tickets t = new Tickets(festival, UsersHelper.getAdminUser(), 2, festival.getPriceFestival(), null, new Date(), festival.getName());
                TicketsHelper.insertTicket(t);
            }

            for (Day d : days) {
                d.setLeftTickets(d.getLeftTickets() - Integer.parseInt(number));
                DaysHelper.updateDay(d);
            }

            festival.setIncome(festival.getIncome() + Integer.parseInt(number) * festival.getPriceFestival());
            FestivalsHelper.updateFestival(festival);

            return adminPanelPage();

        }
    }
    // --
    // -- Pending Requests
    // --

    public void approveRequest(Users user) {
        user.setIsActive(1);
        UsersHelper.updateUser(user);
        requests.remove(user);
    }

    public void rejectRequest(Users user) {
        UsersHelper.deleteUser(user);
        requests.remove(user);
    }

    // -- 
    // -- Pending Orders
    // -- 
    public String pendingOrders() {
        orders = TicketsHelper.getPendingTickets();
        return "orders?faces-redirect=true";
    }

    public String approveTicket(Tickets ticket) {
        ticket.setIsBought(2);
        TicketsHelper.updateTicket(ticket);
        Festivals festival = TicketsHelper.getTicketsFestival(ticket);
        festival.setIncome(festival.getIncome() + ticket.getPrice());
        FestivalsHelper.updateFestival(festival);
        return pendingOrders();
    }

    // --
    // -- Pending Files
    // --
    public String pendingFiles() {
        List<Files> pendingFiles = FilesHelper.getPendingFiles();
        images = new LinkedList<>();
        videos = new LinkedList<>();
        for (Files tempFile : pendingFiles) {
            if (tempFile.getIsVideo() == 0) {
                images.add(tempFile.getLocation());
            } else {
                videos.add(tempFile.getLocation());
            }
        }
        return "pendingfiles?faces-redirect=true";
    }

    public String rejectImage(String image) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String destPath = ec.getRealPath("/") + "uploads\\";
        File fileDisc = new File(destPath + image);
        if (fileDisc.exists()) {
            fileDisc.delete();
        }
        Files file = FilesHelper.getFileByImageUrl(image);
        FilesHelper.remove(file);
        if (file.getIsVideo() == 0) {
            images.remove(image);
        } else {
            videos.remove(image);
        }
        return null;
    }

    public String approveImage(String image) {
        Files file = FilesHelper.getFileByImageUrl(image);
        file.setIsAccepted(1);
        FilesHelper.update(file);
        if (file.getIsVideo() == 0) {
            images.remove(image);
        } else {
            videos.remove(image);
        }
        return null;
    }

    // --
    // -- Edit Festivals List
    // -- 
    public String editFestivalsList() throws ParseException {
        editFestivals = FestivalsHelper.getAllUpcomingFestivals();
        return "admineditfestivals?faces-redirect=true";
    }

    public String showEditFestival(Festivals f) {
        editFestival = f;
        return "editFestival?faces-redirect=true";
    }

    public String editFestivalsCommit() {
        
        FestivalsHelper.updateFestivalWithPerformers(editFestival);
        
        List<Users> users = new LinkedList<>();
        List<Tickets> tickets = TicketsHelper.getsTicketsForFestival(editFestival);
        for (Tickets t : tickets) {
            if (users.contains(t.getUsers()) == false) {
                users.add(t.getUsers());
            }
        }
        
        String warningMessage = "Festival Edit: " + editFestival.getName() + " .";

        for (Users user : users) {
            Messages message = new Messages(editFestival, user, warningMessage);
            MessagesHelper.insert(message);
        }
        
        return "admin?faces-redirect=true";
    }

    // --
    // -- Cancel Festivals
    // --
    public String cancelFestivals() throws ParseException {
        cancelFestival = FestivalsHelper.getAllUpcomingFestivals();
        return "cancelfestivals?faces-redirect=true";
    }

    public String cancelFestival(Festivals festival) throws ParseException {

        List<Users> users = new LinkedList<>();
        List<Tickets> tickets = TicketsHelper.getsTicketsForFestival(festival);
        for (Tickets t : tickets) {
            if (users.contains(t.getUsers()) == false) {
                users.add(t.getUsers());
            }
        }

        String warningMessage = "Festival Canceled: " + festival.getName() + " visit us for more information.";

        for (Users user : users) {
            Messages message = new Messages(festival, user, warningMessage);
            MessagesHelper.insert(message);
        }

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date oldDate = sdf.parse("2000-01-01");

        festival.setBegin(oldDate);
        festival.setEnd(oldDate);
        FestivalsHelper.updateFestival(festival);
        cancelFestival.remove(festival);

        List<Tickets> ticketsToDelete = TicketsHelper.getsTicketsForFestival(festival);

        for (Tickets t : ticketsToDelete) {
            TicketsHelper.delete(t);
        }

        for (Files file : festival.getFileses()) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            String destPath = ec.getRealPath("/") + "uploads\\";
            File fileDisc = new File(destPath + file.getLocation());
            if (fileDisc.exists()) {
                fileDisc.delete();
            }
        }

        return null;
    }

    public String requestsPage() {
        requests = UsersHelper.getPendingRequests();
        return "requests?faces-redirect=true";
    }

    public String newFestivalPage() {
        return "newfestival?faces-redirect=true";
    }

    public String newFestivalFromFile() {
        return "newfestivalfile?faces-redirect=true";
    }

    public List<Users> getRequests() {
        return requests;
    }

    public void setRequests(List<Users> requests) {
        this.requests = requests;
    }

    public List<Loggins> getLoggins() {
        loggins = LogginsHelper.getLatestLoggins();
        return loggins;
    }

    public void setLoggins(List<Loggins> loggins) {
        this.loggins = loggins;
    }

    public List<Festivals> getMostVisited() {
        mostVisited = FestivalsHelper.getMostVisited();
        return mostVisited;
    }

    public void setMostVisited(List<Festivals> mostVisited) {
        this.mostVisited = mostVisited;
    }

    public List<Tickets> getOrders() {
        return orders;
    }

    public void setOrders(List<Tickets> orders) {
        this.orders = orders;
    }

    public List<Festivals> getMostSold() {
        mostSold = FestivalsHelper.getMostSold();
        return mostSold;
    }

    public void setMostSold(List<Festivals> mostSold) {
        this.mostSold = mostSold;
    }

    public Admin() {
    }

    public String adminPanelPage() {
        loggins = LogginsHelper.getLatestLoggins();
        return "admin?faces-redirect=true";
    }

    public List<Festivals> getGuestsFestivals() {
        return guestsFestivals;
    }

    public void setGuestsFestivals(List<Festivals> guestsFestivals) {
        this.guestsFestivals = guestsFestivals;
    }

    public String getTicketMessage() {
        return ticketMessage;
    }

    public void setTicketMessage(String ticketMessage) {
        this.ticketMessage = ticketMessage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Festivals getFestival() {
        return festival;
    }

    public void setFestival(Festivals festival) {
        this.festival = festival;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<Festivals> getCancelFestival() {
        return cancelFestival;
    }

    public void setCancelFestival(List<Festivals> cancelFestival) {
        this.cancelFestival = cancelFestival;
    }

    public List<Festivals> getEditFestivals() {
        return editFestivals;
    }

    public void setEditFestivals(List<Festivals> editFestivals) {
        this.editFestivals = editFestivals;
    }

    public Festivals getEditFestival() {
        return editFestival;
    }

    public void setEditFestival(Festivals editFestival) {
        this.editFestival = editFestival;
    }

}
