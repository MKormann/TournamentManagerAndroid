package com.mattkormann.tournamentmanager.tournaments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.participants.ParticipantFactory;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 5/30/2016.
 */
//TODO different Tournament types
public class SqliteTournamentDAO implements TournamentDAO {

    private DatabaseHelper mDbHelper;
    private final String strSeparator = "_,_";

    public SqliteTournamentDAO(DatabaseHelper mDbHelper) {
        this.mDbHelper = mDbHelper;
    }

    @Override
    public int saveFullTournament(Tournament tournament) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME, tournament.getName());

        int size = tournament.getSize();

        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE, size);
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_WINNER_ID, tournament.isOver() ?
                tournament.getMatch(tournament.getMatches().length - 1).getWinner() :
                Match.NO_WINNER);
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_RUNNER_UP_ID, tournament.isOver() ?
                tournament.getMatch(tournament.getMatches().length - 1).getRunnerUp() :
                Match.NO_WINNER);
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED, tournament.isOver() ? 1 : 0);

        tournament.setSaveTimeToCurrent();
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME, tournament.getSaveTime());

        //Insert participant ids
        Integer[] ids = new Integer[size];
        for (int i = 0; i < size; i++) {
            ids[i] = tournament.getParticipant(i).getID();
        }
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_PARTICIPANT_IDS, arrayToString(ids));

        //Insert match information
        int numParticipants = tournament.getMatch(0).getParticipantIndices().length;

        //Create integer array equal to the number of matches (size - 1) multiplied by the number
        //of participants + an extra spot for the winner id
        Integer[] matchesInfo = new Integer[(size - 1) * (numParticipants + 1)];
        int count = 0;
        for (Match m : tournament.getMatches()) {
            for (int i: m.getParticipantIndices()) {
                matchesInfo[count++] = i;
            }
            matchesInfo[count++] = m.getWinner();
        }
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_MATCH_DETAILS, arrayToString(matchesInfo));

        //Insert stat categories
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_STAT_CATEGORIES,
                arrayToString(tournament.getStatCategories()));

        //Insert stat values
        int numStats = tournament.getStatCategories().length;

        //Create an array equal to number of matches * total stats for each match (categories * participants)
        Double[] statValues = new Double[(size - 1) * (numStats * numParticipants)];
        count = 0;
        for (Match m: tournament.getMatches()) {
            for (double d : m.getStatistics()) {
                statValues[count++] = d;
            }
        }
        values.put(DatabaseContract.TournamentHistory.COLUMN_NAME_STAT_VALUES, arrayToString(statValues));

        if (tournament.getSavedId() == NOT_YET_SAVED) {
            long rowId = db.insert(
                    DatabaseContract.TournamentHistory.TABLE_NAME,
                    null,
                    values
            );
            tournament.setSavedId((int)rowId);
        } else {
            String selection = DatabaseContract.TournamentHistory._ID + "=?";
            String[] selectionArgs = {String.valueOf(tournament.getSavedId())};

            int check = db.update(
                    DatabaseContract.TournamentHistory.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
        }
        return tournament.getSavedId();
    }


    //Converts array into a string to store in database
    private String arrayToString(Object[] array) {
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
    private String[] stringToArray(String databaseString) {
        return databaseString.split(strSeparator);
    }

    @Override
    public Tournament loadFullTournament(int tournamentId) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {DatabaseContract.TournamentHistory._ID,
                DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME,
                DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE,
                DatabaseContract.TournamentHistory.COLUMN_NAME_WINNER_ID,
                DatabaseContract.TournamentHistory.COLUMN_NAME_RUNNER_UP_ID,
                DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED,
                DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME,
                DatabaseContract.TournamentHistory.COLUMN_NAME_PARTICIPANT_IDS,
                DatabaseContract.TournamentHistory.COLUMN_NAME_MATCH_DETAILS,
                DatabaseContract.TournamentHistory.COLUMN_NAME_STAT_CATEGORIES,
                DatabaseContract.TournamentHistory.COLUMN_NAME_STAT_VALUES
        };

        String selection = DatabaseContract.TournamentHistory._ID + "=?";

        String[] selectionArgs = {String.valueOf(tournamentId)};

        Cursor c = db.query(
                DatabaseContract.TournamentHistory.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        c.moveToFirst();

        String name = c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME));
        int size = c.getInt(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE));
        int finished = c.getInt(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED));
        String endTime = "";
        if (finished == 1) {
            endTime = c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME));
        }

        String statCategoriesString = c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_STAT_CATEGORIES));
        String[] statCategories = stringToArray(statCategoriesString);

        //Create matches array
        Match[] matches = new Match[size - 1];

        //Read match String from database, convert to String array, then int array
        String matchesInfoString = c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_MATCH_DETAILS));
        String[] matchesInfoArray = stringToArray(matchesInfoString);
        int[] matchesInfo = new int[matchesInfoArray.length];
        for (int i = 0; i < matchesInfo.length; i++) {
            matchesInfo[i] = Integer.valueOf(matchesInfoArray[i]);
        }

        //Read stat values from database, convert to double array
        String statValuesString = c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_STAT_VALUES));
        String[] statValuesArray = stringToArray(statValuesString);
        double[] statistics = new double[statValuesArray.length];
        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = Double.valueOf(statValuesArray[i]);
        }

        //Assign match data
        int numStats = statCategories.length;
        for (int i = 0; i < matches.length; i++) {
            Match m = new StandardMatch(2, statCategories.length); //TODO change the 2's to accommodate different numParticipants
            m.setStatistics(Arrays.copyOfRange(statistics, (i * numStats), ((i + 1) * numStats)));
            m.setParticipant(0, matchesInfo[(2 + 1) * i]);
            m.setParticipant(1, matchesInfo[((2 + 1) * i) + 1]);
            m.setWinner(matchesInfo[((2 + 1) * i) + 2]);
        }

        //Create map of saved participants to create new Participant objects
        Cursor pc = readParticipantsData(db);
        pc.moveToFirst();
        Map<Integer, String> participantMap = new HashMap<>();
        for (int i = 0; i < pc.getCount(); i++) {
            participantMap.put(c.getInt(c.getColumnIndex(DatabaseContract.ParticipantTable._ID)),
                    c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)));
        }

        //Create participants array
        Participant[] participants = new Participant[size];

        //Read participant info from database
        String participantsString = c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_PARTICIPANT_IDS));
        String[] participantsArray = stringToArray(participantsString);
        int[] participantIds = new int[participantsArray.length];
        for (int i = 0; i < participantIds.length; i++) {
            participantIds[i] = Integer.valueOf(participantsArray[i]);
        }

        //Assign participants to array
        for (int i = 0; i < participants.length; i++) {
            participants[i] = participantMap.containsKey(participantIds[i]) ?
                ParticipantFactory.getParticipant("single", participantMap.get(participantIds[i]), participantIds[i]) :
                ParticipantFactory.getParticipant("single", "Unknown Participant", Participant.GENERIC);
        }

        Tournament tournament = new SingleElimTournament(name, size, 1, statCategories, participants);
        tournament.setSavedId(tournamentId);
        tournament.setMatches(matches);
        if (finished == 1) tournament.setSaveTime(endTime);

        return tournament;
    }

    @Override
    public Tournament loadTournamentFromTemplate(int templateId) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.SavedTournaments._ID,
                DatabaseContract.SavedTournaments.COLUMN_NAME_NAME,
                DatabaseContract.SavedTournaments.COLUMN_NAME_SIZE,
                DatabaseContract.SavedTournaments.COLUMN_NAME_TEAM_SIZE,
                DatabaseContract.SavedTournaments.COLUMN_NAME_DOUBLE_ELIM,
                DatabaseContract.SavedTournaments.COLUMN_NAME_USE_STATS,
                DatabaseContract.SavedTournaments.COLUMN_NAME_STATS_ARRAY
        };

        String selection = DatabaseContract.SavedTournaments._ID + "=?";

        String[] selectionArgs = {String.valueOf(templateId)};

        Cursor c = db.query(
                DatabaseContract.SavedTournaments.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        c.moveToFirst();

        String name = c.getString(c.getColumnIndex(DatabaseContract.SavedTournaments.COLUMN_NAME_NAME));
        int size = c.getInt(c.getColumnIndex(DatabaseContract.SavedTournaments.COLUMN_NAME_SIZE));
        int teamSize = c.getInt(c.getColumnIndex(DatabaseContract.SavedTournaments.COLUMN_NAME_TEAM_SIZE));
        String statCategoriesString = c.getString(c.getColumnIndex(DatabaseContract.SavedTournaments.COLUMN_NAME_STATS_ARRAY));
        String[] statCategories = stringToArray(statCategoriesString);

        Tournament tournament = new SingleElimTournament(name, size, teamSize);
        tournament.setSavedId(NOT_YET_SAVED);
        tournament.setStatCategories(statCategories);

        return tournament;
    }

    @Override
    public int saveTournamentTemplate(int templateId, String name, int size, int teamSize, int doubleElim,
                                       int useStats, String[] statCategories) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_SIZE, size);
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_TEAM_SIZE, teamSize);
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_DOUBLE_ELIM, doubleElim);
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_USE_STATS, useStats);
        String statsArray = "";
        if (useStats == 1) statsArray = arrayToString(statCategories);
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_STATS_ARRAY, statsArray);

        int rowId;
        if (templateId == NEW_TOURNAMENT_TEMPLATE) {
            long newRowId = db.insert(
                    DatabaseContract.SavedTournaments.TABLE_NAME,
                    null,
                    values
            );
            rowId = (int)newRowId;
        } else {
            String selection = DatabaseContract.SavedTournaments._ID + "=?";
            String[] selectionArgs = {String.valueOf(templateId)};

            rowId = db.update(
                    DatabaseContract.SavedTournaments.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
        }

        return rowId;
    }

    private Cursor readParticipantsData(SQLiteDatabase db) {
        String[] projection = {
                DatabaseContract.ParticipantTable._ID,
                DatabaseContract.ParticipantTable.COLUMN_NAME_NAME,
                DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM
        };

        String selection = DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM + "=?";

        String[] selectionArgs = {"0"}; //TODO teams

        String sortOrder = DatabaseContract.ParticipantTable._ID + " ASC";

        return db.query(
                DatabaseContract.ParticipantTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
