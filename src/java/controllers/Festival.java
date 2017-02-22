/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.UploadedFile;
import utils.FestivalsHelper;
import utils.UsersHelper;

/**
 *
 * @author korisnik
 */
@Named(value = "festival")
@RequestScoped
public class Festival {

    private static final int MAXFILES = 11;

    private String message = "";
    private boolean error = false;

    private String name;
    private String place;
    private Date begin;
    private Date end;

    private String priceDay;
    private String priceFestival;
    private String ticketsPerUser;
    private String ticketsPerDay;

    private String facebook;
    private String twitter;
    private String instagram;
    private String youtube;

    private UploadedFile files[] = new UploadedFile[MAXFILES];

    public Festival() {
    }   

    public String createFestival() throws ParseException {

        if (begin == null || end == null) {
            message = "Date field cannot be null.";
            return null;
        }

        int festivalPriceDay = Integer.parseInt(this.priceDay);
        int festivalPrice = Integer.parseInt(this.priceFestival);
        int festivalTicketsPerUser = Integer.parseInt(this.ticketsPerUser);
        int festivalTicketsPerDay = Integer.parseInt(this.ticketsPerDay);

        // (1) Getting festival info    
        Festivals festival = new Festivals(name, place, begin, end, festivalPriceDay, festivalPrice, 0, festivalTicketsPerUser, festivalTicketsPerDay);
        festival.setFacebook(facebook);
        festival.setTwitter(twitter);
        festival.setInstagram(instagram);
        festival.setYoutube(youtube);

        // (2) Getting performers        
        Set<Performers> allPerformers = getAllPerformers(festival);
        festival.setPerformerses(allPerformers);

        // ! Validation !
        validateInput();
        if (error) {
            return null;
        }

        // (3) Getting files
        Set<Files> allFiles = getUploadedFiles(festival);
        festival.setFileses(allFiles);

        FestivalsHelper.insertFestival(festival);

        return "admin?faces-redirect=true";

    }

    private void validateInput() {
        if (begin == null) {
            error = true;
            message += "Begin date null.";
        }
        if (end == null) {
            error = true;
            message += "End date null.";
        }
        if (begin != null && end != null && begin.after(end)) {
            error = true;
            message += "Begin date cannot be after end date.";
        }
    }

    private Set<Performers> getAllPerformers(Festivals festival) throws ParseException {

        Set<Performers> performers = new LinkedHashSet<>();
        String[] names = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get("names");
        String[] begindates = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get("begindates");
        String[] begintimes = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get("begintimes");
        String[] enddates = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get("enddates");
        String[] endtimes = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get("endtimes");

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("") || begindates[i].equals("") || begintimes[i].equals("") || enddates[i].equals("") || endtimes[i].equals("")) {
                error = true;
                message += "Performer fields cannot be empty.";
                return null;
            }

            String[] splitDate = begindates[i].split("-");
            String[] splitDate2 = enddates[i].split("-");

            Date performerBegin = df.parse(splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0] + " " + begintimes[i]);
            Date performerEnd = df.parse(splitDate2[2] + "-" + splitDate2[1] + "-" + splitDate2[0] + " " + endtimes[i]);

            if (performerBegin.after(performerEnd)) {
                error = true;
                message += "Performer begin date is after end date.";
                return null;
            }
            if (performerBegin.before(festival.getBegin()) || performerEnd.after(festival.getEnd())) {
                error = true;
                message += "Performer date must be between festival's date.";
                return null;
            }
            Performers newPerformer = new Performers(festival, names[i], performerBegin, performerEnd);
            performers.add(newPerformer);
        }
        return performers;
    }

    private Set<Files> getUploadedFiles(Festivals festival) {

        Set<Files> resultFiles = new LinkedHashSet<>();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String destPath = ec.getRealPath("/") + "uploads\\";

        Users admin = UsersHelper.getAdminUser();

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

                    Files newFile = new Files(festival, admin, fileName, isVideo, 1, festival.getName());
                    resultFiles.add(newFile);

                }

            }

        }

        return resultFiles;

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

    public String getFormFieldValue(String id) {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getParameter(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getPriceDay() {
        return priceDay;
    }

    public void setPriceDay(String priceDay) {
        this.priceDay = priceDay;
    }

    public String getPriceFestival() {
        return priceFestival;
    }

    public void setPriceFestival(String priceFestival) {
        this.priceFestival = priceFestival;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTicketsPerUser() {
        return ticketsPerUser;
    }

    public void setTicketsPerUser(String ticketsPerUser) {
        this.ticketsPerUser = ticketsPerUser;
    }

    public String getTicketsPerDay() {
        return ticketsPerDay;
    }

    public void setTicketsPerDay(String ticketsPerDay) {
        this.ticketsPerDay = ticketsPerDay;
    }

    public UploadedFile[] getFiles() {
        return files;
    }

    public void setFiles(UploadedFile[] files) {
        this.files = files;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
