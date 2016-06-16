package com.mattkormann.tournamentmanager.participants;

import com.mattkormann.tournamentmanager.tournaments.Tournament;

/**
 * Created by Matt on 5/3/2016.
 */
public class ParticipantFactory {

    public static Participant getParticipant(String type, String name, int ID) {
        switch (type) {
            case "single":
                return new SingleParticipant(name, ID);
            case "generic single":
                return new SingleParticipant("Participant " + (ID + Tournament.MAX_TOURNAMENT_SIZE + 1),
                        ID);
            case "team":
                return new TeamParticipant(name, ID);
            default:
                return new SingleParticipant(name, ID);
        }
    }
}
