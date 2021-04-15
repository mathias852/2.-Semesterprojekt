package domain.program;

import domain.credit.Credit;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Program {

    private UUID uuid;
    private String name, description;
    private int eventID, createdBy;
    private int duration;
    private ArrayList<Credit> credits;

    public Program(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    public Program(UUID uuid, String name, String description, int eventID, int createdBy, int duration) { // createdBy tager userID fra creator
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.eventID = eventID;
        this.createdBy = createdBy;
        this.duration = duration;
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

    public UUID getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public int getEventID() {
        return eventID;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<Credit> getCredits() {
        return credits;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        String result = "\nName: " + name + "\nDescription: " + description + "\nDuration: " + duration + "\n";
        if (credits != null) {
            result += "Credits: " + credits.toString();
        }
        return result;
    }


    public String getName() {
        return name;
    }
}
