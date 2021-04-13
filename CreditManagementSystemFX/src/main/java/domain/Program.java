package domain;

import java.util.ArrayList;
import java.util.Date;

public class Program {
    private String name, description;
    private Date period;
    private int eventID, createdBy;
    private long duration;
    private ArrayList<Credit> credits;

    public Program(String name) {
        this.name = name;
    }

    public Program(String name, String description, Date period, int eventID, int createdBy, long duration) {
        this.name = name;
        this.description = description;
        this.period = period;
        this.eventID = eventID;
        this.createdBy = createdBy;
        this.duration = duration;
    }

    public void addCredit(Credit c) {
        if (credits == null) {
            credits = new ArrayList<>();
        }
        credits.add(c);
    }

    public void deleteCredit(Credit c) {
        credits.remove(c);
    }


    @Override
    public String toString() {
        return name + " " + credits.toString();
    }
}
