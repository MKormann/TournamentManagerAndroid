package com.mattkormann.tournamentmanager.participants;

import com.mattkormann.tournamentmanager.tournaments.Match;

/**
 * Created by Matt on 5/2/2016.
 */
public class SingleParticipant implements Participant {

    private String name;
    private int ID;

    public SingleParticipant() {

    }

    public SingleParticipant(String name, int ID) { //TODO change constructor to look up name via ID
        this.name  = name;
        this.ID = ID;

    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

}
