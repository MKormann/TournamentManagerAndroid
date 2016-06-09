package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;

/**
 * Created by Matt on 5/2/2016.
 */
public class SingleElimTournament extends Tournament {

    private final boolean IS_DOUBLE_ELIM = false;

    public SingleElimTournament() {

    }

    public SingleElimTournament(String name, int size, int teamSize) {
        this(name, size, teamSize, new String[] {}, new Participant[size]);
    }


    public SingleElimTournament(String name, int size, int teamSize, String[] statCategories,
                                Participant[] participants) {
        super(name, size, teamSize, statCategories, participants);
    }

    @Override
    public boolean isDoubleElimination() {
        return IS_DOUBLE_ELIM;
    }

}

