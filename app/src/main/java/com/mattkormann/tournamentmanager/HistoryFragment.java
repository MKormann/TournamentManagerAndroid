package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.sql.DatabaseHelper;

public class HistoryFragment extends Fragment {

    private OnFragmentInteractionListener mCallback;
    private DatabaseHelper mDbHelper;

    public static final String TOURNAMENT_HISTORY_PREFIX = "TH.";
    public static final String PARTICIPANT_PREFIX = "P.";

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mDbHelper = new DatabaseHelper(getContext());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mCallback = (OnFragmentInteractionListener) context;
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

    //Executes the SQL statement to retrieve tournament data from table
    private Cursor retrieveTournamentData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseContract.TournamentHistory.TABLE_NAME +
                " LEFT OUTER JOIN " + DatabaseContract.ParticipantTable.TABLE_NAME + " ON "
                );
//TODO
        String query = "SELECT " +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory._ID + "," +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME + "," +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE + "," +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory.COLUMN_NAME_WINNER_ID + "," +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory.COLUMN_NAME_RUNNER_UP_ID + "," +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED + "," +
                TOURNAMENT_HISTORY_PREFIX + DatabaseContract.TournamentHistory.COLUMN_NAME_FINISH_TIME + "," +
                PARTICIPANT_PREFIX + DatabaseContract.ParticipantTable.COLUMN_NAME_NAME;

        String[] projection = {
                DatabaseContract.TournamentHistory._ID,
                DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME,
                DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE,
                DatabaseContract.TournamentHistory.COLUMN_NAME_WINNER_ID,
                DatabaseContract.TournamentHistory.COLUMN_NAME_RUNNER_UP_ID,
                DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED,
                DatabaseContract.TournamentHistory.COLUMN_NAME_FINISH_TIME
        };

        String selection = DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED + "=?";

        String[] selectionArgs = {"1"};

        String sortOrder = DatabaseContract.ParticipantTable._ID + " DESC";

        return db.query(
                DatabaseContract.TournamentHistory.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
