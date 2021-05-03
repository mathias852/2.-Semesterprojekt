package domain.accesscontrol;

import java.util.ArrayList;
import java.util.List;

public class LoginHandler {

    private List<User> users;
    private User currentUser;

    public LoginHandler() {
        users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean createProducer(String username, int password) {
        for (User p : users) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        User producer = new Producer(username, password);
        users.add(producer);
        System.out.println("Added something to list: " + producer.getUsername());
        return true;
    }

    public boolean createSystemAdmin(String username, int password) {
        for (User p : users) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        User systemAdmin = new SystemAdmin(username, password);
        users.add(systemAdmin);
        System.out.println("Added something to list: " + systemAdmin.getUsername() + " with hashed password: " + systemAdmin.getHashedPassword());
        return true;
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

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }


}
