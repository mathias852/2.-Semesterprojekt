package domain;

import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;
import persistence.ExportHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Facade {
    private List<Program> programs = new ArrayList<>();
    private List<TVSeries> tvSeriesList = new ArrayList<>();
    private List<CreditedPerson> creditedPeople = new ArrayList<>();

    ExportHandler exportHandler = new ExportHandler();


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
    }

    //Create method for transmission when it's not stored in the DB(Text-files)
    public void createTransmission(String name, String description, UUID createdBy, int duration, boolean approved, String production) {
        createTransmission(UUID.randomUUID(), name, description, createdBy, duration, approved, production);
    }

    //Create method when read from the DB(Text-file)
    public void createTransmission(UUID uuid, String name, String description, UUID createdBy, int duration, boolean approved, String production) {
        Program program = new Transmission(uuid, name, description, createdBy, duration, approved, production);
        programs.add(program);
    }

    //Create method for tv-series when it's not stored in the DB(Text-files)
    public void createTvSeries(String name, String description, UUID createdBy) {
        createTvSeries(UUID.randomUUID(), name, description, createdBy);
    }

    //Create method when read from the DB(Text-file)
    public void createTvSeries(UUID uuid, String name, String description, UUID createdBy) {
        TVSeries tvSeries = new TVSeries(uuid, name, description, createdBy);
        tvSeriesList.add(tvSeries);
    }

    //Create method when read from the DB(Text-file) **Note: We do not store a UUID which is why we don't have two create-methods for credits
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
    public void exportToTxt() throws IOException {
        //Strings to be written in the corresponding file
        String transmissionString;
        String episodeString;
        String CPString;
        String tvSeriesString;
        String creditStringTransmission;
        String creditStringEpisode;

/*        exportHandler.writeTransmission("");
        exportHandler.writeEpisode("");
        exportHandler.writeCredit("");
        exportHandler.writePerson("");
        exportHandler.writeTvSeries("");*/

        exportHandler.fileWriter = new FileWriter(exportHandler.getCreditFile());
        exportHandler.fileWriter = new FileWriter(exportHandler.getTransmission());
        exportHandler.fileWriter = new FileWriter(exportHandler.getEpisode());
        exportHandler.fileWriter = new FileWriter(exportHandler.getTvSeries());
        exportHandler.fileWriter = new FileWriter(exportHandler.getPerson());

        //Loop through the programs array - if instance of transmission, we store that instance as a single line in the transmission-file
        for (Program p : programs) {
            if (p instanceof Transmission) {
                transmissionString = p.getUuid() + ";" + p.getName() + ";" + p.getDescription() + ";" + p.getCreatedBy() + ";" + p.getDuration() + ";" + p.isApproved() + ";" + p.getProduction();
                exportHandler.writeTransmission(transmissionString);
                //If the transmission has credits associated we store them in the credit-file
                if (p.getCredits() != null) {
                    for (Credit credit : p.getCredits()) {
                        creditStringTransmission = credit.getCreditedPerson().getUuid() + ";" + credit.getFunction().role + ";" + p.getUuid();
                        exportHandler.writeCredit(creditStringTransmission);
                    }
                }
                //If instance of Episode - store in the episode-file
            } else if (p instanceof Episode) {
                Episode e = (Episode) p;
                episodeString = e.getUuid() + ";" + getTvSeriesFromEpisode(e).getUuid() + ";" + e.getName() + ";" + e.getDescription() + ";" + e.getCreatedBy() + ";" + e.getEpisodeNo() + ";" + e.getSeasonNo() + ";" + e.getDuration() + ";" + e.isApproved() + ";" + p.getProduction();
                exportHandler.writeEpisode(episodeString);
                //If the episode has credits associated we store them in the credit-file
                if (e.getCredits() != null) {
                    for (Credit credit : e.getCredits()) {
                        creditStringEpisode = credit.getCreditedPerson().getUuid() + ";" + credit.getFunction().role + ";" + e.getUuid();
                        exportHandler.writeCredit(creditStringEpisode);
                    }
                }
            }
        }
        //Loop through every instance of a credited person and store it in the person-file
        for (CreditedPerson cp : creditedPeople) {
            CPString = cp.getUuid() + ";" + cp.getName();
            exportHandler.writePerson(CPString);
        }
        //Loop through every instance of a tv-series and store it in the person-file
        for (TVSeries tvSeries : tvSeriesList) {
            tvSeriesString = tvSeries.getUuid() + ";" + tvSeries.getName() + ";" + tvSeries.getDescription() + ";" + tvSeries.getCreatedBy();
            exportHandler.writeTvSeries(tvSeriesString);
        }
    }

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


    //Update the values of the selected transmission
    public void updateTransmission(Program program, String name, String description, int duration, String production) {
        Transmission p = (Transmission) program;
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
    }

    //Update the values of the selected episode
    public void updateEpisode(Program program, String name, String description, int duration, int seasonNo, int episodeNo, TVSeries tvSeries, String production) {
        Episode p = (Episode) program;
        int oldSeasonNo = p.getSeasonNo();
        int oldEpisodeNo = p.getEpisodeNo();
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
