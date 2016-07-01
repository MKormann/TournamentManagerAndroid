package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 5/2/2016.
 */
public class SingleElimTournament extends Tournament {

    private final boolean IS_DOUBLE_ELIM = false;

    public SingleElimTournament() {

    }

    public SingleElimTournament(String name, int size, int teamSize) {
        this(name, size, teamSize, new String[] {}, new HashMap<Integer, Participant>());
    }


    public SingleElimTournament(String name, int size, int teamSize, String[] statCategories,
                                Map<Integer, Participant> participants) {
        super(name, size, teamSize, statCategories, participants);
    }

    @Override
    public boolean isDoubleElimination() {
        return IS_DOUBLE_ELIM;
    }

}

