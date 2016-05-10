package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/9/2016.
 */
public class MatchFactory {

    public static StandardMatch getMatch(int numParticipants) {
        return new StandardMatch(numParticipants);
    }

    public static StandardMatch getMatch(int numParticipants, int numStats) {
        return new StandardMatch(numParticipants, numStats);
    }

}
