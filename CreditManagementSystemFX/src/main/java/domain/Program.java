package domain;

import java.util.ArrayList;
import java.util.Date;

public abstract class Program {


    private String name, description;
    private Date periodStart;
    private Date periodEnd;
    private int eventID, createdBy;
    private long duration;
    private ArrayList<Credit> credits;

    public Program(String name) {
        this.name = name;
    }

    public Program(String name, String description, Date periodStart, Date periodEnd, int eventID, int createdBy) { // createdBy tager userID fra creator
        this.name = name;
        this.description = description;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.eventID = eventID;
        this.createdBy = createdBy;
        this.duration = periodEnd.getTime() - periodStart.getTime();
    }

    // Tjekker om creditsliste eksisterer, tilføjer givne credit til liste.
    public void addCredit(Credit c) {
        if (credits == null) {
            credits = new ArrayList<>();
        }
        credits.add(c);
    }

    // Tjekker om creditsliste eksisterer, sletter givne credit fra liste.
    public void deleteCredit(Credit c) {
        if(credits != null){
            credits.remove(c);
        }
    }


    @Override
    public String toString() {
        return name + " " + credits.toString();
    }


    public String getName() {
        return name;
    }
}
