package com.mattkormann.tournamentmanager.tournaments;

import android.content.ContentValues;
import android.database.Cursor;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.participants.ParticipantFactory;
import com.mattkormann.tournamentmanager.sql.DatabaseContract.ParticipantTable;
import com.mattkormann.tournamentmanager.sql.DatabaseContract.TournamentHistory;
import com.mattkormann.tournamentmanager.util.SeedFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Matt on 5/30/2016.
 */
public class TournamentDAO {

    public static final int NEW_TOURNAMENT_TEMPLATE = -9;
    public static final int NOT_YET_SAVED = -7;

    private final String strSeparator = "_,_";

    //Creates an instance of ContentValues from a Tournament object containing all the information
    //needed to save to the Tournament History table
    public static ContentValues getFullTournamentContentValues(Tournament tournament) {
        ContentValues values = new ContentValues();

        values.put(TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME, tournament.getName());

        int size = tournament.getSize();

        values.put(TournamentHistory.COLUMN_NAME_SIZE, size);
        int winnerId = Match.NOT_YET_ASSIGNED;
        int runnerUpId = Match.NOT_YET_ASSIGNED;
        if (tournament.isOver()) {
            Match m = tournament.getMatch(tournament.getMatches().length - 1);
            Participant winner = tournament.getParticipant(m.getWinnerSeed());
            winnerId = winner.getID();
            Participant runnerUp = tournament.getParticipant(m.getRunnerUpSeed());
            runnerUpId = runnerUp.getID();
        }
        values.put(TournamentHistory.COLUMN_NAME_WINNER_ID, winnerId);
        values.put(TournamentHistory.COLUMN_NAME_RUNNER_UP_ID, runnerUpId);
        values.put(TournamentHistory.COLUMN_NAME_FINISHED, tournament.isOver() ? 1 : 0);

        tournament.setSaveTimeToCurrent();
        values.put(TournamentHistory.COLUMN_NAME_SAVE_TIME, tournament.getSaveTime());

        //Insert participant ids
        Integer[] ids = new Integer[size];
        for (int i = 0; i < size; i++) {
            ids[i] = tournament.getParticipant(i + 1).getID();
        }
        values.put(TournamentHistory.COLUMN_NAME_PARTICIPANT_IDS, arrayToString(ids));

        //Insert match information
        int numParticipants = tournament.getMatch(0).getParticipantSeeds().length;

        //Create integer array equal to the number of matches (size - 1) multiplied by the number
        //of participants + an extra spot for the winner id
        Integer[] matchesInfo = new Integer[(tournament.getMatches().length) * (numParticipants + 1)];
        int count = 0;
        for (Match m : tournament.getMatches()) {
            for (int i: m.getParticipantSeeds()) {
                matchesInfo[count++] = i;
            }
            matchesInfo[count++] = m.getWinner();
        }
        values.put(TournamentHistory.COLUMN_NAME_MATCH_DETAILS, arrayToString(matchesInfo));

        //Insert stat categories
        values.put(TournamentHistory.COLUMN_NAME_STAT_CATEGORIES,
                arrayToString(tournament.getStatCategories()));

        //Insert stat values
        int numStats = tournament.getStatCategories().length;

        //Create an array equal to number of matches * total stats for each match (categories * participants)
        Double[] statValues = new Double[(tournament.getMatches().length) * (numStats * numParticipants)];
        count = 0;
        for (Match m: tournament.getMatches()) {
            for (double d : m.getStatistics()) {
                statValues[count++] = d;
            }
        }
        values.put(TournamentHistory.COLUMN_NAME_STAT_VALUES, arrayToString(statValues));

        return values;
    }

    public static Tournament loadFullTournamentFromCursor(Cursor c, Cursor pCursor) {
        c.moveToFirst();

        String name = c.getString(c.getColumnIndex(TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME));
        int size = c.getInt(c.getColumnIndex(TournamentHistory.COLUMN_NAME_SIZE));
        int finished = c.getInt(c.getColumnIndex(TournamentHistory.COLUMN_NAME_FINISHED));
        String saveTime = c.getString(c.getColumnIndex(TournamentHistory.COLUMN_NAME_SAVE_TIME));

        String statCategoriesString = c.getString(c.getColumnIndex(TournamentHistory.COLUMN_NAME_STAT_CATEGORIES));
        String[] statCategories;
        if (statCategoriesString.equals("")) statCategories = new String[0];
        else statCategories = stringToArray(statCategoriesString);

        //Read match String from database, convert to String array, then int array
        String matchesInfoString = c.getString(c.getColumnIndex(TournamentHistory.COLUMN_NAME_MATCH_DETAILS));
        String[] matchesInfoArray = stringToArray(matchesInfoString);
        int[] matchesInfo = new int[matchesInfoArray.length];
        for (int i = 0; i < matchesInfo.length; i++) {
            matchesInfo[i] = Integer.valueOf(matchesInfoArray[i]);
        }

        //Create matches array
        Match[] matches = new Match[matchesInfo.length / 3];

        //Read stat values from database, convert to double array
        int numStats = statCategories.length;
        String statValuesString = c.getString(c.getColumnIndex(TournamentHistory.COLUMN_NAME_STAT_VALUES));
        String[] statValuesArray = stringToArray(statValuesString);
        double[] statistics = new double[statValuesArray.length];
        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = Double.valueOf(statValuesArray[i]);
        }


        //Assign match data
        for (int i = 0; i < matches.length; i++) {
            Match m = new StandardMatch(2, statCategories.length); //TODO change the 2's to accommodate different numParticipants per match
            if (numStats > 0) m.setStatistics(Arrays.copyOfRange(statistics, (i * (numStats * 2)), ((i + 1) * (numStats * 2))));
            m.setParticipant(0, matchesInfo[(2 + 1) * i]);
            m.setParticipant(1, matchesInfo[((2 + 1) * i) + 1]);
            m.setWinner(matchesInfo[((2 + 1) * i) + 2]);
            matches[i] = m;
        }

        //Create map of saved participants to create new Participant objects
        pCursor.moveToFirst();
        Map<Integer, String> loadedParticipants = new HashMap<>();
        for (int i = 0; i < pCursor.getCount(); i++) {
            loadedParticipants.put(pCursor.getInt(pCursor.getColumnIndex(ParticipantTable._ID)),
                    pCursor.getString(pCursor.getColumnIndex(ParticipantTable.COLUMN_NAME_NAME)));
            pCursor.moveToNext();
        }

        //Create participants map
        Map<Integer, Participant> participants = new HashMap<>();

        //Read participant info from database
        String participantsString = c.getString(c.getColumnIndex(TournamentHistory.COLUMN_NAME_PARTICIPANT_IDS));
        String[] participantsArray = stringToArray(participantsString);
        int[] participantIds = new int[participantsArray.length];
        for (int i = 0; i < participantIds.length; i++) {
            participantIds[i] = Integer.valueOf(participantsArray[i]);
        }

        //Assign participants to map, either loads a saved participant, loads a numbered generic participant previously generated,
        //or creates an unknown participant if neither is found
        for (int i = 0; i < participantIds.length; i++) {
            if (loadedParticipants.containsKey(participantIds[i]))
                participants.put(i + 1, ParticipantFactory.getParticipant(
                        "single", loadedParticipants.get(participantIds[i]), participantIds[i]));
            else if (participantIds[i] < 0 && participantIds[i] >= (0 - Tournament.MAX_TOURNAMENT_SIZE))
                participants.put(i + 1, ParticipantFactory.getParticipant("generic single", "", participantIds[i]));
            else participants.put(i + 1, ParticipantFactory.getParticipant("single", "Unknown Participant", Participant.GENERIC));
        }

        Tournament tournament = new SingleElimTournament(name, size, 1);
        tournament.setStatCategories(statCategories);
        tournament.setParticipants(participants);
        tournament.setMatches(matches);
        tournament.setSaveTime(saveTime);
        SeedFactory sf = new SeedFactory(size);
        sf.getSeedsInMatchOrder();
        tournament.setNextMatchIds(sf.getPrelimNumber());

        return tournament;
    }

    //Converts array into a string to store in database
    private static String arrayToString(Object[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str += array[i].toString();
            if (i < array.length - 1) {
                str += strSeparator;
            }
        }
        return str;
    }

    //Converts database string into array object
    private static String[] stringToArray(String databaseString) {
        return databaseString.split(strSeparator);
    }

    public static void saveTournamentPrefs(String name) {

    }

    public static void loadTournamentPrefs(String name) {

    }
}
