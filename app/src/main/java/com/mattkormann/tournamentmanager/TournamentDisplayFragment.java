package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TournamentDisplayFragment extends Fragment {

    private TournamentDisplayListener mCallback;

    public TournamentDisplayFragment() {
        // Required empty public constructor
    }

    public static TournamentDisplayFragment newInstance(String param1, String param2) {
        TournamentDisplayFragment fragment = new TournamentDisplayFragment();
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
        return inflater.inflate(R.layout.fragment_tournament_display, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TournamentDisplayListener) {
            mCallback = (TournamentDisplayListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TournamentDisplayListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface TournamentDisplayListener {
        void onFragmentInteraction(Uri uri);
    }
}
