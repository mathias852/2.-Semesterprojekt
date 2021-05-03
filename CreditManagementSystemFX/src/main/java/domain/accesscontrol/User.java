package domain.accesscontrol;

import java.util.UUID;

public class User {

    private UUID uuid;
    private String username;
    private int hashedPassword;

    public User(String username, int password){
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.hashedPassword = password;
    }

    public User(UUID id){
        this.uuid = id;
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

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
