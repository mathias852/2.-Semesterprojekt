package domain;

import domain.accesscontrol.Producer;
import domain.accesscontrol.SystemAdmin;
import domain.accesscontrol.User;
import persistence.ExportHandler;
import persistence.PersistenceHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class LoginHandler {
    private static LoginHandler instance;
    private ArrayList<User> users = new ArrayList<>();
    private User currentUser;

    ExportHandler exportHandler = new ExportHandler();
    IPersistenceHandler persistenceHandler = PersistenceHandler.getInstance();

    public static LoginHandler getInstance() {
        if (instance == null) {
            instance = new LoginHandler();
        }
        return instance;
    }


    //Create method for producer when made in the application
    public boolean createProducer(String username, int password) {
        return createProducer(UUID.randomUUID(), username, password);
    }

    //Create method for system Admin when made in the application
    public boolean createSystemAdmin(String username, int password) {
        return createSystemAdmin(UUID.randomUUID(), username, password);
    }

    //Create method for producer when we have the UUID (Read from the DB)
    public boolean createProducer(UUID uuid, String username, int password) {
        User user = new Producer(uuid, username, password);
        persistenceHandler.storeProducer((Producer) user);
        return addUser(user);
    }

    //Create method for systemAdmin when we have the UUID (Read from the DB)
    public boolean createSystemAdmin(UUID uuid, String username, int password) {
        User user = new SystemAdmin(uuid, username, password);
        persistenceHandler.storeSystemAdmin((SystemAdmin) user);
        return addUser(user);
    }

    /*
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
     */

    public void importLogins(){
        users.addAll(persistenceHandler.getProducers());
        users.addAll(persistenceHandler.getSystemAdmins());
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
            users.add(user);
            return true;
        } else if (user instanceof Producer && !users.contains(user)) {
            users.add(user);
            return true;
        }
        return false;
    }

    // Tjekker om user-listen eksisterer, sletter givne user fra liste.
    public void deleteUser(User user) {
        if(users != null){
            users.remove(user);
            if (user instanceof SystemAdmin) {
                persistenceHandler.deleteSystemAdmin((SystemAdmin) user);
            } else if (user instanceof Producer) {
                persistenceHandler.deleteProducer((Producer) user);
            }
        }
    }

    public User verifyCredentials(String username, int password) {
        for (User p : users) {
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
