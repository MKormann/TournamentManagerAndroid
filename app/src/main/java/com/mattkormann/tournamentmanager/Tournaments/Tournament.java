package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.util.SeedFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Matt on 5/2/2016.
 */
public class Tournament {

    public static String STAT_CATEGORIES = "STAT_CATEGORIES";
    public static int MIN_TOURNAMENT_SIZE = 4;
    public static int MAX_TOURNAMENT_SIZE = 256;

    private String name;
    private int size;
    private int teamSize;
    private int rounds;
    private int savedId;
    private int matchesCompleted;
    private Map<Integer, Participant> participants;
    private Match[] matches;
    private String[] statCategories;
    private String saveTime;
    private SeedFactory sf;
    private boolean isDoubleElim = false;

    public Tournament() {

    }

    public Tournament(String name, int size, int teamSize) {
        this(name, size, teamSize, new String[] {}, new HashMap<Integer, Participant>());
    }


    public Tournament(String name, int size, int teamSize, String[] statCategories,
                                Map<Integer, Participant> participants) {
        this.name = name;
        if (size < MIN_TOURNAMENT_SIZE)
            throw new IllegalArgumentException("Tournament is less than minimum allowed size.");
        this.size = size;
        this.teamSize = teamSize;
        this.rounds = Integer.toBinaryString(size - 1).length();
        this.participants = participants;
        this.matches = new Match[size - 1];
        this.statCategories = statCategories;
        sf = new SeedFactory(size);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Compares the value of the last Match in the match array to the static Tournament value
    //for not having occurred.
    public boolean isOver() {
        for (Match m : matches) {
            if (m.getWinner() == Match.NOT_YET_ASSIGNED) return false;
        }
        return true;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public void setSaveTimeToCurrent() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        setSaveTime(dateFormat.format(date));
    }

    public boolean isDoubleElimination() {
        return isDoubleElim;
    }

    public int getSize() {
        return size;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public Map<Integer, Participant> getParticipants() {
        return participants;
    }

    public Match[] getMatches() {
        return matches;
    }

    public Match getMatch(int matchId) {
        return matches[matchId];
    }

    public void setMatches(Match[] matches) {
        this.matches = matches;
    }

    public Participant getParticipant(int seed) {
        if (seed < 1 || seed > size)
            throw new IndexOutOfBoundsException("" + seed + " is not a valid seed.");
        return participants.get(seed);
    }

    public void setParticipants(Map<Integer, Participant> participants) {
        if (participants.size() != size)
            throw new IllegalArgumentException("Participant map does not equal tournament size.");
        this.participants = participants;
    }

    public int getNumberOfRounds() {
        return rounds;
    }

    public int getRoundStartDelimiter(int roundNumber) {
        if (roundNumber == 1) return 0;
        else return (getRoundEndDelimiter(roundNumber - 1) + 1);
    }

    public int getRoundEndDelimiter(int roundNumber) {
        if (roundNumber < 1 || roundNumber > rounds) {
            throw new IllegalArgumentException("Round number is invalid: " + roundNumber);
        }
        return (int)(matches.length - Math.pow(2, (rounds - roundNumber)));
    }

    public void setNextMatchIds(int prelimParticipants) {

       //Get index for final match, set next match equal to a bye since it doesn't exist
        int num = matches.length - 1;
        matches[num].setNextMatchId(Match.BYE);

        //Get number of prelim matches
        int prelimMatches = prelimParticipants / 2;


        //Iterate backwards through tournament setting next match, until prelim round is hit
        for (int i = num - 1; i >= prelimMatches;) {
            matches[i--].setNextMatchId(num);
            matches[i--].setNextMatchId(num--);
        }

        if (prelimMatches > 0) {
            //Assign next match id for the preliminary round matches
            for (int i = 0, j = getRoundStartDelimiter(2); i <= getRoundEndDelimiter(1); j++) {
                if (matches[j].getParticipantSeed(1) == Match.NOT_YET_ASSIGNED) {
                    matches[i++].setNextMatchId(j);
                }
            }
        }

    }

    public void assignSeeds() {

        int[] seedsInMatchOrder = sf.getSeedsInMatchOrder();

        int byes = sf.getByes();
        if (byes > 0) {
            matches = new Match[size - 1 + byes];
        }

        for (int i = 0; i < matches.length; i++) {
            matches[i] = new StandardMatch(2, statCategories.length); //TODO multi-participant matches

            if (i * 2 + 1 < seedsInMatchOrder.length) {
                matches[i].setParticipant(0, seedsInMatchOrder[i * 2]);
                matches[i].setParticipant(1, seedsInMatchOrder[i * 2 + 1]);
            } else {
                matches[i].setParticipants(new int[] {Match.NOT_YET_ASSIGNED, Match.NOT_YET_ASSIGNED});
            }
        }

        int prelimParticipants = sf.getPrelimNumber();
        setNextMatchIds(prelimParticipants);

        //Set winner and advance those with byes
        for (int i = 0; i <= getRoundEndDelimiter(1); i++) {
            if (matches[i].getParticipantSeed(1) == Match.BYE) {
                matches[i].setWinner(0);
                Match m = matches[matches[i].getNextMatchId()];
                m.setParticipant(0, matches[i].getParticipantSeed(0));
            }
        }
    }

    public void setMatchWinner(int matchId, int winner) {
        //Retrieve given match
        Match m = getMatch(matchId);

        //If winner hasn't changed, do nothing, else set winner
        if (m.getWinner() == winner) return;
        m.setWinner(winner);

        //If reaching this point, winner has changed.  Iterate through remaining rounds, set
        //participant in next to new winner, and check if previous winner had won future matches and
        //remove
        int participantIndex = (winner != Match.NOT_YET_ASSIGNED) ? m.getParticipantSeed(winner) :
                Match.NOT_YET_ASSIGNED;
        while (m.getNextMatchId() != Match.BYE) {
            int nextMatchId = m.getNextMatchId();
            Match next = getMatch(nextMatchId);
            int indexInNext = getParticipantNumInNextMatch(matchId);
            next.setParticipant(indexInNext, participantIndex);
            if (next.getWinner() == indexInNext) {
                next.setWinner(Match.NOT_YET_ASSIGNED);
                matchId = m.getNextMatchId();
                m = next;
                participantIndex = Match.NOT_YET_ASSIGNED;
            } else break;
        }
    }

    public void setMatchWinner(int matchId, int winner, double[] stats) {
        //Update stats
        getMatch(matchId).setStatistics(stats);
        setMatchWinner(matchId, winner);
    }

    public int getParticipantNumInNextMatch(int matchId) {
        if (matchId < sf.getPrelimNumber()) return 1; //TODO more match participants
        int index = 0;
        Match match = getMatch(matchId);
        int prevMatchId = matchId - 1;
        while (prevMatchId >= 0) {
            if (getMatch(prevMatchId).getNextMatchId() == match.getNextMatchId()) {
                index++;
                prevMatchId--;
            }
            else break;
        }
        return index;
    }

    public int getSavedId() {
        return savedId;
    }

    public void setSavedId(int id) {
        savedId = id;
    }

    public boolean isStatTrackingEnabled(){
        return (statCategories.length != 0);
    }

    public String[] getStatCategories() {
        return statCategories;
    }

    public void setStatCategories(String[] statCategories) {
        if (statCategories.length > R.integer.max_number_of_stats)
            throw new IllegalArgumentException("Maximum number of stat categories is " + R.integer.max_number_of_stats);
        this.statCategories = statCategories;
    }

    public int getNumberOfStatistics() {
        return statCategories.length;
    }
}

