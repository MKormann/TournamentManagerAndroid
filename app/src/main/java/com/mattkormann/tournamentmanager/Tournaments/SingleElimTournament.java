package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;

import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * Created by Matt on 5/2/2016.
 */
public class SingleElimTournament implements Tournament {

    private int size;
    private int teamSize;
    private int rounds;
    private Participant[] participants;
    private Match[] matches;
    private String[] statCategories;

    private final boolean IS_DOUBLE_ELIM = false;

    public SingleElimTournament() {

    }

    public SingleElimTournament(int size, int teamSize) {
        this(size, teamSize, new String[] {}, new Participant[size]);
    }


    public SingleElimTournament(int size, int teamSize, String[] statCategories,
                                Participant[] participants) {
        if (size < MIN_TOURNAMENT_SIZE)
            throw new IllegalArgumentException("Tournament is less than minimum allowed size.");
        this.size = size;
        this.teamSize = teamSize;
        this.rounds = Integer.toBinaryString(size - 1).length();
        this.participants = participants;
        this.matches = new Match[size - 1];
        this.statCategories = statCategories;
    }

    //Compares the value of the last Match in the match array to the static Tournament value
    //for not having occurred.
    @Override
    public boolean isOver() {
        return (matches[size - 2].getWinner() != Tournament.NOT_YET_OCCURRED);
    }

    @Override
    public boolean isDoubleElimination() {
        return IS_DOUBLE_ELIM;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public Participant[] getParticipants() {
        return participants;
    }

    @Override
    public int getNumberOfRounds() {
        return rounds;
    }

    @Override
    public int getRoundStartDelimiter(int roundNumber) {
        if (roundNumber == 1) return 0;
        else return (getRoundEndDelimiter(roundNumber - 1) + 1);
    }

    @Override
    public int getRoundEndDelimiter(int roundNumber) {
        if (roundNumber < 1 || roundNumber > rounds) {
            throw new IllegalArgumentException("Round number is invalid: " + roundNumber);
        }
        return (int)(matches.length - Math.pow(2, (rounds - roundNumber)));
    }

    @Override
    public boolean isStatTrackingEnabled(){
        return (statCategories.length != 0);
    }

    @Override
    public String[] getStatCategories() {
        return statCategories;
    }

    @Override
    public int getNumberOfStatistics() {
        return statCategories.length;
    }
}

