package com.mattkormann.tournamentmanager.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.Telephony;

import com.mattkormann.tournamentmanager.participants.Participant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 7/20/2016.
 */
public class TournamentContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    //Return codes for uri matcher
    private static final int SINGLE_PARTICIPANT = 1;
    private static final int PARTICIPANTS = 2;
    private static final int TEAM_PAIRINGS = 4;
    private static final int SINGLE_TEMPLATE = 5;
    private static final int TEMPLATES = 6;
    private static final int SINGLE_TOURNAMENT = 7;
    private static final int TOURNAMENTS = 8;
    private static final int TOURNAMENT_HISTORY = 9;
    private static final int INDIVIDUAL_HISTORY = 10;

    //Add uri matchers for all of the databases tables
    static {
        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.ParticipantTable.TABLE_NAME + "/#", SINGLE_PARTICIPANT);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.ParticipantTable.TABLE_NAME, PARTICIPANTS);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.TeamPairings.TABLE_NAME, TEAM_PAIRINGS);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.SavedTournaments.TABLE_NAME + "/#", SINGLE_TEMPLATE);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.SavedTournaments.TABLE_NAME, TEMPLATES);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.TournamentHistory.TABLE_NAME + "/#", SINGLE_TOURNAMENT);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.TournamentHistory.TABLE_NAME, TOURNAMENTS);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.TournamentHistory.TABLE_NAME + "/history", TOURNAMENT_HISTORY);

        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.IndividualHistory.TABLE_NAME, INDIVIDUAL_HISTORY);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //Determine which query to perform
        switch (uriMatcher.match(uri)) {
            case SINGLE_PARTICIPANT:
                queryBuilder.setTables(DatabaseContract.ParticipantTable.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.ParticipantTable._ID + "=" +
                    uri.getLastPathSegment());
                break;
            case PARTICIPANTS:
                queryBuilder.setTables(DatabaseContract.ParticipantTable.TABLE_NAME);
                break;
            case TEAM_PAIRINGS:
                queryBuilder.setTables(DatabaseContract.TeamPairings.TABLE_NAME);
                break;
            case SINGLE_TEMPLATE:
                queryBuilder.setTables(DatabaseContract.SavedTournaments.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.SavedTournaments._ID + "=" +
                        uri.getLastPathSegment());
                break;
            case TEMPLATES:
                queryBuilder.setTables(DatabaseContract.SavedTournaments.TABLE_NAME);
                break;
            case SINGLE_TOURNAMENT:
                queryBuilder.setTables(DatabaseContract.TournamentHistory.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.TournamentHistory._ID + "=" +
                        uri.getLastPathSegment());
                break;
            case TOURNAMENTS:
                queryBuilder.setTables(DatabaseContract.TournamentHistory.TABLE_NAME);
                break;
            case TOURNAMENT_HISTORY:
                String t = DatabaseContract.TournamentHistory.TABLE_NAME;
                String p = DatabaseContract.ParticipantTable.TABLE_NAME;

                projection = new String[] {
                        t + "." + DatabaseContract.TournamentHistory._ID,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_WINNER_ID,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME,
                        DatabaseContract.ParticipantTable.COLUMN_NAME_NAME
                };

                queryBuilder.setTables(t + " LEFT OUTER JOIN " +
                        p + " ON " +
                        t + "." + DatabaseContract.TournamentHistory.COLUMN_NAME_WINNER_ID + "=" +
                        p + "." + DatabaseContract.ParticipantTable._ID
                );

                break;
            case INDIVIDUAL_HISTORY:
                queryBuilder.setTables(DatabaseContract.IndividualHistory.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri newUri= null;
        long rowId;

        switch (uriMatcher.match(uri)) {
            case PARTICIPANTS:
                rowId = dbHelper.getWritableDatabase().insert(
                    DatabaseContract.ParticipantTable.TABLE_NAME, null, values);
                if (rowId > 0) newUri = DatabaseContract.ParticipantTable.buildParticipantUri(rowId);
                break;
            case TEAM_PAIRINGS:
                rowId = dbHelper.getWritableDatabase().insert(
                        DatabaseContract.TeamPairings.TABLE_NAME, null, values);
                if (rowId > 0) newUri = DatabaseContract.TeamPairings.buildTeamPairingUri(rowId);
                break;
            case TEMPLATES:
                rowId = dbHelper.getWritableDatabase().insert(
                        DatabaseContract.SavedTournaments.TABLE_NAME, null, values);
                if (rowId > 0) newUri = DatabaseContract.SavedTournaments.buildTemplateUri(rowId);
                break;
            case TOURNAMENTS:
                rowId = dbHelper.getWritableDatabase().insert(
                        DatabaseContract.TournamentHistory.TABLE_NAME, null, values);
                if (rowId > 0) newUri = DatabaseContract.TournamentHistory.buildSavedTournamentUri(rowId);
                break;
            case INDIVIDUAL_HISTORY:
                rowId = dbHelper.getWritableDatabase().insert(
                        DatabaseContract.IndividualHistory.TABLE_NAME, null, values);
                if (rowId > 0) newUri = DatabaseContract.IndividualHistory.buildIndividualUri(rowId);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (rowId > 0) getContext().getContentResolver().notifyChange(uri, null);
        else throw new SQLException();

        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int numberOfRowsUpdated;
        String id = "", tableName = "", idCol = "";

        switch (uriMatcher.match(uri)) {
            case SINGLE_PARTICIPANT:
                id = uri.getLastPathSegment();
                tableName= DatabaseContract.ParticipantTable.TABLE_NAME;
                idCol = DatabaseContract.ParticipantTable._ID;
                break;
            case SINGLE_TEMPLATE:
                id = uri.getLastPathSegment();
                tableName= DatabaseContract.SavedTournaments.TABLE_NAME;
                idCol = DatabaseContract.SavedTournaments._ID;
                break;
            case SINGLE_TOURNAMENT:
                id = uri.getLastPathSegment();
                tableName= DatabaseContract.TournamentHistory.TABLE_NAME;
                idCol = DatabaseContract.TournamentHistory._ID;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                tableName, values, idCol + "=" + id,
                selectionArgs);

        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;
        String id = "";
        String tableName = "";
        String idCol = "";

        switch(uriMatcher.match(uri)) {
            case SINGLE_PARTICIPANT:
                id = uri.getLastPathSegment();
                tableName = DatabaseContract.ParticipantTable.TABLE_NAME;
                idCol = DatabaseContract.ParticipantTable._ID;
                break;
            case SINGLE_TEMPLATE:
                id = uri.getLastPathSegment();
                tableName = DatabaseContract.SavedTournaments.TABLE_NAME;
                idCol = DatabaseContract.SavedTournaments._ID;
                break;
            case SINGLE_TOURNAMENT:
                id = uri.getLastPathSegment();
                tableName = DatabaseContract.TournamentHistory.TABLE_NAME;
                idCol = DatabaseContract.TournamentHistory._ID;
                break;
            default: throw new UnsupportedOperationException();
        }

        numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                tableName, idCol + "=" + id, selectionArgs);

        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}
