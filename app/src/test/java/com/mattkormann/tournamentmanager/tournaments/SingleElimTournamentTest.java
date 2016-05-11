package com.mattkormann.tournamentmanager.tournaments;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Matt on 5/10/2016.
 */
public class SingleElimTournamentTest {

    private SingleElimTournament tournament;

    @Test
    public void testGetNumberOfRounds() {
        tournament = new SingleElimTournament(8);
        assertEquals(3, tournament.getNumberOfRounds());
        tournament = new SingleElimTournament(32);
        assertEquals(5, tournament.getNumberOfRounds());
        tournament = new SingleElimTournament(128);
        assertEquals(7, tournament.getNumberOfRounds());
    }

    @Test
    public void testGetRoundEndDelimiter() {
        tournament = new SingleElimTournament(128);
        assertEquals(126, tournament.getRoundEndDelimiter(tournament.getNumberOfRounds()));
        assertEquals(95, tournament.getRoundEndDelimiter(2));
        assertEquals(63, tournament.getRoundEndDelimiter(1));
    }

    @Test
    public void testGetRoundStartDelimiter() {
        tournament = new SingleElimTournament(61);
        assertEquals(59, tournament.getRoundStartDelimiter(tournament.getNumberOfRounds()));
        assertEquals(29, tournament.getRoundStartDelimiter(2));
        assertEquals(0, tournament.getRoundStartDelimiter(1));
    }
}
