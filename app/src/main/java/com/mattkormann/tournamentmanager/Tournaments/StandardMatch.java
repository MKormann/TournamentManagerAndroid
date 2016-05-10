package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/6/2016.
 */
public class StandardMatch implements Match {

    private double[] statistics;
    private int winner = 0;
    private final int numParticipants;

    public StandardMatch(int numParticipants) {
        this(numParticipants, 0);
    }

    public StandardMatch(int numParticipants, int numStats) {
        this.numParticipants = numParticipants;
        statistics = new double[numStats];
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }

    //Get one single stat for one participant
    public double getSingleStatistic(int statIndex) {
        return statistics[statIndex];
    }

    //Returns an array of doubles corresponding to the same stat for each participant
    public double[] getSingleStatisticForAll(int statIndex) {
        double[] statForAll = new double[numParticipants];
        int key = statistics.length / numParticipants;
        for (int i = 0; i < numParticipants; i++) {
            statForAll[i] = statistics[key * i + statIndex];
        }
        return statForAll;
    }

    //Get entire statistics array
    public double[] getStatistics() {
        return statistics;
    }


}
