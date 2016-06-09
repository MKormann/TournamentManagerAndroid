package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.tournaments.Match;
import com.mattkormann.tournamentmanager.tournaments.Tournament;


public class TournamentDisplayFragment extends Fragment {

    private TournamentDisplayListener mCallback;

    private Tournament tournament;
    private View[] matchBracketDisplays;
    private LinearLayout grid;
    private int maxRoundSize;

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



        grid = (LinearLayout)view.findViewById(R.id.tournament_grid);
        //Determine tournament grid display size
        //gridHor = tournament.getNumberOfRounds();
        //gridVer = Integer.highestOneBit(tournament.getSize() - 1) * 2;

        //Create a view for each match
        createMatchBracketViews(inflater);

        //Create a layout for each round and add corresponding views
        for (int i = 1; i <= tournament.getNumberOfRounds(); i++) {
            grid.addView(createViewForRound(i));
        }

        return view;
    }

    //Create a bracket matchup for each match in tournament
    private void createMatchBracketViews(LayoutInflater inflater) {

        matchBracketDisplays = new View[tournament.getMatches().length];

        for (int cnt = 0; cnt < tournament.getMatches().length; cnt++) {

            Match m = tournament.getMatch(cnt);

            //Create layout to hold participants of a single match
            MatchBracketLayout layout = new MatchBracketLayout(getContext(), cnt);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            //Check if match is null (no participants have been determined)
            if (m != null) {
                for (int i = 0; i < m.getParticipantIndices().length; i++) {

                    //Check if team has won and choose style to reflect
                    int bracketStyle = m.getWinner() == i ?
                            R.layout.single_winner_bracket_display :
                            R.layout.single_participant_bracket_display;
                    View bracket = inflater.inflate(bracketStyle, null);

                    //Check whether or not each participant has been determined
                    if (m.getParticipantIndex(i) != Match.NOT_YET_ASSIGNED) {

                        TextView seed = (TextView) bracket.findViewById(R.id.display_seed);
                        seed.setText(m.getParticipantIndex(i));
                        TextView name = (TextView) bracket.findViewById(R.id.display_name);
                        name.setText(tournament.getParticipant(i).getName());
                        TextView stat = (TextView) bracket.findViewById(R.id.display_stat);
                        stat.setText(String.valueOf(m.getSingleStatistic(i)));
                    }

                    layout.addView(bracket);
                }
            }

            layout.setLayoutParams(lp);
            matchBracketDisplays[cnt] = layout;
        }
    }

    private LinearLayout createViewForRound(int round) {

        int roundStart = tournament.getRoundStartDelimiter(round);
        int roundEnd = tournament.getRoundEndDelimiter(round);
        int start = (int)Math.pow(2, round - 1);
        int gap = start * 2;

        LinearLayout roundLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        roundLayout.setOrientation(LinearLayout.VERTICAL);
        roundLayout.setLayoutParams(lp);

        for (int i = roundStart; i <= roundEnd; i++) {
            roundLayout.addView(matchBracketDisplays[i]);
        }

        return roundLayout;
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
        if (view instanceof MatchBracketLayout) {
            MatchBracketLayout mbl = (MatchBracketLayout)view;
            mCallback.displayMatch(mbl.getMatchId());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface TournamentDisplayListener {
        Tournament getCurrentTournament();
        void displayMatch(int matchId);
        void setWinner(int matchId, int winner);
    }
}
