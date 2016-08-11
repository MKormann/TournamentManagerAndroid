package com.mattkormann.tournamentmanager.sql;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Matt on 5/2/2016.
 * Schema for all databases used in the Tournament Manager app
 */
public class DatabaseContract {

    public static final String AUTHORITY =
            "com.mattkormann.tournamentmanager.sql";

    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";

    //Holds create statement for every table for use in onCreate method
    public static final String[] TABLE_CREATE_STATEMENTS = {
            ParticipantTable.CREATE_TABLE,
            TeamPairings.CREATE_TABLE,
            TournamentHistory.CREATE_TABLE
    };

    //Table of saved participants
    public static abstract class ParticipantTable implements BaseColumns {
        public static final String TABLE_NAME = "participantList";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildParticipantUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IS_TEAM = "isTeam";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_IS_TEAM + INTEGER_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //Table of participant ID to team ID pairings
    public static abstract class TeamPairings implements BaseColumns {
        public static final String TABLE_NAME = "teamMembers";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildTeamPairingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String COLUMN_NAME_PART_ID = "participantID";
        public static final String COLUMN_NAME_TEAM_ID = "teamID";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_NAME_PART_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_TEAM_ID + INTEGER_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //Table of previously run tournaments
    public static abstract class TournamentHistory implements BaseColumns {
        public static final String TABLE_NAME = "tournamentHistory";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final Uri CONTENT_HISTORY_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).appendPath("history").build();

        public static Uri buildSavedTournamentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
    }

        public static final String COLUMN_NAME_TOURNAMENT_NAME = "tournamentName";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_WINNER_ID = "winnerID";
        public static final String COLUMN_NAME_RUNNER_UP_ID = "runnerUpID";
        public static final String COLUMN_NAME_FINISHED = "isFinished";
        public static final String COLUMN_NAME_SAVE_TIME = "saveTime";
        public static final String COLUMN_NAME_PARTICIPANT_IDS = "participantIds";
        public static final String COLUMN_NAME_MATCH_DETAILS = "matchDetails";
        public static final String COLUMN_NAME_STAT_CATEGORIES = "statCategories";
        public static final String COLUMN_NAME_STAT_VALUES = "statValues";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_TOURNAMENT_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SIZE + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_WINNER_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_RUNNER_UP_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_FINISHED + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_SAVE_TIME + DATETIME_TYPE + COMMA_SEP +
                COLUMN_NAME_PARTICIPANT_IDS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_MATCH_DETAILS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_STAT_CATEGORIES + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_STAT_VALUES + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //Table of each individual performance in previously run tournaments
    public static abstract class IndividualHistory implements BaseColumns {
        public static final String TABLE_NAME = "individualHistory";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildIndividualUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String COLUMN_NAME_PART_ID = "participantID";
        public static final String COLUMN_NAME_FINISHED_TOURNAMENT_ID = "tournamentNumber";
        public static final String COLUMN_NAME_PLACE = "place";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_PART_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_FINISHED_TOURNAMENT_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_PLACE + INTEGER_TYPE + COMMA_SEP + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
