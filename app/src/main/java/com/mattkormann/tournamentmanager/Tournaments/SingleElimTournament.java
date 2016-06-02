package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.participants.Participant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Matt on 5/2/2016.
 */
public class SingleElimTournament extends Tournament {

    private String name;
    private int size;
    private int teamSize;
    private int rounds;
    private Participant[] participants;
    private Match[] matches;
    private String[] statCategories;
    private String endTime;

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

}

