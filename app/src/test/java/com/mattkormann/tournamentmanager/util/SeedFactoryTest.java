package com.mattkormann.tournamentmanager.util;

import com.mattkormann.tournamentmanager.tournaments.Match;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Matt on 6/9/2016.
 */
@RunWith(Parameterized.class)
public class SeedFactoryTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {8, new int[] {1, 8, 4, 5, 2, 7, 3, 6}},
                {13, new int[] {1, Match.BYE, 8, 9, 4, 13, 5, 12, 2, Match.BYE, 7, 10, 3, Match.BYE,
                        6, 11}},
                {24, new int[] {16, 17, 9, 24, 13, 20, 12, 21, 15, 18, 10, 23, 14, 19, 11, 22,
                        1, Match.NOT_YET_ASSIGNED, 8, Match.NOT_YET_ASSIGNED, 4, Match.NOT_YET_ASSIGNED,
                        5, Match.NOT_YET_ASSIGNED, 2, Match.NOT_YET_ASSIGNED, 7, Match.NOT_YET_ASSIGNED,
                        3, Match.NOT_YET_ASSIGNED, 6, Match.NOT_YET_ASSIGNED}},
                {49, new int[] {1, Match.BYE, 32, 33, 16, 49, 17, 48, 8, Match.BYE, 25, 40,
                        9, Match.BYE, 24, 41, 4, Match.BYE, 29, 36, 13, Match.BYE, 20, 45,
                        5, Match.BYE, 28, 37, 12, Match.BYE, 21, 44, 2, Match.BYE, 31, 34, 15,
                        Match.BYE, 18, 47, 7, Match.BYE, 26, 39, 10, Match.BYE, 23, 42,
                        3, Match.BYE, 30, 35, 14, Match.BYE, 19, 46, 6, Match.BYE, 27, 38,
                        11, Match.BYE, 22, 43}}
        });
    }

    private SeedFactory seedFactory;
    private int[] expectedSeeds;

    public SeedFactoryTest(int size, int[] expectedSeeds) {
        seedFactory = new SeedFactory(size);
        this.expectedSeeds = expectedSeeds;
    }

    @Test
    public void testGetSeedsInMatchOrder() {
        assertArrayEquals(expectedSeeds, seedFactory.getSeedsInMatchOrder());
    }
}
