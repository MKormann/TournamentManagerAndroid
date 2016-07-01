package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.participants.SingleParticipant;

/**
 * Created by Matt on 5/10/2016.
 */
public interface Match {

    String MATCH_ID = "MATCH_ID";
    String MATCH_STATS = "MATCH_STATS";
    String PARTICIPANT_ONE = "PARTICIPANT_ONE";
    String PARTICIPANT_TWO = "PARTICIPANT_TWO";
    String MATCH_WINNER = "MATCH_WINNER";

    Participant UNASSIGNED_PARTICIPANT = new SingleParticipant("_____", Match.NOT_YET_ASSIGNED);
    Participant BYE_PARTICIPANT = new SingleParticipant("BYE", Match.BYE);

    int NOT_YET_ASSIGNED = -2;
    int BYE = -3;

    int getWinner();
    void setWinner(int winner);
    int getWinnerSeed();
    int getRunnerUpSeed();
    boolean hasWinner();
    double[] getStatistics();
    double getSingleStatistic(int statIndex);
    double[] getSingleStatisticForAll(int statIndex);
    int[] getParticipantSeeds();
    int getParticipantSeed(int participantNum);
    void setParticipants(int[] seeds);
    void setParticipant(int participantNum, int seed);
    void setStatistics(double[] statistics);
    void setNextMatchId(int nextMatchId);
    int getNextMatchId();
}
