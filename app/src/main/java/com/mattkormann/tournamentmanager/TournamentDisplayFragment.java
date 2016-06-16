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
import com.mattkormann.tournamentmanager.util.SeedFactory;


public class TournamentDisplayFragment extends Fragment {

    private TournamentDisplayListener mCallback;

    private Tournament tournament;
    private int[] seedsInMatchOrder;
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

        SeedFactory sf = new SeedFactory(tournament.getSize());
        seedsInMatchOrder = sf.getSeedsInMatchOrder();

        grid = (LinearLayout)view.findViewById(R.id.tournament_grid);
        grid.setWeightSum(1f);
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

        int numMatches = tournament.getMatches().length;

        matchBracketDisplays = new View[numMatches];

        for (int cnt = 0; cnt < numMatches; cnt++) {

            Match m = tournament.getMatch(cnt);

            //Create layout to hold participants of a single match
            MatchBracketLayout layout = new MatchBracketLayout(getContext(), cnt);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);


            for (int i = 0; i < m.getParticipantIndices().length; i++) {

                //Check if team has won and choose style to reflect
                int bracketStyle = m.getWinner() == i ?
                        R.layout.single_winner_bracket_display :
                        R.layout.single_participant_bracket_display;
                View bracket = inflater.inflate(bracketStyle, null);

                int index = m.getParticipantIndex(i);
                TextView seed = (TextView) bracket.findViewById(R.id.display_seed);
                TextView name = (TextView) bracket.findViewById(R.id.display_name);
                TextView stat = (TextView) bracket.findViewById(R.id.display_stat);

                if (index != Match.NOT_YET_ASSIGNED && index != Match.BYE) {
                    seed.setText(String.valueOf(index + 1));
                    name.setText(tournament.getParticipant(index).getName());
                    if (tournament.isStatTrackingEnabled()) stat.setText(String.valueOf(m.getSingleStatistic(i)));
                } else if (index == Match.NOT_YET_ASSIGNED) {
                    seed.setText("_") ;
                    name.setText("_____");
                    if (tournament.isStatTrackingEnabled()) stat.setText(String.valueOf(m.getSingleStatistic(i)));
                } else if (index == Match.BYE) {
                    seed.setText("-");
                    name.setText("BYE");
                    if (tournament.isStatTrackingEnabled()) stat.setText("-");
                }
                layout.addView(bracket);
            }

            layout.setLayoutParams(lp);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MatchBracketLayout mbl = (MatchBracketLayout)v;
                    Match m = tournament.getMatch(mbl.getMatchId());
                    for (int i : m.getParticipantIndices()) {
                        if (i == Match.BYE || i == Match.NOT_YET_ASSIGNED)
                            return;
                    }
                    mCallback.displayMatch(mbl.getMatchId());
                }
            });
            matchBracketDisplays[cnt] = layout;
        }
    }

    private LinearLayout createViewForRound(int round) {

        int roundStart = tournament.getRoundStartDelimiter(round);
        int roundEnd = tournament.getRoundEndDelimiter(round);
        int roundSize = roundEnd - roundStart + 1;

        LinearLayout roundLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT);
        roundLayout.setOrientation(LinearLayout.VERTICAL);
        Float roundWeight = 1f / tournament.getNumberOfRounds();
        lp.weight = roundWeight;
        roundLayout.setLayoutParams(lp);

        //Create new layout parameters to assign each view a height of 0, and a weight equivalent
        LinearLayout.LayoutParams matchLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
        );
        matchLayout.weight = .1f;

        //Add views to Round Layout
        //Gap and getSpace method used to space out display appropriately
        int gap = (int)Math.pow(2, round - 1) - 1;
        for (int i = roundStart; i <= roundEnd; i++) {

            if (round > 1 && i == roundStart) {
                roundLayout.addView(getSpace(gap / 2));
            }
            matchBracketDisplays[i].setLayoutParams(matchLayout);
            roundLayout.addView(matchBracketDisplays[i]);
            if (round > 1 && i == roundEnd) {
                getSpace(gap / 2);
            } else if (round > 1) {
                getSpace(gap);
            }
        }

        return roundLayout;
    }

    //
    private LinearLayout getSpace(float weight) {
        LinearLayout space = new LinearLayout(getContext());
        LinearLayout.LayoutParams lpSpace = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
        );
        lpSpace.weight = weight;
        space.setLayoutParams(lpSpace);
        return space;
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
        Tournament getCurrentTournament();
        void displayMatch(int matchId);
        void setWinner(int matchId, int winner);
    }
}
