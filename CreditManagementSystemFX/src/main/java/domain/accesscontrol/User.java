package domain.accesscontrol;

import java.util.ArrayList;
import java.util.UUID;

public class User {

    private UUID uuid;
    private String username;
    private int hashedPassword;
    private ArrayList<User> users;


    public User(UUID uuid, String username, int password){
        this.uuid = uuid;
        this.username = username;
        this.hashedPassword = password;
    }


    public void addUser(User user){
        if (users == null) {
            users = new ArrayList<>();
        }
        if (user instanceof SystemAdmin && !users.contains(user)){
            users.add(user);
        } else if (user instanceof Producer && !users.contains(user)) {
            users.add(user);
        }
    }

    // Tjekker om userliste eksisterer, sletter givne user fra liste.
    public void deleteUser(User user) {
        if(users != null){
            users.remove(user);
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(int hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
