package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/10/2016.
 */
public interface Match {

    String MATCH_ID = "MATCH_ID";
    String MATCH_STATS = "MATCH_STATS";
    String PARTICIPANT_ONE = "PARTICIPANT_ONE";
    String PARTICIPANT_TWO = "PARTICIPANT_TWO";

    int NO_WINNER = -1;
    int NOT_YET_ASSIGNED = -2;

    int getWinner();
    void setWinner(int winner);
    int getRunnerUp();
    double[] getStatistics();
    double getSingleStatistic(int statIndex);
    double[] getSingleStatisticForAll(int statIndex);
    int[] getParticipantIndices();
    int getParticipantIndex(int participantNum);
    void setParticipants(int[] indices);
    void setParticipant(int participantNum, int index);
    void setStatistics(double[] statistics);
}
