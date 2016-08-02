package com.mattkormann.tournamentmanager;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.util.TournamentAdapter;

public class ChooseTournamentFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private ChooseTournamentListener mCallback;
    private TournamentAdapter tournamentAdapter;
    private RecyclerView recyclerView;
    private boolean inProgressOnly = false;
    private boolean startAfter = false;

    private static final int TOURNAMENT_LOADER = 0;

    public static final String TOURNAMENT_TYPE = "TOURNAMENT_TYPE";
    public static final String START_AFTER = "START_AFTER";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TOURNAMENT_LOADER, null, this);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View chooseTournamentFragment = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_choose_tournament, null
        );

        builder.setTitle(R.string.select_tournament);

        if (getArguments() != null) {
            inProgressOnly = getArguments().getBoolean(TOURNAMENT_TYPE);
            startAfter = getArguments().getBoolean(START_AFTER);
        }

        recyclerView = (RecyclerView) chooseTournamentFragment.findViewById(R.id.chooseTournamentRecyclerView);
        tournamentAdapter = new TournamentAdapter(new TournamentAdapter.TournamentClickListener() {
            @Override
            public void onClick(int tournamentId) {
                Uri tournamentUri = DatabaseContract.TournamentHistory.buildSavedTournamentUri(tournamentId);
                if (startAfter) mCallback.setCurrentTournamentAndDisplay(tournamentUri);
                else mCallback.swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
                dismiss();
            }
        });
        recyclerView.setAdapter(tournamentAdapter);
        recyclerView.setHasFixedSize(true);

        builder.setView(chooseTournamentFragment);
        return builder.create();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TOURNAMENT_LOADER:
                //Sets arguments to load all tournaments
                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = DatabaseContract.TournamentHistory._ID + " ASC";
                //Checks if looking for in progress tournaments, and loads accordingly
                if (inProgressOnly) {
                    selection = DatabaseContract.TournamentHistory.COLUMN_NAME_FINISHED + "=?";
                    selectionArgs = new String[]{"0"};
                    sortOrder = DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME + " DESC";
                }
                return new CursorLoader(getActivity(),
                        DatabaseContract.TournamentHistory.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        sortOrder);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) mCallback.displayNoTournamentMessage();
        tournamentAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        tournamentAdapter.swapCursor(null);
    }


    public interface ChooseTournamentListener {
        void displayNoTournamentMessage();
        void swapFragment(Fragment fragment);
        void setCurrentTournamentAndDisplay(Uri uri);
        void setCurrentTournament(Tournament tournament);
    }
}