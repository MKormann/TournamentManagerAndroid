package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;

/**
 * Created by Matt on 5/6/2016.
 */
public class StandardMatch implements Match {

    private double[] statistics;
    private int winner = -1;
    private int[] participants;

    private final int numParticipants;

    public StandardMatch(int numParticipants) {
        this(numParticipants, 0);
    }

    public StandardMatch(int numParticipants, int numStats) {
        this.numParticipants = numParticipants;
        this.participants = new int[numParticipants];
        statistics = new double[numStats];
    }

    public boolean checkIndexIsValid(int index) {
        if (index < 0 || index >= numParticipants)
            return false;
        return true;
    }

    public void setWinner(int winner) {
        checkIndexIsValid(winner);
        this.winner = winner;
    }

    @Override
    public int getWinner() {
        return participants[winner];
    }

    //Get one single stat for one participant
    @Override
    public double getSingleStatistic(int statIndex) {
        return statistics[statIndex];
    }

    //Returns an array of doubles corresponding to the same stat for each participant
    @Override
    public double[] getSingleStatisticForAll(int statIndex) {
        double[] statForAll = new double[numParticipants];
        int key = statistics.length / numParticipants;
        for (int i = 0; i < numParticipants; i++) {
            statForAll[i] = statistics[key * i + statIndex];
        }
        return statForAll;
    }

    //Get entire statistics array
    @Override
    public double[] getStatistics() {
        return statistics;
    }

    @Override
    public int[] getParticipantIndices() {
        return participants;
    }

    @Override
    public int getParticipantIndex(int index) {
        checkIndexIsValid(index);
        return participants[index];
    }
}
