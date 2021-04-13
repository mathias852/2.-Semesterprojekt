package domain;

import java.util.*;

public class TVSeries {

    private Map<Integer, ArrayList<Episode>> seasonMap;
    private String name;



    public TVSeries(String name) {
        this.name = name;
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
}
