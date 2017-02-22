/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Festivals;
import entities.Files;
import entities.Performers;
import entities.Users;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.model.UploadedFile;
import utils.FestivalsHelper;
import utils.UsersHelper;

/**
 *
 * @author korisnik
 */
@Named(value = "festivalViaFile")
@SessionScoped
public class FestivalViaFile implements Serializable {

    private static final int MAXFILES = 11;

    private String message = "";
    private UploadedFile file;
    private UploadedFile files[] = new UploadedFile[MAXFILES];
    private Festivals festival;
    private List<Performers> performers;

    private List<String> images;
    private List<String> videos;

    public String createFestival() throws ParseException, IOException {

        if (file.getSize() == 0) {
            message = "You haven't provided file.";
            return null;
        }

        String str = file.getFileName();                                                        // fest.csv
        String ext = str.substring(str.lastIndexOf('.'), str.length());                         // .csv
        String fileType = str.substring(str.lastIndexOf('.') + 1, str.length());                // csv

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String destPath = ec.getRealPath("/") + "uploads\\";

        // CSV or JSON NOT Provided
        if (fileType.equals("csv") == false && fileType.equals("json") == false) {
            message = "Please choose correct file format.";
            return null;
        }

        byte[] bFile = file.getContents();

        // --
        // -- CSV found
        // --
        if (fileType.equals("csv")) {

            writeBytesToFile(bFile, destPath + "FESTIVALTEMP.csv");
            // CSV NOT Match Pattern
            if (!checkCSVformat(destPath + "FESTIVALTEMP.csv")) {
                message = "Provided .csv file format does not match festival csv pattern.";
                return null;
            }
            Festivals festival = createFromCSV(destPath + "FESTIVALTEMP.csv");
            Set<Files> files = getUploadedFiles(festival);
            festival.setFileses(files);

            this.festival = festival;
            this.performers = new ArrayList<>(festival.getPerformerses());

            images = new ArrayList<>();
            videos = new ArrayList<>();

            for (Files tempFile : festival.getFileses()) {
                if (tempFile.getIsVideo() == 0) {
                    images.add(tempFile.getLocation());
                } else {
                    videos.add(tempFile.getLocation());
                }
            }

        } else {

            try {
                writeBytesToFile(bFile, destPath + "FESTIVALTEMP.json");
                Festivals festival = new Festivals();
                Set<Performers> performersSet = new LinkedHashSet<>();

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

                String jsonFileContent = readContentFromFile(destPath + "FESTIVALTEMP.json");

                JSONObject jsonObject = new JSONObject(jsonFileContent);
                jsonObject = jsonObject.getJSONObject("Festival");

                festival.setName(jsonObject.getString("Name"));
                festival.setPlace(jsonObject.getString("Place"));

                String tempDate = jsonObject.getString("StartDate") + " 00:00:00 AM";
                Date dateBegin = df.parse(tempDate);
                festival.setBegin(dateBegin);

                tempDate = jsonObject.getString("EndDate") + " 00:00:00 AM";
                Date dateEnd = df.parse(tempDate);
                festival.setEnd(dateEnd);

                JSONArray number = jsonObject.getJSONArray("Tickets");
                festival.setPriceDay(Integer.parseInt(number.getString(0)));
                festival.setPriceFestival(Integer.parseInt(number.getString(1)));

                number = jsonObject.getJSONArray("Amount");
                festival.setTicketsPerUser(Integer.parseInt(number.getString(0)));
                festival.setTicketsPerDay(Integer.parseInt(number.getString(1)));

                JSONArray performerList = jsonObject.getJSONArray("PerformersList");

                for (int i = 0; i < performerList.length(); i++) {

                    JSONObject performer = performerList.getJSONObject(i);
                    Performers newPerformer = new Performers();

                    newPerformer.setFestivals(festival);
                    newPerformer.setName(performer.getString("Performer"));

                    String tempDateString = performer.getString("StartDate");
                    String tempTimeString = performer.getString("StartTime");
                    String dateTime = tempDateString + " " + tempTimeString;
                    Date beginDatePerformer = df.parse(dateTime);

                    newPerformer.setStart(beginDatePerformer);

                    tempDateString = performer.getString("EndDate");
                    tempTimeString = performer.getString("EndTime");
                    dateTime = tempDateString + " " + tempTimeString;
                    Date endDatePerformer = df.parse(dateTime);

                    newPerformer.setEnd(endDatePerformer);

                    performersSet.add(newPerformer);

                }

                JSONArray socialNetworkList = jsonObject.getJSONArray("SocialNetworks");

                for (int i = 0; i < socialNetworkList.length(); i++) {

                    JSONObject p = socialNetworkList.getJSONObject(i);
                    String socName = p.getString("Name");
                    String socLink = p.getString("Link");

                    if (socName.equals("Facebook")) {
                        festival.setFacebook(socLink);
                    }
                    if (socName.equals("Twitter")) {
                        festival.setTwitter(socLink);
                    }
                    if (socName.equals("Instagram")) {
                        festival.setInstagram(socLink);
                    }
                    if (socName.equals("YouTube")) {
                        festival.setYoutube(socLink);
                    }

                }

                festival.setPerformerses(performersSet);

                Set<Files> files = getUploadedFiles(festival);
                festival.setFileses(files);

                this.festival = festival;
                this.performers = new ArrayList<>(festival.getPerformerses());
                images = new ArrayList<>();
                videos = new ArrayList<>();

                for (Files tempFile : festival.getFileses()) {
                    if (tempFile.getIsVideo() == 0) {
                        images.add(tempFile.getLocation());
                    } else {
                        videos.add(tempFile.getLocation());
                    }
                }
            } catch (IOException | JSONException | ParseException | NumberFormatException e) {
                message = "Provided .json file format does not match festival json pattern.";
                return null;
            }
            
        }

        return "newfestivalpreview?faces-redirect=true";

    }

    public String approve() {
        message = "";
        FestivalsHelper.insertFestival(festival);
        return "admin?faces-redirect=true";
    }

    public String reject() {
        message = "";
        deleteSavedFiles();
        return "admin?faces-redirect=true";
    }

    private void deleteSavedFiles() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String destPath = ec.getRealPath("/") + "uploads\\";
        for (String imgName : images) {
            File file = new File(destPath + imgName);
            if (file.exists()) {
                file.delete();
            }
        }
        for (String vidName : videos) {
            File file = new File(destPath + vidName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void writeBytesToFile(byte[] bFile, String fileDest) {

        try {
            FileOutputStream fileOuputStream = new FileOutputStream(fileDest);
            fileOuputStream.write(bFile);
            fileOuputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FestivalViaFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FestivalViaFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Festivals createFromCSV(String csvLocation) {

        Festivals festival = new Festivals();
        Set<Performers> performers = new LinkedHashSet<>();
        String lineParts[];
        Date tempDate;
        BufferedReader br = null;
        String line;

        try {
            br = new BufferedReader(new FileReader(csvLocation));
            line = br.readLine();
            line = br.readLine();
            lineParts = line.split("\",\"");
            // "Beer Fest   ","   Belgrade, Usce   ","   17/08/2017    ","   21/08/2017"            
            festival.setName(lineParts[0].substring(1, lineParts[0].length()));     // name: Beer Fest
            festival.setPlace(lineParts[1]);    // place: Belgrade, Usce
            tempDate = createDate(lineParts[2]);
            festival.setBegin(tempDate);
            tempDate = createDate(lineParts[3].substring(0, lineParts[3].length() - 1));
            festival.setEnd(tempDate);

            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            lineParts = line.split("\",\"");
            // "per day","200"      
            int pricePerDay = Integer.parseInt(lineParts[1].substring(0, lineParts[1].length() - 1));
            line = br.readLine();
            lineParts = line.split("\",\"");
            // "whole festival","800"
            int priceWholeFestival = Integer.parseInt(lineParts[1].substring(0, lineParts[1].length() - 1));
            festival.setPriceDay(pricePerDay);
            festival.setPriceFestival(priceWholeFestival);

            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            lineParts = line.split("\",\"");
            // "per user","4"
            int perUser = Integer.parseInt(lineParts[1].substring(0, lineParts[1].length() - 1));
            line = br.readLine();
            lineParts = line.split("\",\"");
            // "per day","100"
            int perDay = Integer.parseInt(lineParts[1].substring(0, lineParts[1].length() - 1));
            festival.setTicketsPerUser(perUser);
            festival.setTicketsPerDay(pricePerDay);

            line = br.readLine();
            line = br.readLine();
            line = br.readLine();

            while (line.matches(FOURTH_VALUE)) {
                lineParts = line.split("\",\"");
                // "Viva Vox","17/08/2017","17/08/2017","08:00:00 PM","09:00:00 PM"
                String pName = lineParts[0].substring(1, lineParts[0].length());
                Performers performer = new Performers();
                performer.setName(pName);
                Date begin = createDateTime(lineParts[1], lineParts[3]);
                Date end = createDateTime(lineParts[2], lineParts[4].substring(0, lineParts[4].length() - 1));
                performer.setStart(begin);
                performer.setEnd(end);
                performer.setFestivals(festival);
                performers.add(performer);
                line = br.readLine();
            }

            festival.setPerformerses(performers);
            line = br.readLine();

            while ((line = br.readLine()) != null) {
                // "Facebook","https://www.facebook.com/belgradebeerfest"
                lineParts = line.split("\",\"");
                String kind = lineParts[0].substring(1, lineParts[0].length());
                if (kind.equals("Facebook")) {
                    festival.setFacebook(lineParts[1].substring(0, lineParts[1].length() - 1));
                }
                if (kind.equals("Twitter")) {
                    festival.setTwitter(lineParts[1].substring(0, lineParts[1].length() - 1));
                }
                if (kind.equals("Instagram")) {
                    festival.setInstagram(lineParts[1].substring(0, lineParts[1].length() - 1));
                }
                if (kind.equals("YouTube")) {
                    festival.setYoutube(lineParts[1].substring(0, lineParts[1].length() - 1));
                }
            }

        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return festival;
    }

    private Date createDate(String text) throws ParseException {
        // got String 17/08/2017
        // return Date 2017-08-17
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = df.parse(text);
        return date;
    }

    private Date createDateTime(String dateString, String timeString) throws ParseException {
        // "17/08/2017"
        // "08:00:00 PM"
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Date date = df.parse(dateString + " " + timeString);
        return date;
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

                    String location = destPath + fileName;

                    writeBytesToFile(bFile, location);

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

    //
    //
    // --
    // --   CSV Validation
    // -- 
    //
    //
    // "Festival","Place","StartDate","EndDate"
    private static final String FIRST_HEADER = "^\"Festival\",\"Place\",\"StartDate\",\"EndDate\"$";
    // "Beer Fest","Belgrade, Usce", "17/08/2017", "21/08/2017"
    private static final String FIRST_VALUE = "^\\\".+\\\",\\\".+\\\",\"\\d\\d[/]\\d\\d[/]\\d\\d\\d\\d\"$";

    // "TicketType", "Price"
    private static final String SECOND_HEADER = "^\"TicketType\",\"Price\"$";
    // "per day", "200"
    private static final String SECOND_VALUE1 = "^\\\"per day\\\",\\\"\\d+\\\"$";
    // "whole festival", "800"
    private static final String SECOND_VALUE2 = "^\\\"whole festival\\\",\\\"\\d+\\\"$";

    // "TicketsInfo", "Count"
    private static final String THIRD_HEADER = "^\\\"TicketsInfo\\\",\\\"Count\\\"$";
    // "per user", "4"
    private static final String THIRD_VALUE1 = "^\\\"per user\\\",\\\"\\d+\\\"$";
    // "per day", "100"
    private static final String THIRD_VALUE2 = "^\\\"per day\\\",\\\"\\d+\\\"$";

    // "Performer","StartDate","EndDate","StartTime","EndTime"
    private static final String FOURTH_HEADER = "^\\\"Performer\\\",\\\"StartDate\\\",\\\"EndDate\\\",\\\"StartTime\\\",\\\"EndTime\\\"$";
    // "Viva Vox","17/08/2017","17/08/2017","08:00:00 PM","09:00:00 PM"
    private static final String FOURTH_VALUE = "^\\\".+\\\",\\\"\\d\\d[/]\\d\\d[/]\\d\\d\\d\\d\\\",\\\"\\d\\d[/]\\d\\d[/]\\d\\d\\d\\d\\\",\\\"\\d\\d:\\d\\d:\\d\\d (P|A)M\\\",\\\"\\d\\d:\\d\\d:\\d\\d (P|A)M\\\"$";

    // "Social Network","Link"
    private static final String FIFTH_HEADER = "^\\\"Social Network\\\",\\\"Link\\\"$";
    // "Facebook", "https://www.facebook.com/belgradebeerfest"
    // "Twitter", "http://twitter.com/bgbeerfest"
    // "Instagram", "http://instagram.com/belgradebeerfest"
    // "YouTube", "http://www.youtube.com/Belgradebeerfest"
    private static final String FIFTH_VALUE = "\\\"(Facebook|Twitter|Instagram|Youtube)\\\",\\\".+\\\"";

    private boolean checkCSVformat(String csvFile) {
        BufferedReader br = null;
        String line;
        boolean correctFormat = true;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();
            // check: "Festival","Place","StartDate","EndDate"
            if (line == null || !line.matches(FIRST_HEADER)) {
                return false;
            }
            line = br.readLine();
            // check: "Beer Fest","Belgrade, Usce", "17/08/2017", "21/08/2017"
            if (line == null || !line.matches(FIRST_VALUE)) {
                return false;
            }
            // blank row
            line = br.readLine();
            if (line == null) {
                return false;
            }
            line = br.readLine();
            // check: "TicketType", "Price"
            if (line == null || !line.matches(SECOND_HEADER)) {
                return false;
            }
            line = br.readLine();
            // check: "per day", "200"
            if (line == null || !line.matches(SECOND_VALUE1)) {
                return false;
            }
            line = br.readLine();
            // check: "per festival", "800"
            if (line == null || !line.matches(SECOND_VALUE2)) {
                return false;
            }
            // blank row
            line = br.readLine();
            if (line == null) {
                return false;
            }
            line = br.readLine();
            // check: "TicketsInfo", "Count"
            if (line == null || !line.matches(THIRD_HEADER)) {
                return false;
            }
            line = br.readLine();
            // check: "per user", "4"
            if (line == null || !line.matches(THIRD_VALUE1)) {
                return false;
            }
            line = br.readLine();
            // check: "per day", "100"
            if (line == null || !line.matches(THIRD_VALUE2)) {
                return false;
            }
            // blank row
            line = br.readLine();
            if (line == null) {
                return false;
            }
            // check: "Performer","StartDate","EndDate","StartTime","EndTime"
            line = br.readLine();
            if (line == null || !line.matches(FOURTH_HEADER)) {
                return false;
            }
            // check first: "Viva Vox","17/08/2017","17/08/2017","08:00:00 PM","09:00:00 PM"
            line = br.readLine();
            if (line == null || !line.matches(FOURTH_VALUE)) {
                return false;
            }
            // skip all performers
            while (line != null && line.matches(FOURTH_VALUE)) {
                line = br.readLine();
            }
            if (line != null) {
                System.out.println("line failed: " + line);
            }
            if (line == null) {
                return false;
            }
            // blank row
            line = br.readLine();
            // check: "Social Network","Link"
            if (line == null || !line.matches(FIFTH_HEADER)) {
                if (!line.matches(FIFTH_HEADER)) {
                    System.out.println("lUAKA");
                }
                return false;
            }
            line = br.readLine();
            if (line == null || !line.matches(FIFTH_VALUE)) {
                return false;
            }
            while (line != null && line.matches(FIFTH_VALUE)) {
                line = br.readLine();
            }

        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public String readContentFromFile(String fileDest) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(fileDest);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        String fileAsString = sb.toString();
        return fileAsString;
    }

    public FestivalViaFile() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public UploadedFile[] getFiles() {
        return files;
    }

    public void setFiles(UploadedFile[] files) {
        this.files = files;
    }

    public Festivals getFestival() {
        return festival;
    }

    public void setFestival(Festivals festival) {
        this.festival = festival;
    }

    public List<Performers> getPerformers() {
        return performers;
    }

    public void setPerformers(List<Performers> performers) {
        this.performers = performers;
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

}
