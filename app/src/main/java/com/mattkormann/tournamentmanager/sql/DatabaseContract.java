package com.mattkormann.tournamentmanager.sql;

import android.provider.BaseColumns;

/**
 * Created by Matt on 5/2/2016.
 * Schema for all databases used in the Tournament Manager app
 */
public class DatabaseContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    //Holds create statement for every table for use in onCreate method
    public static final String[] TABLE_CREATE_STATEMENTS = {
            ParticipantTable.CREATE_TABLE,
            TeamPairings.CREATE_TABLE
    };

    public DatabaseContract() {
        //Empty constructor
    }

    //Table of saved participants
    public static abstract class ParticipantTable implements BaseColumns {
        public static final String TABLE_NAME = "participantList";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IS_TEAM = "isTeam";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_IS_TEAM + INTEGER_TYPE + COMMA_SEP + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //Table of participant ID to team ID pairings
    public static abstract class TeamPairings implements BaseColumns {
        public static final String TABLE_NAME = "teamMembers";
        public static final String COLUMN_NAME_PART_ID = "participantID";
        public static final String COLUMN_NAME_TEAM_ID = "teamID";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                COLUMN_NAME_PART_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_TEAM_ID + INTEGER_TYPE + COMMA_SEP + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //Table of saved tournament templates
    public static abstract class SavedTournaments implements BaseColumns {
        public static final String TABLE_NAME = "savedTournaments";
        public static final String COLUMN_NAME_NAME = "tournamentName";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_DOUBLE_ELIM = "doubleElim";
        public static final String COLUMN_NAME_USE_TEAMS = "useTeams";
        public static final String COLUMN_NAME_TEAM_SIZE = "teamSize";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SIZE + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_DOUBLE_ELIM + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_USE_TEAMS + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_TEAM_SIZE + INTEGER_TYPE + COMMA_SEP + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    //Table of previously run tournaments
    public static abstract class TournamentHistory implements BaseColumns {
        public static final String TABLE_NAME = "tournamentHistory";
        public static final String COLUMN_NAME_TOURNAMENT_ID = "savedTournID";
        public static final String COLUMN_NAME_WINNER_ID = "winnerID";
        public static final String COLUMN_NAME_FINISHED = "isFinished";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + "INTEGER PRIMARY KEY," +
                COLUMN_NAME_TOURNAMENT_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_WINNER_ID + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_FINISHED + INTEGER_TYPE + COMMA_SEP + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
