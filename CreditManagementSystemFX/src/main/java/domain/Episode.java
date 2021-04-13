package domain;

import java.util.Date;

public class Episode extends Program {

    private int episodeNo;
    private int seasonNo;

    public Episode(String name, int seasonNo, int episodeNo) {
        super(name);
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
    }

    public Episode(String name, String description, Date periodStart, Date periodEnd, int eventID, int createdBy, int episodeNo, int seasonNo) {
        super(name, description, periodStart, periodEnd, eventID, createdBy);
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
    }

    public int getEpisodeNo() {
        return episodeNo;
    }

    public void setEpisodeNo(int episodeNo) {
        this.episodeNo = episodeNo;
    }

    public int getSeasonNo() {
        return seasonNo;
    }

    public void setSeasonNo(int seasonNo) {
        this.seasonNo = seasonNo;
    }




}
