/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.*;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.model.UploadedFile;
import utils.*;

/**
 *
 * @author korisnik
 */
@Named(value = "controller")
@SessionScoped
public class Controller implements Serializable {

    // -------------------------
    // Login
    private String username;
    private String password;
    private String loginMessage = "";
    private Users loggedUser;
    // Search
    private List<Festivals> festivals;
    private Date searchDateBegin;
    private Date searchDateEnd;
    private String searchName = "";
    private String searchPlace = "";
    private String searchPerformer = "";
    // Details Festival
    private Festivals festival;
    private List<String> images;
    private List<String> videos;
    private List<Performers> performers;
    private List<Comments> comments;
    private List<Day> days;
    private double avarageMark;
    // Reservation
    private String kind = "reserve";
    private String ticketMessage = "";
    // Remove Reservation
    private List<Tickets> myReservations;
    // Visited // Attending Festivals
    private List<Tickets> myVisitedFestivals;
    private List<Tickets> myAttendingFestivals;
    // Rate Festivals
    private List<Festivals> myRateFestivals;
    private Festivals festivalToRate;
    private Integer value = 1;
    private String commentText = "Leave comment here.";
    private UploadedFile files[] = new UploadedFile[5];
    // Admin -> User message
    private String adminsMessage = "";
    // -------------------------

    public String reserveTicket(Day day) throws ParseException {
        // Blocked from System
        if (loggedUser.getReserved() >= 3) {
            ticketMessage = "You are blocked on ticket service.";
            return null;
        }
        // Blocked from Festival
        if (BlockedListHelper.isBanned(festival, loggedUser)) {
            ticketMessage = "You are blocked on this festival.";
            return null;
        }
        // Reached maximum tickets per user
        if (festival.getTicketsPerUser() <= TicketsHelper.getNumberTicketsUserFestival(loggedUser, festival)) {
            ticketMessage = "You reached ticket limit for this festival.";
            return null;
        }
        int isBought = (kind.equals("reserve") ? 0 : 2);
        Tickets ticket = new Tickets(festival, loggedUser, isBought, festival.getPriceDay(), day.getDate(), new Date(), festival.getName());
        TicketsHelper.insertTicket(ticket);
        day.setLeftTickets(day.getLeftTickets() - 1);
        DaysHelper.updateDay(day);
        return "user?faces-redirect=true";
    }

    public String reserveFullTicket(Festivals fest) throws ParseException {
        // Check if every day has > 0 tickets left
        if (DaysHelper.possibleFullTicket(fest) == false) {
            ticketMessage = "Not enough tickets per day to create full festival ticket.";
            return null;
        }
        // Reached maximum tickets per user
        if (fest.getTicketsPerUser() <= TicketsHelper.getNumberTicketsUserFestival(loggedUser, fest)) {
            ticketMessage = "You reached ticket limit for this festival.";
            return null;
        }
        int isBought = (kind.equals("reserve") ? 0 : 2);
        Tickets ticket = new Tickets(fest, loggedUser, isBought, fest.getPriceFestival(), null, new Date(), fest.getName());
        TicketsHelper.insertTicket(ticket);
        List<Day> days = DaysHelper.getDaysFestivalByUser(fest);
        for (Day d : days) {
            d.setLeftTickets(d.getLeftTickets() - 1);
            DaysHelper.updateDay(d);
        }
        return "user?faces-redirect=true";
    }

    public String detailedFestival(Festivals f) throws ParseException {
        FestivalsHelper.visited(f);
        festival = f;
        performers = new LinkedList<>(f.getPerformerses());
        days = DaysHelper.getDaysFestivalByUser(festival);
        comments = CommentsHelper.getComments(f);
        avarageMark = FestivalsHelper.getAvarageMark(festival);
        images = new LinkedList<>();
        videos = new LinkedList<>();
        ticketMessage = "";
        List<Files> festFiles = FilesHelper.getFilesForFestival(f.getName());
        for (Files tempFile : festFiles) {
            if (tempFile.getIsAccepted() == 1) {
                if (tempFile.getIsVideo() == 0) {
                    images.add(tempFile.getLocation());
                } else {
                    videos.add(tempFile.getLocation());
                }
            }
        }
        return "details?faces-redirect=true";
    }

    public String login() throws ParseException {
        Users user = UsersHelper.getUserByUsername(username);
        if (user == null) {
            loginMessage = "User does not exist.";
            username = "";
            password = "";
            return null;
        } else if (user.getPassword().equals(password) == false) {
            loginMessage = "Incorrect password.";
            password = "";
            return null;
        } else if (user.getIsActive() == 0) {
            loginMessage = "User is not activated yet.";
            username = "";
            password = "";
            return null;
        } else {
            checkExpiredReservations();
            if (user.getIsAdmin() == 0) {
                loggedUser = UsersHelper.getUserByUsername(username);
                festivals = FestivalsHelper.get5UpcomingFestivals();
                LogginsHelper.newLoggin(username);
                Messages adminMessage = MessagesHelper.getUsersMessage(loggedUser);
                if (adminMessage != null) {
                    adminsMessage = adminMessage.getContent();
                }
            }
            return user.getIsAdmin() == 1 ? "admin?faces-redirect=true" : "user?faces-redirect=true";
        }
    }

    private void checkExpiredReservations() throws ParseException {
        Date now = new Date();
        List<Tickets> reservations = TicketsHelper.getAllReservations();
        for (Tickets reservation : reservations) {
            if (true) {
                long diff = now.getTime() - reservation.getReserved().getTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                if (minutes >= 2 * 24 * 60) {
                    if (reservation.getDate() == null) {
                        List<Day> days = DaysHelper.getDaysFestivalByUser(reservation.getFestivals());
                        for (Day d : days) {
                            d.setLeftTickets(d.getLeftTickets() + 1);
                            DaysHelper.updateDay(d);
                        }
                    } else {
                        Day day = DaysHelper.getDayByFestivalDate(reservation.getFestivals(), reservation.getDate());
                        day.setLeftTickets(day.getLeftTickets() + 1);
                        DaysHelper.updateDay(day);
                    }
                    TicketsHelper.delete(reservation);
                    // BANNING USER
                    int IDUser = reservation.getUsers().getIduser();
                    //int IDUser = UsersHelper.getUserByUsername(username).getIduser();
                    int IDFest = reservation.getFestivals().getIdfest();
                    BlockedListHelper.insertBan(IDUser, IDFest);
                    Users user = UsersHelper.getUserById(IDUser);
                    user.setReserved(user.getReserved() + 1);
                    UsersHelper.updateUser(user);
                }
            }
        }
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        username = password = null;
        loggedUser = null;
        loginMessage = "";
        return "index?faces-redirect=true";
    }

    public void search() throws ParseException {
        List<Festivals> resultingList = new LinkedList<>();
        festivals = FestivalsHelper.getAllUpcomingFestivals();
        for (Festivals f : festivals) {
            if (f.getName().toLowerCase().contains(searchName.toLowerCase())
                    && f.getPlace().toLowerCase().contains(searchPlace.toLowerCase())
                    && festivalsContainsPerformer(f, searchPerformer)
                    && festivalBeginDateValid(f, searchDateBegin)
                    && festivalEndDateValid(f, searchDateEnd)) {
                resultingList.add(f);
            }
        }
        festivals = resultingList;
    }

    private boolean festivalsContainsPerformer(Festivals f, String performerName) {
        List<Performers> performerList = FestivalsHelper.getPerformersList(f);
        for (Performers p : performerList) {
            if (p.getName().toLowerCase().contains(performerName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean festivalBeginDateValid(Festivals f, Date date) {
        if (date == null) {
            return true;
        } else {
            return f.getBegin().equals(date) || f.getBegin().after(date);
        }
    }

    private boolean festivalEndDateValid(Festivals f, Date date) {
        if (date == null) {
            return true;
        } else {
            return date.equals(f.getEnd()) || f.getEnd().before(date);
        }
    }

    public Controller() {
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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public Date getSearchDateBegin() {
        return searchDateBegin;
    }

    public void setSearchDateBegin(Date searchDateBegin) {
        this.searchDateBegin = searchDateBegin;
    }

    public Date getSearchDateEnd() {
        return searchDateEnd;
    }

    public void setSearchDateEnd(Date searchDateEnd) {
        this.searchDateEnd = searchDateEnd;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchPlace() {
        return searchPlace;
    }

    public void setSearchPlace(String searchPlace) {
        this.searchPlace = searchPlace;
    }

    public String getSearchPerformer() {
        return searchPerformer;
    }

    public void setSearchPerformer(String searchPerformer) {
        this.searchPerformer = searchPerformer;
    }

    public List<Festivals> getFestivals() {
        return festivals;
    }

    public void setFestivals(List<Festivals> festivals) {
        this.festivals = festivals;
    }

    public Users getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Users loggedUser) {
        this.loggedUser = loggedUser;
    }

    public Festivals getFestival() {
        return festival;
    }

    public void setFestival(Festivals festival) {
        this.festival = festival;
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

    public List<Performers> getPerformers() {
        return performers;
    }

    public void setPerformers(List<Performers> performers) {
        this.performers = performers;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public List<Tickets> getMyVisitedFestivals() {
        return myVisitedFestivals;
    }

    public void setMyVisitedFestivals(List<Tickets> myVisitedFestivals) {
        this.myVisitedFestivals = myVisitedFestivals;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public UploadedFile[] getFiles() {
        return files;
    }

    public void setFiles(UploadedFile[] files) {
        this.files = files;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTicketMessage() {
        return ticketMessage;
    }

    public void setTicketMessage(String ticketMessage) {
        this.ticketMessage = ticketMessage;
    }

    public Festivals getFestivalToRate() {
        return festivalToRate;
    }

    public void setFestivalToRate(Festivals festivalToRate) {
        this.festivalToRate = festivalToRate;
    }

    public String reservations() {
        myReservations = TicketsHelper.getReservationsForUser(loggedUser);
        return "reservations?faces-redirect=true";
    }

    public String removeReservation(Tickets ticket) throws ParseException {
        // all days ticket
        if (ticket.getDate() == null) {
            List<Day> days = DaysHelper.getDaysFestivalByUser(ticket.getFestivals());
            for (Day d : days) {
                d.setLeftTickets(d.getLeftTickets() + 1);
                DaysHelper.updateDay(d);
            }
        } else {
            Day day = DaysHelper.getDayByFestivalDate(ticket.getFestivals(), ticket.getDate());
            day.setLeftTickets(day.getLeftTickets() + 1);
            DaysHelper.updateDay(day);
        }
        TicketsHelper.delete(ticket);
        myReservations = TicketsHelper.getReservationsForUser(loggedUser);
        return "reservations?faces-redirect=true";
    }

    public List<Tickets> getMyReservations() {
        return myReservations;
    }

    public String getAdminsMessage() {
        return adminsMessage;
    }

    public void setAdminsMessage(String adminsMessage) {
        this.adminsMessage = adminsMessage;
    }

    public void setMyReservations(List<Tickets> myReservations) {
        this.myReservations = myReservations;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public List<Tickets> getMyAttendingFestivals() {
        return myAttendingFestivals;
    }

    public void setMyAttendingFestivals(List<Tickets> myAttendingFestivals) {
        this.myAttendingFestivals = myAttendingFestivals;
    }

    public String visitedFestivals() {
        myVisitedFestivals = TicketsHelper.getEndedFestivalTicketsByUser(loggedUser);
        myAttendingFestivals = TicketsHelper.getActiveFestivalTicketsByUser(loggedUser);
        return "visitedFestivals?faces-redirect=true";
    }

    public String rateFestivals() {
        myRateFestivals = new LinkedList<>();
        List<Tickets> myEndedFestivals = TicketsHelper.getEndedFestivalTicketsByUser(loggedUser);
        for (Tickets t : myEndedFestivals) {
            // Hasn't voted for this festival
            if (CommentsHelper.hasRated(loggedUser, t.getFestivals()) == false) {
                // Only vote for one ticket (if there are more)
                //if (myRateFestivals.contains(t.getFestivals()) == false) {
                myRateFestivals.add(t.getFestivals());
                //}
            }
        }
        return "rateFestivals?faces-redirect=true";
    }

    public List<Festivals> getMyRateFestivals() {
        return myRateFestivals;
    }

    public void setMyRateFestivals(List<Festivals> myRateFestivals) {
        this.myRateFestivals = myRateFestivals;
    }

    public String showFestivalToRate(Festivals f) {
        festivalToRate = f;
        return "rateFestival?faces-redirect=true";
    }

    private void getUploadedFiles(Festivals festival, Users user) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String destPath = ec.getRealPath("/") + "uploads\\";
        for (UploadedFile file : files) {
            if (file != null && file.getSize() > 0) {
                String str = file.getFileName();                                            // filename.mp4
                String ext = str.substring(str.lastIndexOf('.'), str.length());             // .mp4
                String fileType = str.substring(str.lastIndexOf('.') + 1, str.length());    // mp4           
                if (validFileType(fileType)) {
                    byte[] bFile = file.getContents();
                    String fileName = System.currentTimeMillis() + ext;
                    writeBytesToFile(bFile, destPath + fileName);
                    int isVideo = (fileType.equals("mp4")) ? 1 : 0;
                    Files newFile = new Files(festival, user, fileName, isVideo, 0, festival.getName());
                    FilesHelper.insert(newFile);
                }
            }
        }
    }

    private boolean validFileType(String fileType) {
        return ("mp4".equals(fileType) || "jpg".equals(fileType) || "jpeg".equals(fileType) || "png".equals(fileType));
    }

    private void writeBytesToFile(byte[] bFile, String fileDest) {
        try (FileOutputStream fileOuputStream = new FileOutputStream(fileDest)) {
            fileOuputStream.write(bFile);
        } catch (IOException e) {
        }
    }

    public String createRating() {

        Comments comment = new Comments(festivalToRate, loggedUser, commentText, value, festivalToRate.getName());
        CommentsHelper.insert(comment);

        festivalToRate.setNumberRatings(festivalToRate.getNumberRatings() + 1);
        festivalToRate.setTotalRating(festivalToRate.getTotalRating() + value);
        FestivalsHelper.updateFestival(festivalToRate);

        getUploadedFiles(festivalToRate, loggedUser);

        return "user?faces-redirect=true";

    }

    public String guestShow() throws ParseException {
        festivals = FestivalsHelper.get5BestRated();
        return "guest?faces-redirect=true";
    }

    public double getAvarageMark() {
        return avarageMark;
    }

    public void setAvarageMark(double avarageMark) {
        this.avarageMark = avarageMark;
    }

}
