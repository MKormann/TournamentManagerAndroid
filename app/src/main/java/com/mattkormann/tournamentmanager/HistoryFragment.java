package com.mattkormann.tournamentmanager;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.util.HistoryAdapter;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {;

    private HistoryFragmentListener mCallback;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    private static final int TOURNAMENT_LOADER = 1;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TOURNAMENT_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        historyAdapter = new HistoryAdapter(new HistoryAdapter.HistoryClickListener() {
            @Override
            public void onClick(final int tournamentId, String tournamentName) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(getString(R.string.loadTournamentTitle));
                alertDialogBuilder.setMessage(getString(R.string.loadTournamentMessage, tournamentName));
                alertDialogBuilder.setPositiveButton(getString(R.string.buttonOK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri tournamentUri = DatabaseContract.TournamentHistory.buildSavedTournamentUri(tournamentId);
                        mCallback.setCurrentTournamentAndDisplay(tournamentUri);
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.buttonCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.show();
            }
        });

        recyclerView.setAdapter(historyAdapter);
        recyclerView.setHasFixedSize(true);

        return view;
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoryFragmentListener) {
            mCallback = (HistoryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HistoryFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void updateList() {
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TOURNAMENT_LOADER:
                return new CursorLoader(getActivity(),
                        DatabaseContract.TournamentHistory.CONTENT_HISTORY_URI,
                        null,
                        null,
                        null,
                        DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME + " DESC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case TOURNAMENT_LOADER:
                historyAdapter.swapCursor(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TOURNAMENT_LOADER:
                historyAdapter.swapCursor(null);
                break;
            default:
                break;
        }
    }


    public interface HistoryFragmentListener {
        void setCurrentTournamentAndDisplay(Uri uri);
    }
}
