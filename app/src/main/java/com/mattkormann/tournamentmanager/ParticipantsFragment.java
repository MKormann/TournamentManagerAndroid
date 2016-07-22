package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.util.ParticipantsAdapter;

public class ParticipantsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ParticipantInfoListener mCallback;
    private RecyclerView recyclerView;
    private ParticipantsAdapter participantsAdapter;
    private boolean addingNew = true;

    public static final String TYPE_TO_DISPLAY = "TYPE_TO_DISPLAY";
    public static final int INDIVIDUALS = 0;
    public static final int TEAMS = 1;
    private static final int PARTICIPANT_LOADER = 0;

    //Empty constructor
    public ParticipantsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PARTICIPANT_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants, container, false);
        setLabels(view);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        participantsAdapter = new ParticipantsAdapter(new ParticipantsAdapter.ParticipantClickListener() {
            @Override
            public void onClick(String name, int id) {
                mCallback.showParticipantInfoDialog(DatabaseContract.ParticipantTable.buildParticipantUri(id));
            }
        });
        recyclerView.setAdapter(participantsAdapter);

        recyclerView.setHasFixedSize(true);

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

    public void updateList() {
        participantsAdapter.notifyDataSetChanged();
    }

    public void saveParticipant(String name, int type) {

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM, type);

        if (addingNew) {
            Uri newUri = getActivity().getContentResolver().insert(DatabaseContract.ParticipantTable.CONTENT_URI,
                    values);
            updateList();
        } else {

        }
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PARTICIPANT_LOADER:
                return new CursorLoader(getActivity(),
                        DatabaseContract.ParticipantTable.CONTENT_URI,
                        null,
                        DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM + "=?",
                        new String[]{"0"}, //TODO teams
                        DatabaseContract.ParticipantTable._ID + " ASC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        participantsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        participantsAdapter.swapCursor(null);
    }

    //Interface to be implemented by MainActivity class
    //Methods to show Add/Edit Dialog and receive information entered
    public interface ParticipantInfoListener {
        void showParticipantInfoDialog(Uri uri);
        void onFinishParticipantInformationDialog(String name, int type);
    }
}
