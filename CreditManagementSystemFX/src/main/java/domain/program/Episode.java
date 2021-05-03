package domain.program;

import java.util.UUID;

public class Episode extends Program {

    private int episodeNo;
    private int seasonNo;
    private TVSeries tvSeries;

    public Episode(UUID uuid, TVSeries tvSeries, String name, String description, int createdBy, int episodeNo, int seasonNo, int duration, boolean approved) {
        super(uuid, name, description, createdBy, duration, approved);
        this.tvSeries = tvSeries;
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
    public TVSeries getTvSeries() {
        return tvSeries;
    }
    public void setTvSeries(TVSeries tvSeries) {
        this.tvSeries = tvSeries;
    }
}
