package domain;

import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import domain.accesscontrol.User;
import persistence.PersistenceHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoginHandler {

    private ArrayList<User> users = new ArrayList<>();
    private User currentUser;

    PersistenceHandler persistenceHandler = new PersistenceHandler();


    //Create method for producer when made in the application
    public boolean createProducer(String userName, int password) {
        User user = new Producer(UUID.randomUUID(), userName, password);
        return addUser(user);
    }

    //Create method for system Admin when made in the application
    public boolean createSystemAdmin(String userName, int password) {
        User user = new SystemAdmin(UUID.randomUUID(), userName, password);
        return addUser(user);
    }

    //Create method for producer when we have the UUID (Read from the DB)
    public void createProducer(UUID uuid, String userName, int password) {
        User user = new Producer(uuid, userName, password);
        addUser(user);
    }

    //Create method for systemAdmin when we have the UUID (Read from the DB)
    public void createSystemAdmin(UUID uuid, String userName, int password) {
        User user = new SystemAdmin(uuid, userName, password);
        addUser(user);
    }

    public void exportUsersToTxt() throws IOException {
        String producer = "";
        String sysAdmin = "";

        persistenceHandler.fileWriter = new FileWriter(persistenceHandler.getProducerFile());
        persistenceHandler.fileWriter = new FileWriter(persistenceHandler.getSystemAdmin());

        for (User user : getUsers()) {
            if (user instanceof SystemAdmin) {
                sysAdmin = user.getUuid() + ";" + user.getUsername() + ";" + user.getHashedPassword();
                persistenceHandler.writeSystemAdmin(sysAdmin);
            } else if (user instanceof Producer) {
                producer = user.getUuid() + ";" + user.getUsername() + ";" + user.getHashedPassword();
                persistenceHandler.writeProducer(producer);
            }
        }
    }


    public void importLogins(){
        ArrayList<String[]> producers = persistenceHandler.readProducer();
        for(String[] s : producers){
            createProducer(UUID.fromString(s[0]), s[1], Integer.parseInt(s[2]));
        }

        ArrayList<String[]> sysAdmin = persistenceHandler.readSystemAdmin();
        for(String[] s : sysAdmin){
            createSystemAdmin(UUID.fromString(s[0]), s[1], Integer.parseInt(s[2]));
        }
    }


    public User getUserFromUuid(UUID uuid) {
        for (User user : getUsers()) {
            if (user.getUuid().equals(uuid)) {
                return user;
            }
        }
        return null;
    }

    public User getUserFromUsername(String string) {
        for (User user : getUsers()) {
            if (user.getUsername().equals(string)) {
                return user;
            }
        }
        return null;
    }


    public boolean addUser(User user){
        if (users == null) {
            users = new ArrayList<>();
        }
        if (user instanceof SystemAdmin && !users.contains(user)){
            System.out.println("Added a new admin to the list: " + user.getUsername());
            users.add(user);
            return true;
        } else if (user instanceof Producer && !users.contains(user)) {
            System.out.println("Added a new producer to the list: " + user.getUsername());
            users.add(user);
            return true;
        }
        return false;
    }

    // Tjekker om userliste eksisterer, sletter givne user fra liste.
    public void deleteUser(User user) {
        if(users != null){
            users.remove(user);
        }
    }

    public User verifyCredentials(String username, int password) {
        for (User p : users) {
            System.out.println("WE ARE IN LOOP");
            if (p.getUsername().equalsIgnoreCase(username) && p.getHashedPassword() == password) {
                this.currentUser = p;
                return p;
            }
        }
        return null;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
