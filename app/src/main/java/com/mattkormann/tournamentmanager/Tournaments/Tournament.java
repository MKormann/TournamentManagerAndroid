package com.mattkormann.tournamentmanager.tournaments;

import java.util.List;

/**
 * Created by Matt on 5/2/2016.
 */
public interface Tournament {

    int NOT_YET_OCCURRED = -2;

    int getSize();
    List getParticipants(); // TODO add generic when Participant class created
    boolean isOver();
}
