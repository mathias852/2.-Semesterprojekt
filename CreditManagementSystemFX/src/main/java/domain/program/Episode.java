package domain.program;

import java.util.UUID;

public class Episode extends Program {

    private int episodeNo;
    private int seasonNo;

    //Umiddelbart antages der, at episode ikke kan instantieres uden alle parametre
    /*public Episode(String name, int seasonNo, int episodeNo) {
        super(name);
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
    }*/

    public Episode(UUID uuid, String name, String description, int createdBy, int episodeNo, int seasonNo, int duration) {
        super(uuid, name, description, createdBy, duration);
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