package com.mattkormann.tournamentmanager.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matt on 5/2/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TournamentManager.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : DatabaseContract.TABLE_CREATE_STATEMENTS) {
            db.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //CODE BELOW ONLY WHEN USED AS CACHE, DELETES AND REINITIALIZES
        //db.exec(SQL_CREATE_ENTRIES);
        //onCreate(db);
    }
}