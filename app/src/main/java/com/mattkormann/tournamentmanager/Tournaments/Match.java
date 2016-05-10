package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/10/2016.
 */
public interface Match {

    int getWinner();
    double[] getStatistics();
    double getSingleStatistic(int statIndex);
    double[] getSingleStatisticForAll(int statIndex);
}
