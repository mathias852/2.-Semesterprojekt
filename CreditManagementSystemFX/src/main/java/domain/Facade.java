package domain;

import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import persistence.ExportHandler;
import persistence.PersistenceHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class Facade {
    private static Facade instance;
    private List<Program> programs = new ArrayList<>();
    private List<TVSeries> tvSeriesList = new ArrayList<>();
    private List<CreditedPerson> creditedPeople = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    ExportHandler exportHandler = new ExportHandler();
    IPersistenceHandler persistenceHandler = PersistenceHandler.getInstance();


    //All create methods are void because we don't use the return-value **Note to ourself
    //Create method for episode when it's not stored in the DB(Text-files)
    public void createEpisode(TVSeries tv, String name, String description, UUID createdBy, int episodeNo, int seasonNo, int duration, boolean approved, String production) {
        createEpisode(UUID.randomUUID(), tv, name, description, createdBy, episodeNo, seasonNo, duration, approved, production);
    }

    //Create method when read from the DB(Text-file)
    public void createEpisode(UUID uuid, TVSeries tv, String name, String description, UUID createdBy, int episodeNo, int seasonNo, int duration, boolean approved, String production) {
        Episode program = new Episode(uuid, tv, name, description, createdBy, episodeNo, seasonNo, duration, approved, production);
        tv.addEpisode(program);
        programs.add(program);
        persistenceHandler.storeEpisode(program);
        Notification notification = new Notification(String.format("%s created an episode with the title \"%s\" on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    //Create method for transmission when it's not stored in the DB(Text-files)
    public void createTransmission(String name, String description, UUID createdBy, int duration, boolean approved, String production) {
        createTransmission(UUID.randomUUID(), name, description, createdBy, duration, approved, production);
    }

    //Create method when read from the DB(Text-file)
    public void createTransmission(UUID uuid, String name, String description, UUID createdBy, int duration, boolean approved, String production) {
        Transmission program = new Transmission(uuid, name, description, createdBy, duration, approved, production);
        programs.add(program);
        persistenceHandler.storeTransmission(program);
        Notification notification = new Notification(String.format("%s created a transmission with the title \"%s\" on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    //Create method for tv-series when it's not stored in the DB(Text-files)
    public void createTvSeries(String name, String description, UUID createdBy) {
        createTvSeries(UUID.randomUUID(), name, description, createdBy);
    }

    //Create method when read from the DB(Text-file)
    public void createTvSeries(UUID uuid, String name, String description, UUID createdBy) {
        TVSeries tvSeries = new TVSeries(uuid, name, description, createdBy);
        tvSeriesList.add(tvSeries);
        persistenceHandler.storeTVSeries(tvSeries);
        Notification notification = new Notification(String.format("%s created a TV-series with the name \"%s\" on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    //Create method when read from the DB(Text-file) **Note: We do not store a UUID which is why we don't have two create-methods for credits
    public void createCredit(CreditedPerson creditedPerson, Credit.Function function, Program creditedProgram) {
        Credit c = new Credit(creditedPerson, function);
        creditedProgram.addCredit(c);
        persistenceHandler.storeCredit(creditedProgram, c);
        Notification notification = new Notification(String.format("%s added %s as a credit to the program %s with the role %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(),
                creditedPerson.getName(), creditedProgram.getName(), function.role, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    //Create method for person when it's not stored in the DB(Text-files)
    public void createPerson(String name) {
        createPerson(UUID.randomUUID(), name);
    }

    //Create method when read from the DB(Text-file)
    public void createPerson(UUID uuid, String name) {
        CreditedPerson creditedPerson = new CreditedPerson(uuid, name);
        creditedPeople.add(creditedPerson);
        persistenceHandler.storeCreditedPerson(creditedPerson);
        Notification notification = new Notification(String.format("%s created a person with the name \"%s\" on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    public void importFromDatabase() {
        tvSeriesList.addAll(persistenceHandler.getTVSeries());

        //programs.addAll(persistenceHandler.getEpisodes());
        for (Episode episode : persistenceHandler.getEpisodes()) {
            for (TVSeries tvSeries : tvSeriesList) {
                if (episode.getTvSeries().getUuid().equals(tvSeries.getUuid())) {
                    tvSeries.addEpisode(episode);
                }
            }
            programs.add(episode);
        }

        programs.addAll(persistenceHandler.getTransmissions());
        creditedPeople.addAll(persistenceHandler.getCreditedPeople());

        for (Program p : programs) {
            if (persistenceHandler.getCredits(p) != null) {
                for (Credit c : persistenceHandler.getCredits(p)) {
                    if (c != null) {
                        p.addCredit(c);
                    }
                }
            }
        }
        notifications.addAll(persistenceHandler.getNotifications());
    }


    //Export/save programs, persons & credits to txt
    public void exportToTxt() throws IOException {
        String krediteringDanmarkReport = "";

        //Loop through the programs array - if instance of transmission, we store that instance as a single line in the transmission-file
        for (Program p : programs) {
            if (p instanceof Transmission) {
                krediteringDanmarkReport += "Program ID: " + p.getUuid() + "\nName: " + p.getName() + "\nCredits: ";
                //If the transmission has credits associated we store them in the credit-file
                if (p.getCredits() != null) {
                    for (Credit credit : p.getCredits()) {
                        krediteringDanmarkReport += "\n\tPerson ID: " + credit.getCreditedPerson().getUuid() + "\n\t\tName: " +
                                credit.getCreditedPerson().getName() + "\n\t\tCredited role: " + credit.getFunction().role;
                    }
                } else {
                    krediteringDanmarkReport += "\n\t<None>";
                }
                krediteringDanmarkReport += "\n\n";
                //If instance of Episode - store in the episode-file
            } else if (p instanceof Episode) {
                Episode e = (Episode) p;
                krediteringDanmarkReport += "Program ID: " + p.getUuid() + "\nName: " + p.getName()
                        + "\nSeason number: " + e.getSeasonNo() + "\nEpisode number: " + e.getEpisodeNo() + "\nCredits: ";
                //If the episode has credits associated we store them in the credit-file
                if (e.getCredits() != null) {
                    for (Credit credit : e.getCredits()) {
                        krediteringDanmarkReport += "\n\tPerson ID: " + credit.getCreditedPerson().getUuid() + "\n\t\tName: " +
                                credit.getCreditedPerson().getName() + "\n\t\tCredited role: " + credit.getFunction().role;
                    }
                } else {
                    krediteringDanmarkReport += "\n\t<None>";
                }
                krediteringDanmarkReport += "\n\n";
            }
        }
        exportHandler.exportKrediteringDanmarkReport(krediteringDanmarkReport);
    }

    /*
    //Here we import the text-files and create a new instance of every line from each file
    public void importFromTxt() {
        //Read each file and for every string-array(Corresponding to one line) we call the create-method for the respective file
        ArrayList<String[]> transmissions = exportHandler.readTransmission();
        for (String[] transmissionLine : transmissions) {
            createTransmission(UUID.fromString(transmissionLine[0]), transmissionLine[1], transmissionLine[2],
                    UUID.fromString(transmissionLine[3]), Integer.parseInt(transmissionLine[4]), Boolean.parseBoolean(transmissionLine[5]), transmissionLine[6]);

        }

        ArrayList<String[]> tvSeries = exportHandler.readTvSeries();
        for (String[] tvLine : tvSeries) {
            createTvSeries(UUID.fromString(tvLine[0]), tvLine[1], tvLine[2], UUID.fromString(tvLine[3]));
        }

        ArrayList<String[]> episodes = exportHandler.readEpisode();
        for (String[] episodeLine : episodes) {
            createEpisode(UUID.fromString(episodeLine[0]), getTvSeriesFromUuid(UUID.fromString(episodeLine[1])),
                    episodeLine[2], episodeLine[3], UUID.fromString(episodeLine[4]), Integer.parseInt(episodeLine[5]),
                    Integer.parseInt(episodeLine[6]), Integer.parseInt(episodeLine[7]), Boolean.parseBoolean(episodeLine[8]), episodeLine[9]);
        }

        ArrayList<String[]> people = exportHandler.readPerson();
        for (String[] personLine : people) {
            createPerson(UUID.fromString(personLine[0]), personLine[1]);
        }

        ArrayList<String[]> credit = exportHandler.readCredit();
        for (String[] creditLine : credit) {
            createCredit(getPersonFromUuid(UUID.fromString(creditLine[0])), getFunction(creditLine[1]),
                    getProgramFromUuid(UUID.fromString(creditLine[2])));
        }
    }

     */

    //To receive the function(Enum) when the string-value is provided
    public Credit.Function getFunction(String string) {
        for (Credit.Function function : Credit.Function.values()) {
            if (function.role.equalsIgnoreCase(string)) {
                return function;
            }
        }
        return null;
    }


    //Iterates through the tv series in facade and returns the tv series that the episode parameter is in
    public TVSeries getTvSeriesFromEpisode(Episode episode) {
        for (TVSeries tvSeries : getTvSeriesList()) {
            for (Integer i : tvSeries.getSeasonMap().keySet()) {
                for (Episode episodeCheck : tvSeries.getSeasonMap().get(i)) {
                    if (episode.equals(episodeCheck)) {
                        return tvSeries;
                    }
                }
            }
        }
        return null;
    }

    //To get the tvSeries instance when you have the uuid
    public TVSeries getTvSeriesFromUuid(UUID uuid) {
        for (TVSeries tvSeries : getTvSeriesList()) {
            if (tvSeries.getUuid().equals(uuid)) {
                return tvSeries;
            }
        }
        return null;
    }

    //To get the creditedPerson instance when you have the uuid
    public CreditedPerson getPersonFromUuid(UUID uuid) {
        for (CreditedPerson person : getCreditedPeople()) {
            if (person.getUuid().equals(uuid)) {
                return person;
            }
        }
        return null;
    }

    //To get the program instance when you have the uuid
    public Program getProgramFromUuid(UUID uuid) {
        for (Program program : getPrograms()) {
            if (program.getUuid().equals(uuid)) {
                return program;
            }
        }
        return null;
    }

    public Program getProgramFromCreatedBy(String programName, UUID uuid) {
        for (Program program : getPrograms()) {
            if (program.getName().equals(programName) && program.getCreatedBy().equals(uuid)) {
                return program;
            }
        }
        return null;
    }

    public Program getProgramFromCredit(Credit credit) {
        for (Program program : getPrograms()) {
            if (program.getCredits() != null) {
                for (Credit c : program.getCredits()) {
                    if (c.equals(credit)) {
                        return program;
                    }
                }
            }
        }
        return null;
    }

    public Notification getNotificationFromTitle(String title) {
        for (Notification n : getNotifications()) {
            if (n.getTitle().equals(title)) {
                return n;
            }
        }
        return null;
    }


    //Update the values of the selected transmission
    public void updateTransmission(Program program, String name, String description, int duration, String production) {
        Transmission p = (Transmission) program;

        Notification notification = new Notification(String.format("%s updated a transmission with the UUID %s and (new) name %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), program.getUuid().toString(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);

        if (name != null) {
            p.setName(name);
        }
        if (description != null) {
            p.setDescription(description);
        }
        if (duration != -1) {
            p.setDuration(duration);
        }
        p.setProduction(production);
        p.setApproved(false);
        persistenceHandler.updateTransmission(p);
    }

    //Update the values of the selected episode
    public void updateEpisode(Program program, String name, String description, int duration, int seasonNo, int episodeNo, TVSeries tvSeries, String production) {
        Episode p = (Episode) program;

        Notification notification = new Notification(String.format("%s updated an episode with the UUID %s and (new) name %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), program.getUuid().toString(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);

        int oldSeasonNo = p.getSeasonNo();
        int oldEpisodeNo = p.getEpisodeNo(); //not used right now
        if (name != null) {
            p.setName(name);
        }
        if (description != null) {
            p.setDescription(description);
        }
        if (duration != -1) {
            p.setDuration(duration);
        }
        if (episodeNo != -1) {
            p.setEpisodeNo(episodeNo);
        }
        if (seasonNo != -1) {
            tvSeries.getSeasonMap().get(oldSeasonNo).remove(p);
            if (tvSeries.getSeasonMap().get(oldSeasonNo).size() < 1) {
                tvSeries.getSeasonMap().remove(oldSeasonNo);
            }
            p.setSeasonNo(seasonNo);
            tvSeries.addEpisode(p);
        }
        if (tvSeries != null) {
            p.setTvSeries(tvSeries);
        }
        p.setProduction(production);
        p.setApproved(false);
        persistenceHandler.updateEpisode(p);
    }

    //Update the values of the selected tv-series
    public void updateTvSeries(TVSeries tvSeries, String name, String description) {
        Notification notification = new Notification(String.format("%s updated a TV-series with the UUID %s and (new) name %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), tvSeries.getUuid().toString(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);

        if (name != null) {
            tvSeries.setName(name);
        }
        if (description != null) {
            tvSeries.setDescription(description);
        }
        persistenceHandler.updateTVSeries(tvSeries);
    }

    //To approve a given program
    public void approveProgram(Program program) {
        program.setApproved(true);
        if (program instanceof Episode) {
            persistenceHandler.updateEpisode((Episode) program);
        } else if (program instanceof Transmission) {
            persistenceHandler.updateTransmission((Transmission) program);
        }
        Notification notification = new Notification(String.format("%s approved a program with the UUID %s and name %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), program.getUuid().toString(), program.getName(), currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    public void updateCredit(Credit credit, Credit.Function function) {
        String oldRole = credit.getFunction().role;
        credit.setFunction(function);
        Program program = getProgramFromCredit(credit);
        if (program.isApproved()) {
            program.setApproved(false);
        }
        persistenceHandler.updateCredit(getProgramFromCredit(credit), credit, oldRole);
        Notification notification = new Notification(String.format("%s updated a credit for %s on %s from %s to now be %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), credit.getCreditedPerson().getName(),
                getProgramFromCredit(credit).getName(), oldRole, function.role, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    public void updatePerson(CreditedPerson creditedPerson, String name) {
        creditedPerson.setName(name);
        persistenceHandler.updateCreditedPerson(creditedPerson);
        Notification notification = new Notification(String.format("%s updated a person with the UUID %s to now be named %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), creditedPerson.getUuid().toString(), name, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    public void setNotificationAsSeen(Notification notification) {
        notification.setSeen(true);
        persistenceHandler.updateNotification(notification);
    }

    public void deleteCredit(Program program, Credit credit) {
        program.deleteCredit(credit);
        persistenceHandler.deleteCredit(program, credit);
        Notification notification = new Notification(String.format("%s deleted a credit for %s on \"%s\" credited for %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), credit.getCreditedPerson().getName(),
                getProgramFromCredit(credit).getName(), credit.getFunction().role, currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    public void deleteProgram(Program program) {
        programs.remove(program);

        if (program instanceof Episode) {
            Episode episode = (Episode) program;
            persistenceHandler.deleteEpisode(episode);

            getTvSeriesFromEpisode(episode).getSeasonMap().get(episode.getSeasonNo()).remove(episode);
        } else if (program instanceof Transmission) {
            persistenceHandler.deleteTransmission((Transmission) program);
        }
        Notification notification = new Notification(String.format("%s deleted a program with the name %s and UUID %s on %s",
                LoginHandler.getInstance().getCurrentUser().getUsername(), program.getName(),
                program.getUuid().toString(), currentTime()));
        notifications.add(notification);
        persistenceHandler.storeNotification(notification);
    }

    //To get the current date + time formatted
    public String currentTime() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return myDateObj.format(myFormatObj);
    }

    public List<Credit.Function> getFunctions() {
        return Arrays.asList(Credit.Function.values());
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public List<TVSeries> getTvSeriesList() {
        return tvSeriesList;
    }

    public List<CreditedPerson> getCreditedPeople() {
        return creditedPeople;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
