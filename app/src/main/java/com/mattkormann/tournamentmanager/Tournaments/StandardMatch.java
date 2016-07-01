package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/6/2016.
 */
public class StandardMatch implements Match {

    private double[] statistics;
    private int winner = Match.NOT_YET_ASSIGNED;
    private int[] participants;
    private int nextMatchId;

    private final int numParticipants;

    public StandardMatch(int numParticipants) {
        this(numParticipants, 0);
    }

    public StandardMatch(int numParticipants, int numStats) {
        this.numParticipants = numParticipants;
        this.participants = new int[numParticipants];
        statistics = new double[numStats * numParticipants];
    }

    public boolean checkParticipantNumIsValid(int participantNum) {
        if (participantNum < 0 || participantNum >= numParticipants)
            return false;
        return true;
    }

    @Override
    public void setWinner(int winner) {
        this.winner = winner;
    }

    @Override
    public int getWinner() {
        return winner;
    }

    @Override
    public int getWinnerSeed() {
        if (winner == NOT_YET_ASSIGNED) return winner;
        else return participants[winner];
    }

    @Override
    public int getRunnerUpSeed() {
        if (getWinner() == Match.NOT_YET_ASSIGNED) return Match.NOT_YET_ASSIGNED;
        if (getWinner() == 0) return participants[1];
        else return participants[0]; //TODO change to reflect participant number
    }

    @Override
    public boolean hasWinner() {
        return (winner != NOT_YET_ASSIGNED);
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
    public int[] getParticipantSeeds() {
        return participants;
    }

    @Override
    public int getParticipantSeed(int participantNum) {
        checkParticipantNumIsValid(participantNum);
        return participants[participantNum];
    }

    @Override
    public void setParticipants(int[] seeds) {
        if (seeds != null && seeds.length == numParticipants) {
            this.participants = seeds;
        }
    }

    @Override
    public void setParticipant(int participantNum, int seed) {
        if (checkParticipantNumIsValid(participantNum)) participants[participantNum] = seed;
    }

    @Override
    public void setStatistics(double[] statistics) {
        this.statistics = statistics;
    }

    @Override
    public void setNextMatchId(int nextMatchId) {
        this.nextMatchId = nextMatchId;
    }

    @Override
    public int getNextMatchId() {
        return nextMatchId;
    }
}
