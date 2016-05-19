package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.tournaments.Match;
import com.mattkormann.tournamentmanager.tournaments.Tournament;


public class TournamentDisplayFragment extends Fragment {

    private TournamentDisplayListener mCallback;

    private Tournament tournament;
    private View[] matchBracketDisplays;

    public TournamentDisplayFragment() {
        // Required empty public constructor
    }

    public static TournamentDisplayFragment newInstance(Tournament tournament) {
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
        View view = inflater.inflate(R.layout.fragment_tournament_display, container, false);

        //Set tournament to the current tournament from Main Activity
        tournament = mCallback.getCurrentTournament();

        //Create a bracket matchup for each match in tournament
        matchBracketDisplays = new View[tournament.getMatches().length];
        int cnt = 0;
        for (Match m : tournament.getMatches()) {
            //Create layout to hold participants of a single match
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (m != null) {
                for (int i = 0; i < m.getParticipantIndices().length; i++) {

                    //Check if team has won and choose style to reflect
                    int bracketStyle = m.getWinner() == i ?
                            R.layout.single_winner_bracket_display :
                            R.layout.single_participant_bracket_display;
                    View bracket = inflater.inflate(bracketStyle, null);

                    TextView seed = (TextView) bracket.findViewById(R.id.display_seed);
                    seed.setText(m.getParticipantIndex(i));
                    TextView name = (TextView) bracket.findViewById(R.id.display_name);
                    name.setText(tournament.getParticipant(i).getName());
                    TextView stat = (TextView) bracket.findViewById(R.id.display_stat);
                    stat.setText(String.valueOf(m.getSingleStatistic(i)));

                    layout.addView(bracket);
                }
            }

            layout.setLayoutParams(lp);
            matchBracketDisplays[cnt++] = layout;
        }

        return view;
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

    public void onMatchClick(View view) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface TournamentDisplayListener {
        Tournament getCurrentTournament();
        void displayMatch(int matchId);
    }
}
