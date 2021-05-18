package domain.program;

import domain.accesscontrol.User;
import domain.credit.Credit;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Program {

    private UUID uuid, createdBy;
    private String name, description, production;

    private int duration;
    private ArrayList<Credit> credits;
    private boolean approved;

    public Program(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }
    // createdBy tager userID fra creator
    public Program(UUID uuid, String name, String description, UUID createdBy, int duration, boolean approved, String production) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.duration = duration;
        this.approved = approved;
        this.production = production;
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
    public UUID getCreatedBy() {
        return createdBy;
    }
    public int getDuration() {
        return duration;
    }
    public String getProduction(){
        return production;
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
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setApproved(boolean approved){
        this.approved = approved;
    }
    public void setProduction(String production) {
        this.production = production;
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

    public boolean isApproved() {
        return approved;
    }

}
