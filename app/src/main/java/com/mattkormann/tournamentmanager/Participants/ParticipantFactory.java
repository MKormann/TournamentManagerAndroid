package com.mattkormann.tournamentmanager.participants;

import android.database.sqlite.SQLiteDatabase;

import com.mattkormann.tournamentmanager.sql.DatabaseHelper;

/**
 * Created by Matt on 5/3/2016.
 */
public class ParticipantFactory {

    public static Participant getParticipant(String type, String name, int ID) {
        switch (type) {
            case "single":
                return new SingleParticipant(name, ID);
            case "team":
                return new TeamParticipant(name, ID);
            default:
                return new SingleParticipant(name, ID);
        }
    }
}
