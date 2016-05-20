package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;

import java.util.List;

/**
 * Created by Matt on 5/2/2016.
 */
public interface Tournament {

    int NOT_YET_OCCURRED = -2;
    int MIN_TOURNAMENT_SIZE = 4;
    int MAX_TOURNAMENT_SIZE = 256;

    //Return size (number of participants) of tournament
    int getSize();

    //Return size of teams
    int getTeamSize();

    //Return list of Participants
    Participant[] getParticipants();

    //Return single participant
    Participant getParticipant(int index);

    //Return array of matches
    Match[] getMatches();

    //Return whether or not the tournament has concluded
    boolean isOver();

    //Return whether tournament is double or single elimination
    boolean isDoubleElimination();

    //Return the number of rounds in given tournament
    //e.g. in a single elimination tournament, 5-8 participants = 3 rounds, 9-16 = 4 rounds, etc.
    int getNumberOfRounds();

    //Returns array marker value for when a given round begins, inclusive
    int getRoundStartDelimiter(int roundNumber);

    //Returns array marker value for when a given round ends, inclusive
    int getRoundEndDelimiter(int roundNumber);

    //Returns id of the match that the winner of the match given advances to
    int getNextMatchId(int matchId);

    boolean isStatTrackingEnabled();

    String[] getStatCategories() ;

    int getNumberOfStatistics();
}
