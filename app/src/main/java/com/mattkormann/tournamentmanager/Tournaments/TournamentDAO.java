package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.sql.DatabaseHelper;

/**
 * Created by Matt on 5/30/2016.
 */
public interface TournamentDAO {

    //Save all tournament details including stats, participants, winners
    void saveFullTournament(Tournament tournament);

    //Load a complete tournament
    Tournament loadFullTournament(int tournamentId);
}
