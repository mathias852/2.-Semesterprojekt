package domain.accesscontrol;

import java.util.ArrayList;
import java.util.List;

public class LoginHandler {

    private List<Producer> producers;

    public LoginHandler(){
        producers = new ArrayList<>();
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public boolean createProducer(String username, int password) {
        for (Producer p : producers) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        Producer producer = new Producer(username, password);
        producers.add(producer);
        System.out.println("Added something to list: " + producer.getUsername());
        return true;
    }

    public boolean createSystemAdmin(String username, int password) {
        for (Producer p : producers) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        SystemAdmin systemAdmin = new SystemAdmin(username, password);
        producers.add(systemAdmin);
        System.out.println("Added something to list: " + systemAdmin.getUsername());
        return true;
    }

    public Producer verifyCredentials(String username, String password) {
        for (Producer p : producers) {
            if (p.getUsername().equalsIgnoreCase(username) && p.getHashedPassword() == password.hashCode()) {
                return p;
            }
        }
        return null;
    }
}
