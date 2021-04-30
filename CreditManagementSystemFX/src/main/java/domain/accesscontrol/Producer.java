package domain.accesscontrol;

public class Producer extends User{
    private String username;
    private int hashedPassword;

    public Producer(String username, int password) {
        this.username = username;
        this.hashedPassword = password;
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
}
