package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.participants.ParticipantFactory;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;
import com.mattkormann.tournamentmanager.util.ParticipantsAdapter;
import com.mattkormann.tournamentmanager.util.ParticipantsSeedAdapter;

import java.util.HashMap;
import java.util.Map;

public class PopulateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TOURNAMENT_SIZE = "TOURNAMENT_SIZE";
    public static final String SEED_TO_ASSIGN = "SEED_TO_ASSIGN";
    private static final int PARTICIPANT_LOADER = 0;

    private PopulateFragmentListener mCallback;
    private ParticipantsAdapter participantsAdapter;
    private ParticipantsSeedAdapter seedAdapter;

    private Map<Integer, Participant> participants;
    private Button startButton;
    private Button fillButton;
    private int size;
    private boolean isSeedSelected = false;
    private int selectedSeed;
    private boolean isSavedParticipantSelected = false;
    private String selectedParticipantName;
    private int selectedParticipantId;

    public PopulateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PARTICIPANT_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            size = getArguments().getInt(TOURNAMENT_SIZE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_populate, container, false);

        RecyclerView seedRecyclerView = (RecyclerView) view.findViewById(R.id.seedRecyclerView);
        RecyclerView savedRecyclerView = (RecyclerView) view.findViewById(R.id.savedRecyclerView);

        participantsAdapter = new ParticipantsAdapter(new ParticipantsAdapter.ParticipantClickListener() {
            @Override
            public void onClick(String name, int participantId) {
                onParticipantClicked(name, participantId);
            }
        });

        savedRecyclerView.setAdapter(participantsAdapter);
        savedRecyclerView.setHasFixedSize(true);

        seedAdapter = new ParticipantsSeedAdapter(new ParticipantsSeedAdapter.SeedClickListener() {
            @Override
            public void onClick(int seed, Participant participant) {
                onSeedClicked(seed, participant);
            }
        });

        seedRecyclerView.setAdapter(seedAdapter);
        seedRecyclerView.setHasFixedSize(true);

        participants = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            participants.put(i, null);
        }

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

    private void onSeedClicked(int clickedSeed, Participant participant) {
        if (isSeedSelected) {
            // Seed is clicked after another is currently selected
            swapSeeds(selectedSeed, clickedSeed);
        } else if (isSavedParticipantSelected) {
            // Seed is clicked after a saved participant is currently selected
            assignSeed();
        } else {
            // Seed is only thing currently selected
            selectedSeed = clickedSeed;
            isSeedSelected = true;
            highlightSelectedSeed(clickedSeed, true);
        }
    }

    public void onParticipantClicked(String name, int participantId) {
        if (isSavedParticipantSelected && participantId == selectedParticipantId) {
            //Clicked the participant already selected
            isSavedParticipantSelected = false;
            selectedParticipantName = "";
            selectedParticipantId = -1;
        } else {
            //Clicked any other participant
            isSavedParticipantSelected= true;
            selectedParticipantName = name;
            selectedParticipantId = participantId;
            if (isSeedSelected) {
                //Swap if seed selected
                assignSeed();
            }
            //highlightSelectedParticipant(int participantId,boolean highlight) //TODO ***************
        }
    }

    public void assignSeed() {
        Participant p = ParticipantFactory.getParticipant("single",
                selectedParticipantName, selectedParticipantId);
        participants.put(selectedSeed, p);
        isSeedSelected = false;
        selectedSeed = -1;
        isSavedParticipantSelected = false;
        selectedParticipantName = "";
        selectedParticipantId = -1;

        updateSeedList();

        if (!areAnyUnassigned()) startButton.setEnabled(true);
    }

    private void highlightSelectedSeed(int clickedSeed, boolean highlight) {
        //TODO STYLE CHANGES WHEN SELECTED  ********
    }

    private void highlightSelectedParticipant(int clickedParticipant, boolean highlight) {
        //TODO STYLE CHANGES WHEN SELECTED  ********
    }

    //Swap participants assigned to the two given seeds
    private void swapSeeds(int seed1, int seed2) {
        if (seed1 == seed2) {
            highlightSelectedSeed(seed1, false);
            return;
        }
        Participant participantOne = participants.get(seed1);
        Participant participantTwo = participants.put(seed2, participantOne);
        participants.put(seed1, participantTwo);

        isSeedSelected = false;
        selectedSeed = -1;

        updateSeedList();
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
        participantsAdapter.notifyDataSetChanged();
    }

    public void updateSeedList() {
        seedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PopulateFragmentListener) {
            mCallback = (PopulateFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PopulateFragmentListener");
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

    public interface PopulateFragmentListener {
        Tournament getCurrentTournament();
        void swapFragment(Fragment fragment);
        void showChooseParticipantFragment(int seed, Map<Integer, Participant> participantMap);
    }

}
