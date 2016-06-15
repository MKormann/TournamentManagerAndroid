package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.mattkormann.tournamentmanager.sql.DatabaseHelper;
import com.mattkormann.tournamentmanager.tournaments.SqliteTournamentDAO;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;

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
    public static final String START_TOURNAMENT_AFTER = "START_TOURNAMENT_AFTER";
    public static final String TEMPLATE_ID = "TEMPLATE_ID";

    private int templateId;
    private boolean startTournamentAfter;
    private TournamentDAO tDao;

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
                alertDialogBuilder.setMessage(templateId == TournamentDAO.NEW_TOURNAMENT_TEMPLATE ?
                        getString(R.string.save_tournament_alert_dialog) :
                        getString(R.string.update_tournament_alert_dialog));
                alertDialogBuilder.setPositiveButton(getString(R.string.buttonOK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int savedId = saveTournamentTemplate(view);
                        if (startTournamentAfter) {
                            Tournament tournament = tDao.loadTournamentFromTemplate(savedId);
                            mCallback.setCurrentTournament(tournament);
                        };
                        mCallback.advanceFromSettings(startTournamentAfter);
                    }
                });
                alertDialogBuilder.setNeutralButton(getString(R.string.buttonCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.show();

            }
        });
        tButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tButton.isChecked()) mCallback.displayStatEntry(statCategories);
            }
        });

        mDbHelper = new DatabaseHelper(getContext());
        tDao = new SqliteTournamentDAO(mDbHelper);

        // Create a number picker for choosing tournament size
        np.setMinValue(Tournament.MIN_TOURNAMENT_SIZE);
        np.setMaxValue(Tournament.MAX_TOURNAMENT_SIZE);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        Bundle args = getArguments();
        if (args != null) {
            templateId = args.getInt(TEMPLATE_ID);
            if (templateId != TournamentDAO.NEW_TOURNAMENT_TEMPLATE)
                loadTournamentSettings(templateId);
            startTournamentAfter = args.getBoolean(START_TOURNAMENT_AFTER);
        } else {
            templateId = TournamentDAO.NEW_TOURNAMENT_TEMPLATE;
        }

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

    public void loadTournamentSettings(int tournamentId) {

        if (tournamentId == TournamentDAO.NEW_TOURNAMENT_TEMPLATE) return;

        Tournament tournament = tDao.loadTournamentFromTemplate(tournamentId);

        editName.setText(tournament.getName());
        np.setValue(tournament.getSize());
        int spinnerIndex = tournament.isDoubleElimination() ? 1 : 0;
        eSpinner.setSelection(spinnerIndex);
        teamSpinner.setSelection(tournament.getTeamSize() - 1);
        tButton.setChecked(tournament.isStatTrackingEnabled());
        if (tButton.isChecked()) setStatCategories(tournament.getStatCategories());
    }

    private int saveTournamentTemplate(View view) {

        //Collect values from fields
        String name = editName.getText().toString();
        int size = np.getValue();
        String elimType = eSpinner.getSelectedItem().toString();
        int doubleElim = elimType.equals("Double") ? 1 : 0;
        int teamSize = Integer.valueOf(teamSpinner.getSelectedItem().toString());
        int useStats = tButton.isChecked() ? 1 : 0;

        return tDao.saveTournamentTemplate(templateId, name, size, teamSize, doubleElim, useStats, statCategories);
    }

    public void setStatCategories(String[] statCategories) {
        this.statCategories = statCategories;
    }

    public interface TournamentSettingsListener {
        void advanceFromSettings(boolean startTournament);
        void displayStatEntry(String[] statCategories);
        void setCurrentTournament(Tournament tournament);
    }
}
