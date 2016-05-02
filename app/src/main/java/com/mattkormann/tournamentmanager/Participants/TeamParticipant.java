package com.mattkormann.tournamentmanager.participants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 5/2/2016.
 */
public class TeamParticipant implements Participant {

    private String name;
    private int ID;
    private Map<Integer, String> members;

    public TeamParticipant() {
        this.members = new HashMap<>();
    }

    public TeamParticipant(String name, int ID) { //TODO change constructor to look up name, members via ID
        this.name = name;
        this.ID = ID;
        this.members = new HashMap<>();
    }

    @Override
     public String getName() {
        return name;
    }

    @Override
    public int getID() {
        return ID;
    }

    public Map<Integer, String> getMembers() {
        return members;
    }
}
