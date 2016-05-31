package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/6/2016.
 */
public class StandardMatch implements Match {

    private double[] statistics;
    private int winner = Match.NO_WINNER;
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

    public boolean checkParticipantNumIsValid(int participantNum) {
        if (participantNum < 0 || participantNum >= numParticipants)
            return false;
        return true;
    }

    @Override
    public void setWinner(int winner) {
        checkParticipantNumIsValid(winner);
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
    public int getParticipantIndex(int participantNum) {
        checkParticipantNumIsValid(participantNum);
        return participants[participantNum];
    }

    @Override
    public void setParticipants(int[] indices) {
        if (indices != null && indices.length == numParticipants) {
            this.participants = indices;
        }
    }

    @Override
    public void setParticipant(int participantNum, int index) {
        if (checkParticipantNumIsValid(participantNum)) participants[participantNum] = index;
    }
}
