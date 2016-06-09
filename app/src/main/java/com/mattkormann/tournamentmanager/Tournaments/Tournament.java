package com.mattkormann.tournamentmanager.tournaments;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.participants.Participant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Matt on 5/2/2016.
 */
public class Tournament {

    public static String STAT_CATEGORIES = "STAT_CATEGORIES";
    public static int NOT_YET_OCCURRED = -2;
    public static int MIN_TOURNAMENT_SIZE = 4;
    public static int MAX_TOURNAMENT_SIZE = 256;

    private String name;
    private int size;
    private int teamSize;
    private int rounds;
    private int savedId;
    private Participant[] participants;
    private Match[] matches;
    private String[] statCategories;
    private String saveTime;
    private boolean isDoubleElim = false;

    public Tournament() {

    }

    public Tournament(String name, int size, int teamSize) {
        this(name, size, teamSize, new String[] {}, new Participant[size]);
    }


    public Tournament(String name, int size, int teamSize, String[] statCategories,
                                Participant[] participants) {
        this.name = name;
        if (size < MIN_TOURNAMENT_SIZE)
            throw new IllegalArgumentException("Tournament is less than minimum allowed size.");
        this.size = size;
        this.teamSize = teamSize;
        this.rounds = Integer.toBinaryString(size - 1).length();
        this.participants = participants;
        this.matches = new Match[size - 1];
        this.statCategories = statCategories;
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
        return (matches[matches.length - 1].getWinner() != Tournament.NOT_YET_OCCURRED);
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

    public Participant[] getParticipants() {
        return participants;
    }

    public Match[] getMatches() {
        return matches;
    }

    public Match getMatch(int matchId) {
        return matches[matchId];
    }

    public void setMatches(Match[] matches) {
        if (matches.length != size - 1)
            throw new IllegalArgumentException("Match array is not correct length.");
        this.matches = matches;
    }

    public Participant getParticipant(int index) {
        if (index < 0 || index >= participants.length)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds.");
        return participants[index];
    }

    public void setParticipants(Participant[] participants) {
        if (participants.length != size)
            throw new IllegalArgumentException("Participant array does not equal tournament size.");
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

    public int getNextMatchId(int matchId) {

        //Adds a modifier of 1 to id if the tournament size is odd
        int nextMatchId = size % 2 == 0 ? matchId : matchId + 1;
        nextMatchId /= 2;

        int roundOneLength = (getRoundEndDelimiter(1) - getRoundStartDelimiter(1) + 1);

        //Check if the first round is a full round, if so add length to id and return
        //If not, add length of first full round ( 2) and add half of round 1 length
        if (size == Math.pow(2, rounds)) {
            return nextMatchId + roundOneLength;
        } else if (matchId <= getRoundEndDelimiter(1)) {
            return nextMatchId + roundOneLength;
        } else {
            return nextMatchId + (roundOneLength / 2) +
                    (getRoundEndDelimiter(2) - getRoundStartDelimiter(2) + 1);
        }
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

