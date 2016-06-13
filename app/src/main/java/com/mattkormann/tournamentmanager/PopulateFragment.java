package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.participants.ParticipantFactory;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;
import com.mattkormann.tournamentmanager.tournaments.SqliteTournamentDAO;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;

import java.util.HashMap;
import java.util.Map;

public class PopulateFragment extends Fragment implements View.OnClickListener {

    public static final String TOURNAMENT_SIZE = "TOURNAMENT_SIZE";
    public static final String SEED_TO_ASSIGN = "SEED_TO_ASSIGN";

    private PopulateFragmentListener mCallback;

    private Map<Integer, Participant> participantMap;
    private DatabaseHelper mDbHelper;
    private SeedView[] seedViews;
    private Button startButton;
    private int size;
    private boolean swapping = false;
    private int selectedSeed;

    public PopulateFragment() {
        // Required empty public constructor
    }

    public static PopulateFragment newInstance() {
        PopulateFragment fragment = new PopulateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_populate, container, false);

        participantMap = new HashMap<>();
        seedViews = new SeedView[size];
        mDbHelper = new DatabaseHelper(getContext());

        loadSavedParticipants();
        populateParticipantLayout();

        startButton = (Button) view.findViewById(R.id.start_tournament);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTournamentParticipants();
            }
        });

        return view;
    }

    private void loadSavedParticipants() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.ParticipantTable._ID,
                DatabaseContract.ParticipantTable.COLUMN_NAME_NAME,
                DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM
        };

        String selection = DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM + "=?";

        String[] selectionArgs = {"0"}; //TODO teams

        String sortOrder = DatabaseContract.ParticipantTable._ID + " ASC";

        Cursor c = db.query(
                DatabaseContract.ParticipantTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) { //TODO "single" argument for factory method

            Participant p = ParticipantFactory.getParticipant("single",
                    c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)),
                    c.getInt(c.getColumnIndex(DatabaseContract.ParticipantTable._ID)));

            participantMap.put(p.getID(), p);
            c.moveToNext();
        }
    }

    private void populateParticipantLayout() {

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.choose_participants_layout);

        for (int i = 0; i < size; i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView id = new TextView(getContext());
            id.setText(i + 1);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = .2f;
            lp.gravity = Gravity.CENTER;
            id.setLayoutParams(lp);

            SeedView sv = new SeedView(getContext());
            sv.setSeed(i + 1);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp2.weight = .8f;
            lp2.gravity = Gravity.LEFT;
            sv.setLayoutParams(lp2);
            //Add to views array
            sv.setOnClickListener(new ParticipantClickListener());
            seedViews[i] = sv;

            row.addView(id);
            row.addView(sv);

            LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lpRow.weight = 1.0f;
            row.setLayoutParams(lpRow);

            layout.addView(row);
        }
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof SeedView)) return;
        SeedView sv = (SeedView) v;

    }

    private void setTournamentParticipants() {

        //Confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.finalize_participants_title))
                .setMessage(getString(R.string.finalize_participants_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                })
                .show();
        //After confirmation, check for unassigned participants and re-confirm.
        if (areAnyUnassigned()) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.unassigned_title))
                    .setMessage(getString(R.string.unassigned_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, null)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .show();
        }
        //Create and populate array with participants from each SeedView
        //If participant isn't present, a generic one is created and passed along
        int genCount = 0;
        Participant[] participants = new Participant[size];
        for (int i = 0; i < size; i++) {
            if (seedViews[i].isAssigned()) participants[i] = seedViews[i].getParticipant();
            else {
                participants[i] = ParticipantFactory.getParticipant("single",
                        "Participant " + ++genCount,
                        Participant.GENERIC);
            }
        }

        //Set the participants to the current tournament, save into history, and display
        Tournament tournament = mCallback.getCurrentTournament();
        tournament.setParticipants(participants);
        tournament.assignSeeds();
        TournamentDAO tDao = new SqliteTournamentDAO(mDbHelper);
        tDao.saveFullTournament(tournament);
        mCallback.swapFragment(FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_DISPLAY_FRAGMENT));
    }

    //Swap participants assigned to the two given seeds
    private void swapSeeds(int seed1, int seed2) {
        if (!checkValidSeed(seed1) || !checkValidSeed(seed2)) return;
        if (seed1 == seed2) return;
        Participant temp = seedViews[seed1 - 1].getParticipant();
        seedViews[seed1 - 1].setParticipant(seedViews[seed2 - 1].getParticipant());
        seedViews[seed2 - 1].setParticipant(temp);
    }

    private boolean checkValidSeed(int seed) {
        return (seed > 0 && seed <= size);
    }

    public void assignSeed(int id, int seed) {
        if (participantMap.containsKey(id)) {
            seedViews[seed - 1].setParticipant(participantMap.get(id));
            participantMap.remove(id);
        }
    }

    //Check if any seeds have been left unassigned.
    private boolean areAnyUnassigned() {
        for (SeedView sv : seedViews) {
            if (!sv.isAssigned()) return true;
        }
        return false;
    }

    public int saveNewParticipant(String name, int type) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM, type);

        int newRowId;
        newRowId = (int)db.insert(
                DatabaseContract.ParticipantTable.TABLE_NAME,
                null,
                values
        );

        Participant participant = ParticipantFactory.getParticipant("single", name, newRowId);
        participantMap.put(newRowId, participant);

        return newRowId;
    }

    public int getSelectedSeed() {
        return selectedSeed;
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

    public interface PopulateFragmentListener {
        Tournament getCurrentTournament();
        void swapFragment(Fragment fragment);
        void showChooseParticipantFragment(int seed, Map<Integer, Participant> participantMap);
    }

    class ParticipantClickListener implements View.OnClickListener {

        public void onClick(View v) {
            SeedView sv = (SeedView) v;
            if (!swapping) {
                selectedSeed = sv.getSeed();
                if (!sv.isAssigned()) mCallback.showChooseParticipantFragment(selectedSeed, participantMap);
                else {
                    swapping = true;
                    //TODO Change style of seed to indicate swapping
                }
            }
            else {
                swapSeeds(selectedSeed, sv.getSeed());
                swapping = false;
                selectedSeed = -1;
            }
        }
    }
}
