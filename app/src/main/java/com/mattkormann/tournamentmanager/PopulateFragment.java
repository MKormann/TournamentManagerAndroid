package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.participants.ParticipantFactory;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;
import com.mattkormann.tournamentmanager.util.ParticipantsPoolAdapter;
import com.mattkormann.tournamentmanager.util.ParticipantsSeedAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PopulateFragment extends Fragment {

    private MainActivity mCallback;
    private ParticipantsPoolAdapter poolAdapter;
    private ParticipantsSeedAdapter seedAdapter;

    private SortedMap<Integer, Participant> participantPool;
    private Map<Integer, Participant> participants;
    private Button startButton;
    private Button fillButton;
    private Button unassignButton;
    private int size;
    private boolean isSeedSelected = false;
    private int selectedSeed;
    private boolean isSavedParticipantSelected = false;
    private Participant selectedParticipant;
    private LinearLayout selectedRow;

    public PopulateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_populate, container, false);

        size = mCallback.getCurrentTournament().getSize();

        RecyclerView seedRecyclerView = (RecyclerView) view.findViewById(R.id.seedRecyclerView);
        RecyclerView savedRecyclerView = (RecyclerView) view.findViewById(R.id.savedRecyclerView);
        seedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        savedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        participantPool = new TreeMap<>();
        createParticipantPool();

        poolAdapter = new ParticipantsPoolAdapter(new ParticipantsPoolAdapter.PoolClickListener() {
            @Override
            public void onClick(Participant participant, LinearLayout row) {
                onParticipantClicked(participant, row);
            }
        }, participantPool);

        savedRecyclerView.setAdapter(poolAdapter);
        savedRecyclerView.setHasFixedSize(true);

        participants = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            participants.put(i, null);
        }

        seedAdapter = new ParticipantsSeedAdapter(new ParticipantsSeedAdapter.SeedClickListener() {
            @Override
            public void onClick(int seed, LinearLayout row) {
                onSeedClicked(seed, row);
            }
        }, participants);

        seedRecyclerView.setAdapter(seedAdapter);
        seedRecyclerView.setHasFixedSize(true);

        startButton = (Button) view.findViewById(R.id.start_tournament);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTournamentParticipants();
            }
        });
        startButton.setEnabled(false);
        fillButton = (Button) view.findViewById(R.id.fill_participants);
        fillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillParticipants();
                startButton.setEnabled(true);
            }
        });
        unassignButton = (Button) view.findViewById(R.id.unassign_seed);
        unassignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSeedSelected) putSeedBackInPool(selectedSeed);
            }
        });

        return view;
    }

    private void setTournamentParticipants() {

        //Confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.finalize_participants_title))
                .setMessage(getString(R.string.finalize_participants_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(participants == null || areAnyUnassigned()) fillParticipants();

                        //Set the participants to the current tournament, save into history, and display
                        Tournament tournament = mCallback.getCurrentTournament();
                        tournament.setParticipants(participants);
                        tournament.assignSeeds();

                        ContentValues contentValues = TournamentDAO.getFullTournamentContentValues(tournament);
                        int id = tournament.getSavedId();
                        if (id == TournamentDAO.NOT_YET_SAVED) {
                            Uri newUri = getActivity().getContentResolver().insert(
                                    DatabaseContract.TournamentHistory.CONTENT_URI, contentValues);
                            int newId = Integer.valueOf(newUri.getLastPathSegment());
                            tournament.setSavedId(newId);
                        } else {
                            int updatedRows = getActivity().getContentResolver().update(
                                    DatabaseContract.TournamentHistory.buildSavedTournamentUri(id),
                                    contentValues, null, null);
                        }
                        mCallback.swapFragment(FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_DISPLAY_FRAGMENT));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }

    private void createParticipantPool() {
        Cursor c = getActivity().getContentResolver().query(
                DatabaseContract.ParticipantTable.CONTENT_URI,
                null,
                DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM + "=?",
                new String[]{"0"}, //TODO teams
                DatabaseContract.ParticipantTable._ID + " ASC");

        //c.moveToFirst();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME));
            int id = c.getInt(c.getColumnIndex(DatabaseContract.ParticipantTable._ID));
            Participant p = ParticipantFactory.getParticipant("single", name, id);
            participantPool.put(id, p);
        }
    }

    //Create and populate array with participants from each SeedView
    //If participant isn't present, a generic one is created and passed along
    private void fillParticipants() {
        int genCount = 0;
        for (int i = 1; i <= size; i++) {
            if (participants.get(i) == null) {
                participants.put(i, ParticipantFactory.getParticipant("single",
                        "Participant " + ++genCount,
                        genCount - Tournament.MAX_TOURNAMENT_SIZE - 1));
                        // 3rd argument assigns a generic "id" so that a tournament loaded in progress
                        // keeps the correct name for each generic participant
            }
        }
        seedAdapter.notifyDataSetChanged();
    }

    private void onSeedClicked(int clickedSeed, LinearLayout row) {
        if (isNothingSelected()) {
            selectedSeed = clickedSeed;
            isSeedSelected = true;
            selectedRow = row;
            selectedRow.setSelected(true);
        } else if (isSeedSelected) {
            swapSeeds(selectedSeed, clickedSeed);
        } else if (isSavedParticipantSelected) {
            selectedSeed = clickedSeed;
            isSeedSelected = true;
            assignSeed();
        }
    }

    public void onParticipantClicked(Participant participant, LinearLayout row) {
        if (participant == selectedParticipant) { //If clicked same participant
            clearSelections();
        } else if (!isSeedSelected) { //If participant is clicked before a seed slot
            if (isSavedParticipantSelected) { //If participant was currently selected
                selectedRow.setSelected(false);
            }
            isSavedParticipantSelected = true;
            selectedParticipant = participant;
            selectedRow = row;
            selectedRow.setSelected(true);
        } else if (isSeedSelected) { //If participant is clicked after a seed slot
            isSavedParticipantSelected = true;
            selectedParticipant = participant;
            assignSeed();
        }
    }

    public void assignSeed() {
        if (!areBothSelected() || selectedParticipant == null) { //Check selections are made
            clearSelections();
        } else {
            participantPool.remove(selectedParticipant.getID());
            Participant p = participants.put(selectedSeed, selectedParticipant);
            if (p != null) participantPool.put(p.getID(), p); //Checks if participant was replaced and if so, adds back to pool
            clearSelections();
            updateSeedList();
            updateSavedList();
            if (!areAnyUnassigned()) startButton.setEnabled(true);
        }
    }

    //Swap participants assigned to the two given seeds
    private void swapSeeds(int seed1, int seed2) {
        if (seed1 == seed2) {
            clearSelections();
        } else {
            Participant participantOne = participants.get(seed1);
            Participant participantTwo = participants.put(seed2, participantOne);
            participants.put(seed1, participantTwo);

            clearSelections();
            updateSeedList();
        }
    }

    private void putSeedBackInPool(int seed) {
        Participant p = participants.get(seed);
        participants.put(seed, null);
        participantPool.put(p.getID(), p);
        clearSelections();
        updateSavedList();
        updateSeedList();
    }

    public boolean isNothingSelected() {
        return (!isSavedParticipantSelected && !isSeedSelected);
    }

    public boolean areBothSelected() {
        return (isSavedParticipantSelected && isSeedSelected);
    }

    private void clearSelections() {
        isSeedSelected = false;
        selectedSeed = -1;
        isSavedParticipantSelected = false;
        selectedParticipant = null;
        if (selectedRow != null) selectedRow.setSelected(false);
        selectedRow = null;
    }

    //Check if any seeds have been left unassigned.
    private boolean areAnyUnassigned() {
        for (Integer i : participants.keySet()) {
            if (participants.get(i) == null) return true;
        }
        return false;
    }

    public int saveNewParticipant(ContentValues values) {
        Uri newUri = getActivity().getContentResolver().insert(DatabaseContract.ParticipantTable.CONTENT_URI,
                values);
        updateSavedList();
        return Integer.valueOf(newUri.getLastPathSegment());
    }

    public void updateSavedList() {
        poolAdapter.notifyDataSetChanged();
    }

    public void updateSeedList() {
        seedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
