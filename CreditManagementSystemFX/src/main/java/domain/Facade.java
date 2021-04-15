package domain;

import domain.credit.Credit;
import domain.credit.CreditedPerson;
import domain.program.Episode;
import domain.program.Program;
import domain.program.TVSeries;
import domain.program.Transmission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Facade {
    private List<Program> programs = new ArrayList<>();
    private List<TVSeries> tvSeriesList = new ArrayList<>();
    private List<CreditedPerson> creditedPeople = new ArrayList<>();

    public static void main(String[] args) {

        TVSeries badehotellet = new TVSeries("Badehotellet", UUID.randomUUID());

        Facade facade = new Facade();
        Episode ep1 = facade.createEpisode(badehotellet,"Badehotellet 1","Badehotellet the sequel", 1, 1, 1, 1, 60);
        Episode ep2 = facade.createEpisode(badehotellet, "Badehotellet 2", "Badehotellet the sequel", 1, 1, 2, 1, 60);
        CreditedPerson cp = facade.createPerson("Jens Jensen");
        CreditedPerson cp2 = facade.createPerson("Hans Hansen");
        Credit c = facade.createCredit(ep1, cp, Credit.Function.VISUALARTIST);
        Credit c2 = facade.createCredit(ep1, cp2, Credit.Function.EDITOR);
    }

    public Episode createEpisode(TVSeries tv, String name, String description, int eventID, int createdBy, int episodeNo, int seasonNo, int duration) {
        Program program = new Episode(UUID.randomUUID(), name, description, eventID, createdBy, episodeNo, seasonNo, duration);
        tv.addEpisode((Episode) program);
        programs.add(program);
        return (Episode) program;
    }

    public Transmission createTransmission(String name, String description, int eventID, int createdBy, int duration) {
        Program program = new Transmission(UUID.randomUUID(), name, description, eventID, createdBy, duration);
        programs.add(program);
        return (Transmission) program;
    }

    public TVSeries createTvSeries(String name) {
        TVSeries tvSeries = new TVSeries(name, UUID.randomUUID());
        tvSeriesList.add(tvSeries);
        return tvSeries;
    }

    public Credit createCredit(Program creditedProgram, CreditedPerson creditedPerson, Credit.Function function) {
        Credit c = new Credit(creditedPerson, function);
        creditedProgram.addCredit(c);
        return c;
    }

    public CreditedPerson createPerson(String name) {
        CreditedPerson creditedPerson = new CreditedPerson(name, UUID.randomUUID());
        creditedPeople.add(creditedPerson);
        return creditedPerson;
    }

    public void updateProgram(Program p, String name, String description, int eventId, int createdBy, int duration) {
        if (name != null) {
            p.setName(name);
        }
        if (description != null) {
            p.setDescription(description);
        }
        if (eventId != -1) {
            p.setEventID(eventId);
        }
        if (createdBy != -1) {
            p.setEventID(eventId);
        }
        if (duration != -1) {
            p.setEventID(eventId);
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
