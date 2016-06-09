package com.mattkormann.tournamentmanager.util;

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
                {8, new int[] {1, 8, 4, 5, 2, 7, 3, 6}}, {13, new int[] {1, 0, 8, 9, 4, 13, 5, 12, 2, 0, 7, 10, 3, 0, 6, 11}},
                {24, new int[] {16, 17, 9, 24, 13, 20, 12, 21, 15, 18, 10, 23, 14, 19, 11, 22,
                        1, 0, 8, 0, 4, 0, 5, 0, 2, 0, 7, 0, 3, 0, 6, 0}},
                {49, new int[] {1, 0, 32, 33, 16, 49, 17, 48, 8, 0, 25, 40, 9, 0, 24, 41, 4, 0, 29, 36, 13, 0, 20, 45, 5, 0, 28, 37,
                12, 0, 21, 44, 2, 0, 31, 34, 15, 0, 18, 47, 7, 0, 26, 39, 10, 0, 23, 42, 3, 0, 30, 35, 14, 0, 19, 46, 6, 0, 27, 38,
                11, 0, 22, 43}}
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
