package domain;

import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import persistence.PersistenceHandler;

import java.util.*;

public class Facade {
    private List<Program> programs = new ArrayList<>();
    private List<TVSeries> tvSeriesList = new ArrayList<>();
    private List<CreditedPerson> creditedPeople = new ArrayList<>();

    PersistenceHandler persistenceHandler = new PersistenceHandler();

    public static void main(String[] args) {
    }


/*    public void createStuff() {
        TVSeries badehotellet = createTvSeries("Badehotellet", "En serie", 1);
        Episode ep1 = createEpisode(badehotellet, "Badehotellet 1", "Badehotellet the sequel", 1, 1, 1, 60);
        Episode ep2 = createEpisode(badehotellet, "Badehotellet 2", "Badehotellet the sequel", 1, 2, 1, 60);
        CreditedPerson cp = createPerson("Jens Jensen");
        CreditedPerson cp2 = createPerson("Hans Hansen");
        Credit c = createCredit(ep1, cp, Credit.Function.VISUALARTIST);
        Credit c2 = createCredit(ep1, cp2, Credit.Function.EDITOR);
    }*/

    //All create methods are void because we don't use the return-value **Note to ourself

    //Create method for episode when it's not stored in the DB(Text-files)
    public void createEpisode(TVSeries tv, String name, String description, int createdBy, int episodeNo, int seasonNo, int duration, boolean approved) {
        createEpisode(UUID.randomUUID(), tv, name, description, createdBy, episodeNo, seasonNo, duration, approved);
    }

    //Create method when read from the DB(Text-file)
    public void createEpisode(UUID uuid, TVSeries tv, String name, String description, int createdBy, int episodeNo, int seasonNo, int duration, boolean approved) {
        Episode program = new Episode(uuid, tv, name, description, createdBy, episodeNo, seasonNo, duration, approved);
        tv.addEpisode(program);
        programs.add(program);
    }

    //Create method for transmission when it's not stored in the DB(Text-files)
    public void createTransmission(String name, String description, int createdBy, int duration, boolean approved) {
        createTransmission(UUID.randomUUID(), name, description, createdBy, duration, approved);
    }

    //Create method when read from the DB(Text-file)
    public void createTransmission(UUID uuid, String name, String description, int createdBy, int duration, boolean approved) {
        Program program = new Transmission(uuid, name, description, createdBy, duration, approved);
        programs.add(program);
    }

    //Create method for tv-series when it's not stored in the DB(Text-files)
    public void createTvSeries(String name, String description, int createdBy) {
        createTvSeries(UUID.randomUUID(), name, description, createdBy);
    }

    //Create method when read from the DB(Text-file)
    public void createTvSeries(UUID uuid, String name, String description, int createdBy) {
        TVSeries tvSeries = new TVSeries(uuid, name, description, createdBy);
        tvSeriesList.add(tvSeries);
    }

    //Create method when read from the DB(Text-file) **Note: We do not store a UUID which is wuy we don't have two create-methods for credits
    public void createCredit(CreditedPerson creditedPerson, Credit.Function function, Program creditedProgram) {
        Credit c = new Credit(creditedPerson, function);
        creditedProgram.addCredit(c);
    }

    //Create method for person when it's not stored in the DB(Text-files)
    public void createPerson(String name) {
        createPerson(UUID.randomUUID(), name);
    }

    //Create method when read from the DB(Text-file)
    public void createPerson(UUID uuid, String name) {
        CreditedPerson creditedPerson = new CreditedPerson(uuid, name);
        creditedPeople.add(creditedPerson);
    }

    //Export/save programs, persons & credits to txt
    public void exportToTxt() {
        //Strings to be written in the corresponding file
        String transmissionString = "";
        String episodeString = "";
        String CPString = "";
        String tvSeriesString = "";
        String creditStringTransmission = "";
        String creditStringEpisode = "";

        persistenceHandler.deleteFiles();

        //Loop through the programs array - if instance of transmission, we store that instance as a single line in the transmission-file
        for (Program p : programs) {
            if (p instanceof Transmission) {
                transmissionString = p.getUuid() + ";" + p.getName() + ";" + p.getDescription() + ";" + p.getCreatedBy() + ";" + p.getDuration() + ";" + p.isApproved();
                persistenceHandler.writeTransmission(transmissionString);
                //If the transmission has credits associated we store them in the credit-file
                if (p.getCredits() != null) {
                    for (Credit credit : p.getCredits()) {
                        creditStringTransmission = credit.getCreditedPerson().getUuid() + ";" + credit.getFunction().role + ";" + p.getUuid();
                        persistenceHandler.writeCredit(creditStringTransmission);
                    }
                }
                //If instance of Episode - store in the episode-file
            } else if (p instanceof Episode) {
                Episode e = (Episode) p;
                episodeString = e.getUuid() + ";" + getTvSeriesFromEpisode(e).getUuid() + ";" + e.getName() + ";" + e.getDescription() + ";" + e.getCreatedBy() + ";" + e.getEpisodeNo() + ";" + e.getSeasonNo() + ";" + e.getDuration() + ";" + e.isApproved();
                persistenceHandler.writeEpisode(episodeString);
                //If the episode has credits associated we store them in the credit-file
                if (e.getCredits() != null) {
                    for (Credit credit : e.getCredits()) {
                        creditStringEpisode = credit.getCreditedPerson().getUuid() + ";" + credit.getFunction().role + ";" + e.getUuid();
                        persistenceHandler.writeCredit(creditStringEpisode);
                    }
                }
            }
        }
        //Loop through every instance of a credited person and store it in the person-file
        for (CreditedPerson cp : creditedPeople) {
            CPString = cp.getUuid() + ";" + cp.getName();
            persistenceHandler.writePerson(CPString);
        }
        //Loop through every instance of a tv-series and store it in the person-file
        for (TVSeries tvSeries : tvSeriesList) {
            tvSeriesString = tvSeries.getUuid() + ";" + tvSeries.getName() + ";" + tvSeries.getDescription() + ";" + tvSeries.getCreatedBy();
            persistenceHandler.writeTvSeries(tvSeriesString);
        }
    }

    //Here we import the text-files and create a new instance of every line from each file
    public void importFromTxt() {
        //Read each file and for every string-array(Corresponding to one line) we call the create-method for the respective file
        ArrayList<String[]> transmissions = persistenceHandler.readTransmission();
        for (String[] transmissionLine : transmissions) {
            createTransmission(UUID.fromString(transmissionLine[0]), transmissionLine[1], transmissionLine[2],
                    Integer.parseInt(transmissionLine[3]), Integer.parseInt(transmissionLine[4]), Boolean.parseBoolean(transmissionLine[5]));
        }

        ArrayList<String[]> tvSeries = persistenceHandler.readTvSeries();
        for (String[] tvLine : tvSeries) {
            createTvSeries(UUID.fromString(tvLine[0]), tvLine[1], tvLine[2], Integer.parseInt(tvLine[3]));
        }

        ArrayList<String[]> episodes = persistenceHandler.readEpisode();
        for (String[] episodeLine : episodes) {
            createEpisode(UUID.fromString(episodeLine[0]), getTvSeriesFromUuid(UUID.fromString(episodeLine[1])),
                    episodeLine[2], episodeLine[3], Integer.parseInt(episodeLine[4]), Integer.parseInt(episodeLine[5]),
                    Integer.parseInt(episodeLine[6]), Integer.parseInt(episodeLine[7]), Boolean.parseBoolean(episodeLine[5]));
        }

        ArrayList<String[]> people = persistenceHandler.readPerson();
        for (String[] personLine : people) {
            createPerson(UUID.fromString(personLine[0]), personLine[1]);
        }

        ArrayList<String[]> credit = persistenceHandler.readCredit();
        for (String[] creditLine : credit) {
            createCredit(getPersonFromUuid(UUID.fromString(creditLine[0])), getFunction(creditLine[1]),
                    getProgramFromUuid(UUID.fromString(creditLine[2])));
        }
    }


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





    //Update the values of the selected transmission
    public void updateTransmission(Program program, String name, String description, int createdBy, int duration) {
        Transmission p = (Transmission) program;
        if (name != null) {
            p.setName(name);
        }
        if (description != null) {
            p.setDescription(description);
        }
        if (createdBy != -1) {
            p.setCreatedBy(createdBy);
        }
        if (duration != -1) {
            p.setDuration(duration);
        }
    }

    //Update the values of the selected episode
    public void updateEpisode(Program program, String name, String description, int createdBy, int duration, int seasonNo, int episodeNo, TVSeries tvSeries) {
        Episode p = (Episode) program;
        int oldSeasonNo = p.getSeasonNo();
        int oldEpisodeNo = p.getEpisodeNo();
        if (name != null) {
            p.setName(name);
        }
        if (description != null) {
            p.setDescription(description);
        }
        if (createdBy != -1) {
            p.setCreatedBy(createdBy);
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
    }

    //Update the values of the selected tv-series
    public void updateTvSeries(TVSeries tvSeries, String name, String description) {
        if (name != null) {
            tvSeries.setName(name);
        }
        if (description != null) {
            tvSeries.setDescription(description);
        }
    }

    public void updateCredit(Credit credit, Credit.Function function) {
        credit.setFunction(function);
    }

    public void updatePerson(CreditedPerson creditedPerson, String name) {
        creditedPerson.setName(name);
    }

    public void deleteCredit(Program program, Credit credit) {
        program.deleteCredit(credit);
    }

    public void deleteProgram(Program program) {
        programs.remove(program);
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
}
