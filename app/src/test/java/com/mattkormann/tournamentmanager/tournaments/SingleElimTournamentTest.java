package com.mattkormann.tournamentmanager.tournaments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Matt on 5/10/2016.
 */
@RunWith(Parameterized.class)
public class SingleElimTournamentTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {8, 3, 2, 4, 5}, {128, 7, 4, 112, 119}, {61, 6, 2, 29, 44}, {250, 8, 8, 248, 248}
        });
    }

    private SingleElimTournament tournament;
    private int roundToCheck;
    private int expectedRounds;
    private int expectedStartDelim;
    private int expectedEndDelim;

    public SingleElimTournamentTest(int size, int rounds, int check, int startDelim, int endDelim) {
        tournament = new SingleElimTournament(size, 1);
        expectedRounds = rounds;
        roundToCheck = check;
        expectedStartDelim = startDelim;
        expectedEndDelim = endDelim;
    }

    @Test
    public void testGetNumberOfRounds() {
        assertEquals(expectedRounds, tournament.getNumberOfRounds());
    }

    @Test
    public void testGetRoundStartDelimiter() {
        assertEquals(expectedStartDelim, tournament.getRoundStartDelimiter(roundToCheck));
    }

    @Test
    public void testGetRoundEndDelimiter() {
        assertEquals(expectedEndDelim, tournament.getRoundEndDelimiter(roundToCheck));
    }

}
