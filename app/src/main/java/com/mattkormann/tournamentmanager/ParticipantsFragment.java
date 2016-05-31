package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;

import java.util.logging.Logger;

public class ParticipantsFragment extends Fragment
        implements View.OnClickListener{

    private ParticipantInfoListener mCallback;
    private DatabaseHelper mDbHelper;
    private LinearLayout participantDisplay ;

    public static final String TYPE_TO_DISPLAY = "TYPE_TO_DISPLAY";
    public static final int INDIVIDUALS = 0;
    public static final int TEAMS = 1;

    //Empty constructor
    public ParticipantsFragment() {

    }

    //Static method to retrieve instance
    public static ParticipantsFragment newInstance() {
        ParticipantsFragment fragment = new ParticipantsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants, container, false);

        setLabels(view);

        //Set button listeners
        Button addButton = (Button)view.findViewById(R.id.add_participant);
        addButton.setOnClickListener(this);
        Button editButton = (Button)view.findViewById(R.id.edit_participant);
        editButton.setOnClickListener(this);

        //Initialize database helper
        mDbHelper = new DatabaseHelper(getContext());

        //Find table layout, and populate table with initial data
        participantDisplay = (LinearLayout)view.findViewById(R.id.participant_table);
        populateTable(getArguments().getInt(TYPE_TO_DISPLAY));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParticipantInfoListener) {
            mCallback = (ParticipantInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ParticipantInfoListener");
        }
    }

    //Fill table with participant information
    //@param refers to value of isTeam column, returning individuals or teams
    private void populateTable(int type) {

        //Retrieve participant information from database
        Cursor c = retrieveParticipantData(type);
        c.moveToFirst();

        //Erase any existing display
        participantDisplay.removeAllViews();

        //Create a row with count and name fields for each entry
        for (int i = 0; i < c.getCount(); i++) {
            LinearLayout row = getParticipantRow(
                    c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable._ID)),
                    c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)));
            participantDisplay.addView(row);
            c.moveToNext();
        }
    }

    //Executes the SQL statement to retrieve participant data from table
    //@param type refers to value of isTeam column, returning individuals or teams
    private Cursor retrieveParticipantData(int type) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.ParticipantTable._ID,
                DatabaseContract.ParticipantTable.COLUMN_NAME_NAME,
                DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM
        };

        String selection = DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM + "=?";

        String[] selectionArgs = {String.valueOf(type)};

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

    private LinearLayout getParticipantRow(String rowId, String rowName) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView id = new TextView(getContext());
        id.setText(rowId);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = .2f;
        lp.gravity = Gravity.CENTER;
        id.setLayoutParams(lp);

        TextView name = new TextView(getContext());
        name.setText(rowName);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp2.weight = .8f;
        lp2.gravity = Gravity.LEFT;
        name.setLayoutParams(lp2);

        row.addView(id);
        row.addView(name);

        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lpRow.weight = 1.0f;
        row.setLayoutParams(lpRow);

        return row;
    }

    int saveInformation(String name, int type) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM, type);

        long newRowId;
        newRowId = db.insert(
                DatabaseContract.ParticipantTable.TABLE_NAME,
                null,
                values
        );


        //Display updated participant list
        populateTable(getArguments().getInt(TYPE_TO_DISPLAY));

        return (int)newRowId;
    }

    // Set subtitle and button labels based on what type is being displayed
    private void setLabels(View view) {
        if (getArguments() != null) {
            TextView text = (TextView)view.findViewById(R.id.participant_type);
            Button addButton = (Button)view.findViewById(R.id.add_participant);
            Button editButton = (Button)view.findViewById(R.id.edit_participant);
            switch(getArguments().getInt(TYPE_TO_DISPLAY)) {
                case(TEAMS):
                    text.setText(getString(R.string.teams));
                    addButton.setText(R.string.add_team);
                    editButton.setText(R.string.edit_team);
                    break;
                case(INDIVIDUALS):
                    text.setText(R.string.individuals);
                    addButton.setText(R.string.add_participant);
                    editButton.setText(R.string.edit_participant);
                    break;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mDbHelper = null;
    }

    @Override
    public void onClick(View view) {
        mCallback.showParticipantInfoDialog(getArguments().getInt(TYPE_TO_DISPLAY));
    }

    //Interface to be implemented by MainActivity class
    //Methods to show Add/Edit Dialog and receive information entered
    public interface ParticipantInfoListener {
        void showParticipantInfoDialog(int type);
        void onFinishParticipantInformationDialog(String name, int type);
    }
}
