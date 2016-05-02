package com.mattkormann.tournamentmanager.tournaments;

import java.util.List;

/**
 * Created by Matt on 5/2/2016.
 */
public class SingleElimTournament implements Tournament {

    private int size;
    private List participants; //TODO add generic
    private List matches; //TODO change to array of Match class

    public SingleElimTournament() {

    }

    public SingleElimTournament(int size) {
        this.size = size; //TODO Change to reference other constructor
    }


    public SingleElimTournament(int size, List participants) {  //TODO add generic
        this.size = size;
        this.participants = participants;
    }

    public boolean isOver() {
        //return (matches[size - 2] != Tournament.NOT_YET_OCCURRED);
        return true; //TODO edit to return value of last match
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    @Override
    public List getParticipants() {
        return participants;
    }

    public void setParticipants(List participants) {
        this.participants = participants;
    }
}

