package domain.program;

import java.util.*;

public class TVSeries {

    private Map<Integer, ArrayList<Episode>> seasonMap;
    private String name;
    private String description;
    private int createdBy;
    private UUID uuid;

    public TVSeries(UUID uuid, String name, String description, int createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.uuid = uuid;
    }

    public void addEpisode(Episode e){
        if(seasonMap == null){
            seasonMap = new TreeMap<>();
        }
        if(seasonMap.get(e.getSeasonNo()) == null){
            seasonMap.put(e.getSeasonNo(), new ArrayList<Episode>());
        }
        seasonMap.get(e.getSeasonNo()).add(e);
        System.out.println("added episode: " + e.getEpisodeNo() + ", " + e.getName() + " to season: " + e.getSeasonNo());
    }
    public UUID getUuid() {
        return uuid;
    }
    public Map<Integer, ArrayList<Episode>> getSeasonMap() {
        return seasonMap;
    }
    public void setSeasonMap(Map<Integer, ArrayList<Episode>> seasonMap) {
        this.seasonMap = seasonMap;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
