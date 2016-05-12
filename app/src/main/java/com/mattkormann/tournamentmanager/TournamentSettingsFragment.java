package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mattkormann.tournamentmanager.tournaments.Tournament;

public class TournamentSettingsFragment extends Fragment {

    private TournamentSettingsListener mCallback;

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

        // Create a spinner for choosing tournament size
        int numChoices = Tournament.MAX_TOURNAMENT_SIZE - Tournament.MIN_TOURNAMENT_SIZE + 1;
        Integer[] sizes = new Integer[numChoices];
        for (int i = 0; i < numChoices ; i++) {
            sizes[i] = i + Tournament.MIN_TOURNAMENT_SIZE;
        }
        ArrayAdapter<Integer> integerArrayAdapter = new ArrayAdapter<Integer>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, sizes);
        Spinner spinner = (Spinner)container.findViewById(R.id.tournament_size_spinner);
        spinner.setAdapter(integerArrayAdapter);

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

    public interface TournamentSettingsListener {
        void onFragmentInteraction(Uri uri);
    }
}
