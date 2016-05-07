package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/6/2016.
 */
public class Match {

    private double[] statistics;
    private int winner = 0;
    private final int numParticipants;

    public Match(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }

    public double getSingleStatistic(int statIndex) {
        return statistics[statIndex];
    }

    public double[] getSingleStatisticForAll(int statIndex) {
        double[] statForAll = new double[numParticipants];
        int key = statistics.length / numParticipants;
        for (int i = 0; i < numParticipants; i++) {
            statForAll[i] = statistics[key * i + statIndex];
        }
        return statForAll;
    }

    public double[] getStatistics() {
        return statistics;
    }


}
