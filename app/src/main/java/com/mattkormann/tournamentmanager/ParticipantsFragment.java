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
import android.support.v7.widget.GridLayoutManager;
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
    private Button addButton;
    private RecyclerView recyclerView;
    private ParticipantsAdapter participantsAdapter;
    private boolean addingNew = true;

    public static final String TYPE_TO_DISPLAY = "TYPE_TO_DISPLAY";
    public static final String PARTICIPANT_URI = "PARTICIPANT_URI";
    public static final String ADDING_NEW = "ADDING_NEW";
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

        addButton = (Button)view.findViewById(R.id.add_participant);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingNew = true;
                mCallback.showParticipantInfoDialog(DatabaseContract.ParticipantTable.CONTENT_URI, addingNew);
            }
        });

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        participantsAdapter = new ParticipantsAdapter(new ParticipantsAdapter.ParticipantClickListener() {
            @Override
            public void onClick(String name, int id, LinearLayout row) {
                addingNew = false;
                mCallback.showParticipantInfoDialog(DatabaseContract.ParticipantTable.buildParticipantUri(id), addingNew);
            }
        });
        recyclerView.setAdapter(participantsAdapter);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(glm);


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

    public void saveParticipant(Uri uri, ContentValues values) {

        if (addingNew) {
            Uri newUri = getActivity().getContentResolver().insert(uri, values);
            updateList();
        } else {
            int updatedRows = getActivity().getContentResolver().update(uri, values, null, null);
            updateList();
            addingNew = true;
        }
    }

    // Set subtitle and button labels based on what type is being displayed
    private void setLabels(View view) {
        if (getArguments() != null) {
            TextView text = (TextView)view.findViewById(R.id.participant_type);
            Button addButton = (Button)view.findViewById(R.id.add_participant);
            switch(getArguments().getInt(TYPE_TO_DISPLAY)) {
                case(TEAMS):
                    text.setText(getString(R.string.teams));
                    addButton.setText(R.string.add_team);
                    break;
                case(INDIVIDUALS):
                    text.setText(R.string.individuals);
                    addButton.setText(R.string.add_participant);
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
        void showParticipantInfoDialog(Uri uri, boolean addingNew);
        void onFinishParticipantInformationDialog(Uri uri, ContentValues values);
    }
}
