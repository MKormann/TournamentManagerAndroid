package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;

public class ParticipantsFragment extends Fragment {

    private OnFragmentInteractionListener mCallback;
    private DatabaseHelper mDbHelper;
    private LinearLayout participantDisplay ;

    //Empty constructor
    public ParticipantsFragment() {

    }

    //Static method to retrieve instance
    public static ParticipantsFragment newInstance(String param1, String param2) {
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

        //Initialize database helper
        mDbHelper = new DatabaseHelper(getContext());

        //Find table layout, and populate table with initial data
        participantDisplay = (LinearLayout)view.findViewById(R.id.participant_table);
        populateTable(DatabaseContract.ParticipantTable.INDIVIDUALS);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mCallback = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //Fill table with participant information
    //@param refers to value of isTeam column, returning individuals or teams
    private void populateTable(String type) {
        Cursor c = retrieveParticipantData(type);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView id = new TextView(getContext());
            id.setText((i + 1) + "");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = .2f;
            lp.gravity = Gravity.CENTER;
            id.setLayoutParams(lp);

            TextView name = new TextView(getContext());
            name.setText(c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)));
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
            participantDisplay.addView(row);
            c.moveToNext();
        }
    }

    //Executes the SQL statement to retrieve participant data from table
    //@param type refers to value of isTeam column, returning individuals or teams
    private Cursor retrieveParticipantData(String type) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.ParticipantTable.COLUMN_NAME_NAME,
                DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM
        };

        String selection = DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM + "=?";

        String[] selectionArgs = {type};

        return db.query(
                DatabaseContract.ParticipantTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
                );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mDbHelper = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
