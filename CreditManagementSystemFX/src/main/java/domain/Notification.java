package domain;

public class Notification {
    private String title;
    private Boolean seen;

    public Notification(String title) {
        this.title = title;
        this.seen = false;
    }
    public String getTitle() {
        return title;
    }
    public Boolean getSeen() {
        return seen;
    }
    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
