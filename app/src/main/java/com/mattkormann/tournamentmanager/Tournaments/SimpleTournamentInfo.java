package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 6/13/2016.
 */
public class SimpleTournamentInfo {

    private int id;
    private String name;
    private int size;
    private String date;

    public SimpleTournamentInfo(int id, String name, int size, String date) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }
}
