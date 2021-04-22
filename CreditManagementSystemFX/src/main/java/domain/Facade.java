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
//        TVSeries badehotellet = new TVSeries("Badehotellet", "En serie", 1, UUID.randomUUID());
//        Facade facade = new Facade();
//        Episode ep1 = facade.createEpisode(badehotellet,"Badehotellet 1","Badehotellet the sequel", 1, 1, 1, 60);
//        Episode ep2 = facade.createEpisode(badehotellet, "Badehotellet 2", "Badehotellet the sequel", 1, 2, 1, 60);
//        CreditedPerson cp = facade.createPerson("Jens Jensen");
//        CreditedPerson cp2 = facade.createPerson("Hans Hansen");
//        Credit c = facade.createCredit(ep1, cp, Credit.Function.VISUALARTIST);
//        Credit c2 = facade.createCredit(ep1, cp2, Credit.Function.EDITOR);

    }


    public void createStuff() {
        TVSeries badehotellet = createTvSeries("Badehotellet", "En serie", 1);
        Episode ep1 = createEpisode(badehotellet, "Badehotellet 1", "Badehotellet the sequel", 1, 1, 1, 60);
        Episode ep2 = createEpisode(badehotellet, "Badehotellet 2", "Badehotellet the sequel", 1, 2, 1, 60);
        CreditedPerson cp = createPerson("Jens Jensen");
        CreditedPerson cp2 = createPerson("Hans Hansen");
        Credit c = createCredit(ep1, cp, Credit.Function.VISUALARTIST);
        Credit c2 = createCredit(ep1, cp2, Credit.Function.EDITOR);
    }

    public Episode createEpisode(TVSeries tv, String name, String description, int createdBy, int episodeNo, int seasonNo, int duration) {
        return createEpisode(UUID.randomUUID(), tv, name, description, createdBy, episodeNo, seasonNo, duration);
    }

    public Episode createEpisode(UUID uuid, TVSeries tv, String name, String description, int createdBy, int episodeNo, int seasonNo, int duration) {
        Program program = new Episode(UUID.randomUUID(), name, description, createdBy, episodeNo, seasonNo, duration);
        tv.addEpisode((Episode) program);
        programs.add(program);
        return (Episode) program;
    }

    public Transmission createTransmission(String name, String description, int createdBy, int duration) {
        return createTransmission(UUID.randomUUID(), name, description, createdBy, duration);
    }

    public Transmission createTransmission(UUID uuid, String name, String description, int createdBy, int duration) {
        Program program = new Transmission(uuid, name, description, createdBy, duration);
        programs.add(program);
        return (Transmission) program;
    }

    public TVSeries createTvSeries(String name, String description, int createdBy) {
        return createTvSeries(UUID.randomUUID(), name, description, createdBy);
    }

    public TVSeries createTvSeries(UUID uuid, String name, String description, int createdBy) {
        TVSeries tvSeries = new TVSeries(uuid, name, description, createdBy);
        tvSeriesList.add(tvSeries);
        return tvSeries;
    }

    public Credit createCredit(Program creditedProgram, CreditedPerson creditedPerson, Credit.Function function) {
        return createCredit(UUID.randomUUID(), creditedProgram, creditedPerson, function);
    }

    public Credit createCredit(UUID uuid, Program creditedProgram, CreditedPerson creditedPerson, Credit.Function function) {
        Credit c = new Credit(creditedPerson, function);
        creditedProgram.addCredit(c);
        return c;
    }

    public CreditedPerson createPerson(String name) {
        return createPerson(UUID.randomUUID(), name);
    }

    public CreditedPerson createPerson(UUID uuid, String name) {
        CreditedPerson creditedPerson = new CreditedPerson(name, UUID.randomUUID());
        creditedPeople.add(creditedPerson);
        return creditedPerson;
    }

    public void updateProgram(Program p, String name, String description, int createdBy, int duration) {
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

    public void exportToTxt() {
        //Strings to be written in the corresponding file
        String transmissionString = "";
        String episodeString = "";
        String CPString = "";
        String tvSeriesString = "";
        String creditStringTransmission = "";
        String creditStringEpisode = "";

        persistenceHandler.deleteFiles();

        for (Program p : programs) {
            if (p instanceof Transmission) {
                transmissionString = p.getUuid() + ";" + p.getName() + ";" + p.getDescription() + ";" + p.getCreatedBy() + ";" + p.getDuration();
                persistenceHandler.writeTransmission(transmissionString);
                if (p.getCredits() != null) {
                    for (Credit credit : p.getCredits()) {
                        creditStringTransmission = credit.getCreditedPerson().getUuid() + ";" + credit.getFunction().role + ";" + p.getUuid();
                        persistenceHandler.writeCredit(creditStringTransmission);
                    }
                }

            } else if (p instanceof Episode) {
                Episode e = (Episode) p;
                episodeString = e.getUuid() + ";" + getTvSeriesFromEpisode(e).getUuid() + ";" + e.getName() + ";" + e.getDescription() + ";" + e.getCreatedBy() + ";" + e.getEpisodeNo() + ";" + e.getSeasonNo() + ";" + e.getDuration();
                persistenceHandler.writeEpisode(episodeString);

                if (e.getCredits() != null) {
                    for (Credit credit : e.getCredits()) {
                        creditStringEpisode = credit.getCreditedPerson().getUuid() + ";" + credit.getFunction().role + ";" + e.getUuid();
                        persistenceHandler.writeCredit(creditStringEpisode);
                    }
                }
            }
        }
        for (CreditedPerson cp : creditedPeople) {
            CPString = cp.getUuid() + ";" + cp.getName();
            persistenceHandler.writePerson(CPString);
        }
        for (TVSeries tvSeries : tvSeriesList) {
            tvSeriesString = tvSeries.getUuid() + ";" + tvSeries.getName() + ";" + tvSeries.getDescription() + ";" + tvSeries.getCreatedBy();
            persistenceHandler.writeTvSeries(tvSeriesString);
        }
    }

    //Here we import the text-files and create a new instance of every line from each file
    public void importFromTxt() {
        ArrayList<String[]> transmissions = persistenceHandler.readTransmission();
        for (String[] transmissionLine : transmissions) {
            createTransmission(UUID.fromString(transmissionLine[0]), transmissionLine[1], transmissionLine[2], Integer.parseInt(transmissionLine[3]), Integer.parseInt(transmissionLine[4]));
        }

        ArrayList<String[]> episodes = persistenceHandler.readEpisode();
        for (String[] episodeLine : episodes) {
            createEpisode(UUID.fromString(episodeLine[0]), getTvSeriesFromUuid(UUID.fromString(episodeLine[1])), episodeLine[2], episodeLine[3], Integer.parseInt(episodeLine[4]), Integer.parseInt(episodeLine[5]), Integer.parseInt(episodeLine[6]), Integer.parseInt(episodeLine[7]));
        }

        ArrayList<String[]> tvSeries = persistenceHandler.readTvSeries();
        for (String[] tvLine : tvSeries) {
            createTvSeries(UUID.fromString(tvLine[0]), tvLine[1], tvLine[2], Integer.parseInt(tvLine[3]));
        }

        ArrayList<String[]> people = persistenceHandler.readPerson();
        for (String[] personLine : people) {
            createPerson(UUID.fromString(personLine[0]), personLine[1]);
        }

        ArrayList<String[]> credit = persistenceHandler.readCredit();
        for (String[] creditLine : credit) {
            createCredit(UUID.fromString(creditLine[0]), getProgramFromUuid(UUID.fromString(creditLine[1])), getPersonFromUuid(UUID.fromString(creditLine[2])), Credit.Function.valueOf(creditLine[3]));
        }
    }

    //iterates through the tv series in facade and returns the tv series that the episode parameter is in
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

    public List<Credit.Function> getFunctions() {
        return Arrays.asList(Credit.Function.values());
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
