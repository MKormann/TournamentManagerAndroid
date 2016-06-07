package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;
import com.mattkormann.tournamentmanager.tournaments.SqliteTournamentDAO;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;
import com.mattkormann.tournamentmanager.util.TournamentAdapter;

public class ChooseTournamentFragment extends DialogFragment {

    private ChooseTournamentListener mCallback;
    private TournamentAdapter tournamentAdapter;
    private ListView listView;
    private DatabaseHelper mDbHelper;
    private String tournamentType;
    private boolean startAfter = false;

    public static final String TEMPLATES = "TEMPLATES";
    public static final String HISTORY = "HISTORY";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String TOURNAMENT_TYPE = "TOURNAMENT_TYPE";
    public static final String START_AFTER = "START_AFTER";

    public ChooseTournamentFragment() {

    }

    public static ChooseTournamentFragment newInstance() {
        ChooseTournamentFragment fragment = new ChooseTournamentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tournamentType = getArguments().getString(TOURNAMENT_TYPE);
            startAfter = getArguments().getBoolean(START_AFTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_tournament, container, false);

        listView = (ListView)view.findViewById(R.id.choose_tournament_list);
        mDbHelper = new DatabaseHelper(getContext());
        loadTournaments();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idView = (TextView)view.findViewById(R.id.list_tournament_id);
                int clickedId = Integer.valueOf(idView.getText().toString());
                loadSelectedTournament(clickedId);
                dismiss();
            }
        });

        return view;
    }

    private void loadTournaments() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {};
        String tableName = "";
        String selection = "";
        String[] selectionArgs = {};

        switch (tournamentType) {
            case (TEMPLATES) :
                projection = new String[] {
                        DatabaseContract.SavedTournaments._ID,
                        DatabaseContract.SavedTournaments.COLUMN_NAME_NAME,
                        DatabaseContract.SavedTournaments.COLUMN_NAME_SIZE
                };
                tableName = DatabaseContract.SavedTournaments.TABLE_NAME;
                break;
            case (IN_PROGRESS) :
                selection = DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED + "=?";
                selectionArgs = new String[]{"0"};
            case (HISTORY) :
                projection = new String[] {
                        DatabaseContract.TournamentHistory._ID,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME
                };
                tableName = DatabaseContract.TournamentHistory.TABLE_NAME;
                break;
        }

        Cursor c = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        populateList(c);
    }

    private void populateList(Cursor c) {
        c.moveToFirst();

        if (c.getCount() == 0) {
            mCallback.displayNoTournamentMessage();
            dismiss();
        }
        LayoutInflater li = LayoutInflater.from(getContext());

        for (int i = 0; i < c.getCount(); i++) {
            View listItem = li.inflate(R.layout.choose_list_tournament_view, null);

            TextView idView = (TextView) listItem.findViewById(R.id.list_tournament_id);
            TextView nameView = (TextView) listItem.findViewById(R.id.list_tournament_name);
            TextView sizeView = (TextView) listItem.findViewById(R.id.list_tournament_size);
            TextView dateView = (TextView) listItem.findViewById(R.id.list_tournament_date);

            switch(tournamentType) {
                case (TEMPLATES):
                    idView.setText(c.getInt(c.getColumnIndex(DatabaseContract.SavedTournaments._ID)));
                    nameView.setText(c.getString(c.getColumnIndex(DatabaseContract.SavedTournaments.COLUMN_NAME_NAME)));
                    sizeView.setText("Size: " + c.getInt(c.getColumnIndex(DatabaseContract.SavedTournaments.COLUMN_NAME_SIZE)));
                    break;
                case (IN_PROGRESS):
                case (HISTORY):
                    idView.setText(c.getInt(c.getColumnIndex(DatabaseContract.TournamentHistory._ID)));
                    nameView.setText(c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME)));
                    sizeView.setText("Size: " + c.getInt(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE)));
                    dateView.setText(c.getString(c.getColumnIndex(DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME)));
            }

            listView.addView(listItem);
            c.moveToNext();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChooseTournamentListener) {
            mCallback = (ChooseTournamentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChooseTournamentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void loadSelectedTournament(int tournamentId) {
        switch(tournamentType) {
            case (IN_PROGRESS) :
                TournamentDAO tDao = new SqliteTournamentDAO(mDbHelper);
                Tournament tournament = tDao.loadFullTournament(tournamentId);
                mCallback.setCurrentTournament(tournament);
                mCallback.swapFragment(FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_DISPLAY_FRAGMENT));
                break;
            case (TEMPLATES) :
                Bundle args = new Bundle();
                args.putInt(TournamentSettingsFragment.TEMPLATE_ID, tournamentId);
                args.putBoolean(TournamentSettingsFragment.START_TOURNAMENT_AFTER, startAfter);
                mCallback.swapFragment(FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_SETTINGS_FRAGMENT, args));
        }
    }

    public interface ChooseTournamentListener {
        void displayNoTournamentMessage();
        void swapFragment(Fragment fragment);
        void setCurrentTournament(Tournament tournament);
    }
}
