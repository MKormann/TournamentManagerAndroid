package com.mattkormann.tournamentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;
import com.mattkormann.tournamentmanager.tournaments.Tournament;

public class TournamentSettingsFragment extends Fragment {

    private TournamentSettingsListener mCallback;
    private DatabaseHelper mDbHelper;
    private Button generateButton;
    private ToggleButton tButton;
    private NumberPicker np;
    private Spinner eSpinner;
    private Spinner teamSpinner;
    private EditText editName;
    private String[] statCategories;

    public static final String STAT_CATEGORIES = "STAT_CATEGORIES";

    public TournamentSettingsFragment() {
        // Required empty public constructor
    }

    public static TournamentSettingsFragment newInstance() {
        TournamentSettingsFragment fragment = new TournamentSettingsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tournament_settings, container, false);

        generateButton = (Button)view.findViewById(R.id.generate_tournament);
        tButton = (ToggleButton)view.findViewById(R.id.stat_tracking_toggle);
        eSpinner = (Spinner)view.findViewById(R.id.elimination_type_spinner);
        teamSpinner = (Spinner)view.findViewById(R.id.team_size_spinner);
        editName = (EditText)view.findViewById(R.id.tournament_name_text);
        np = (NumberPicker)view.findViewById(R.id.tournament_size_picker);
        statCategories = new String[getContext().getResources().getInteger(R.integer.max_number_of_stats)];

        // Set listeners for buttons
        generateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(getString(R.string.button_create_tournament));
                alertDialogBuilder.setMessage(getString(R.string.create_tournament_alert_dialog));
                alertDialogBuilder.setPositiveButton(getString(R.string.buttonOK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveTournament(view);
                    }
                });
                alertDialogBuilder.setNeutralButton(getString(R.string.buttonCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.show();
                mCallback.generateTournament();
            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tButton.isChecked()) mCallback.displayStatEntry(statCategories);
            }
        });

        mDbHelper = new DatabaseHelper(getContext());

        // Create a number picker for choosing tournament size
        np.setMinValue(Tournament.MIN_TOURNAMENT_SIZE);
        np.setMaxValue(Tournament.MAX_TOURNAMENT_SIZE);
        np.setWrapSelectorWheel(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TournamentSettingsListener) {
            mCallback = (TournamentSettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TournamentSettingsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    // Save new Tournament schema to the SavedTournaments SQL table
    public void saveTournament(View view) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_NAME, editName.getText().toString());
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_SIZE, np.getValue());

        String elimType = eSpinner.getSelectedItem().toString();
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_DOUBLE_ELIM, elimType.equals("Double") ? 1 : 0);

        int teamSize = Integer.valueOf(teamSpinner.getSelectedItem().toString());
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_TEAM_SIZE, teamSize);

        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_USE_STATS, tButton.isChecked() ? 1 : 0);
        values.put(DatabaseContract.SavedTournaments.COLUMN_NAME_STATS_ARRAY, " ");//TODO

        long newRowId;
        newRowId = db.insert(
                DatabaseContract.SavedTournaments.TABLE_NAME,
                null,
                values
        );
    }

    public void setStatCategories(String[] statCategories) {
        this.statCategories = statCategories;
    }

    public interface TournamentSettingsListener {
        void generateTournament();
        void displayStatEntry(String[] statCategories);
    }
}
