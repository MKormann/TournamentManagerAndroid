package com.mattkormann.tournamentmanager.util;

import com.mattkormann.tournamentmanager.tournaments.Match;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by Matt on 6/8/2016.
 * Note in this class the round referred to as the "First Round" is in fact the first FULL round
 * as opposed to a preliminary round of shorter length.  However outside of this class the preliminary
 * round is numerically round 1
 */
public class SeedFactory {

    private int size;
    private int byes = 0;
    private int[] firstRoundSeeds;
    private int[] prelimSeeds;
    private int[] temp;
    private boolean hasPrelimRound;

    public SeedFactory(int size) {
        this.size = size;
    }

    public int[] getSeedsInMatchOrder() {

        //Determine # of matches of the initial rounds
        int higherNum = Integer.highestOneBit(size);
        int lowerNum = Integer.highestOneBit(size) / 2;
        int maxMatchesInRound = size > higherNum + lowerNum ? higherNum : lowerNum;

        hasPrelimRound = size > maxMatchesInRound * 2;

        // if there is a prelim round, set number of participants competing in that round, else 0
        int prelimParticipants = (hasPrelimRound) ?
                (size - Integer.highestOneBit(size)) * 2 :
                0;
        prelimSeeds = new int[prelimParticipants];

        //Create array to hold first round seeds, if first round participants is less than size
        //then the Match interface value for a BYE is left to and pad the array for
        //the method seedSort.
        int firstRoundParticipants = size - prelimParticipants;

        firstRoundSeeds = new int[maxMatchesInRound * 2];
        int cnt = 0;
        while (cnt < firstRoundParticipants) {
            firstRoundSeeds[cnt] = cnt + 1;
            cnt++;
        }
        while (cnt < firstRoundSeeds.length) {
            if (hasPrelimRound) {
                firstRoundSeeds[cnt++] = Match.NOT_YET_ASSIGNED;
            } else {
                firstRoundSeeds[cnt++] = Match.BYE;
                byes += 1;
            }
        }

        temp = new int[firstRoundSeeds.length];
        seedSort(0, firstRoundSeeds.length);

        //Assign prelim seeds to the prelim array
        if (hasPrelimRound) assignPrelimSeeds();

        //Method to subtract one from the seed numbers to refer to a zero-based array
        changeFromSeedToIndex(firstRoundSeeds);
        changeFromSeedToIndex(prelimSeeds);

        return hasPrelimRound ? ArrayUtils.addAll(prelimSeeds, firstRoundSeeds) : firstRoundSeeds;
    }


    //Method takes an in-order array, length equalling a power of 2, and sorts it into seed order
    //for a tournament.
    public void seedSort (int low, int high) {
        if (high - low > 2) {

            for (int i = low; i < high; i++) {
                temp[i] = firstRoundSeeds[i];
            }

            int mid = low + ((high - low) / 2);
            int j = low;
            int k = mid;

            boolean b = true;
            for (int i = low; i < high;) {
                if (b){
                    firstRoundSeeds[j++] = temp[i++];
                    firstRoundSeeds[k++] = temp[i++];
                } else {
                    firstRoundSeeds[k++] = temp[i++];
                    firstRoundSeeds[j++] = temp[i++];
                }
                b = !b;
            }

            seedSort(low, mid);
            seedSort(mid, high);
        }
    }

    public void assignPrelimSeeds() {
        int cnt = 0;
        for (int i = 0; i < firstRoundSeeds.length - 1; i += 2) {
            if (firstRoundSeeds[i + 1] == Match.NOT_YET_ASSIGNED) {
                int one = firstRoundSeeds.length + 1 - firstRoundSeeds[i];
                int two = firstRoundSeeds.length + firstRoundSeeds[i];
                prelimSeeds[cnt++] = one;
                prelimSeeds[cnt++] = two;
            }
        }
    }

    private void changeFromSeedToIndex(int[] seeds) {
        for (int i = 0; i < seeds.length; i++) {
            if (seeds[i] == Match.NOT_YET_ASSIGNED || seeds[i] == Match.BYE) continue;
            seeds[i] = seeds[i] - 1;
        }
    }

    public boolean hasPrelimRound() {
        return hasPrelimRound;
    }

    public int getByes() {
        return byes;
    }

    public int getPrelimNumber() {
        return prelimSeeds.length;
    }
}
