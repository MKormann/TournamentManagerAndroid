package com.mattkormann.tournamentmanager.tournaments;

/**
 * Created by Matt on 5/30/2016.
 */
public interface TournamentDAO {

    int NEW_TOURNAMENT_TEMPLATE = -9;
    int NOT_YET_SAVED = -7;

    //Save all tournament details including stats, participants, winners
    int saveFullTournament(Tournament tournament);

    //Load a complete tournament
    Tournament loadFullTournament(int tournamentId);

    //Create a new tournament using the template of the tournament from the provided id
    Tournament loadTournamentFromTemplate(int templateId);

    //Save a tournament template
    int saveTournamentTemplate(int tournamentId, String name, int size, int teamSize, int doubleElim,
                                int useStats, String[] statCategories);
}
