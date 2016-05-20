package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/10/2016.
 */
public interface Match {

    int NO_WINNER = -1;
    int NOT_YET_ASSIGNED = -2;

    int getWinner();
    double[] getStatistics();
    double getSingleStatistic(int statIndex);
    double[] getSingleStatisticForAll(int statIndex);
    int[] getParticipantIndices();
    int getParticipantIndex(int participantNum);
    void setParticipants(int[] indices);
    void setParticipant(int participantNum, int index);
}
