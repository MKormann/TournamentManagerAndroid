package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import com.mattkormann.tournamentmanager.tournaments.Match;
import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.util.SeedFactory;


public class TournamentDisplayFragment extends Fragment {

    private TournamentDisplayListener mCallback;

    private Tournament tournament;
    private SeedFactory sf;
    private int[] seedsInMatchOrder;
    private MatchBracketHolder[] matchBracketHolders;
    private GridLayout grid;
    private int maxRoundSize;
    private int participantsPerMatch;
    private int width;
    private int height;
    private int margin;

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
        participantsPerMatch = tournament.getMatch(0).getParticipantSeeds().length;


        //Retrieve seeds in match order
        sf = new SeedFactory(tournament.getSize());
        seedsInMatchOrder = sf.getSeedsInMatchOrder();
        int maxRoundSize = sf.getMaxRoundSize();

        //Obtain reference to grid layout and set rows and columns
        grid = (GridLayout) view.findViewById(R.id.tournament_grid);
        grid.setColumnCount(tournament.getNumberOfRounds());
        grid.setRowCount(maxRoundSize * participantsPerMatch);

        width = (int)getResources().getDimension(R.dimen.match_bracket_display_width);
        height = (int)getResources().getDimension(R.dimen.match_bracket_display_height);
        margin = (int)getResources().getDimension(R.dimen.grid_spacing);

        //Create a view for each match
        createMatchBracketViews();

        addMatchBracketViewsToGrid();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tournamentdisplay, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.save_tournament) :
                mCallback.saveTournament();
                Toast.makeText(getContext(),
                        getString(R.string.tournamentSaved), Toast.LENGTH_LONG).show();
                break;
            case (R.id.exit_tournament) :
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(getString(R.string.exitTournamentTitle));
                alertDialogBuilder.setMessage(getString(R.string.exitTournamentMessage));
                alertDialogBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.saveTournament();
                        Toast.makeText(getContext(),
                                getString(R.string.tournamentSaved), Toast.LENGTH_LONG).show();
                        mCallback.exitToMain();
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.exitToMain();
                    }
                });
                alertDialogBuilder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Create a bracket matchup for each match in tournament
    private void createMatchBracketViews() {

        int numMatches = tournament.getMatches().length;

        matchBracketHolders = new MatchBracketHolder[numMatches];

        for (int cnt = 0; cnt < numMatches; cnt++) {

            Match m = tournament.getMatch(cnt);
            MatchBracketHolder mbl = createSingleMatchBracket(m);
            mbl.setMatchId(cnt);
            matchBracketHolders[cnt] = mbl;
            updateSingleMatchInfo(mbl.getMatchId());
        }
    }

    private MatchBracketHolder createSingleMatchBracket(Match m) {
        //Create layout to hold participants of a single match
        final MatchBracketHolder layout = new MatchBracketHolder(getContext(), m);

        //If tournament loaded is already over, or match is not populated, do not set match as clickable.
        if (tournament.isOver()) return layout;

        // Set each bracket view as clickable to display MatchDisplayFragment
        for (View v : layout.getParticipantViews()) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i : layout.getMatch().getParticipantSeeds()) {
                        if (i == Match.BYE || i == Match.NOT_YET_ASSIGNED)
                            return;
                    }
                    mCallback.displayMatch(layout.getMatchId());
                }
            });
        }

        return layout;
    }

    private void addMatchBracketViewsToGrid() {
        int round = 1;

        //Modifier for the row and gap values below if the first "full" round is round 2 instead of 1.
        int prelimMod = 0;

        //Check if tournament has a preliminary round, which will have a different layout
        if (sf.hasPrelimRound()) {
            prelimMod = -1;
            int roundTwoStart = tournament.getRoundStartDelimiter(2);
            for (int index = 0; index <= tournament.getRoundEndDelimiter(1); index++) {
                MatchBracketHolder mbh = matchBracketHolders[index];
                int nextMatchId = mbh.getMatch().getNextMatchId();
                int row = (nextMatchId - roundTwoStart) * participantsPerMatch;
                for (View v: mbh.getParticipantViews()) {
                    addViewToSpecificCell(v, row++, 0);
                }
            }
            //Resume normal layout from round 2
            round = 2;
        }

        for (int i = round; i <= tournament.getNumberOfRounds(); i++) {
            int roundStart = tournament.getRoundStartDelimiter(i);
            int roundEnd = tournament.getRoundEndDelimiter(i);
            int row = (int)Math.pow(participantsPerMatch, i + prelimMod - 1) - 1;
            int gap = row * 2;
            for (int index = roundStart; index <= roundEnd; index++) {
                MatchBracketHolder mbh = matchBracketHolders[index];
                for (View v : mbh.getParticipantViews()) {
                    addViewToSpecificCell(v, row++, i - 1);
                }
                row += gap;
            }
        }
    }

    //Add given view to the Grid Layout at the row and column provided
    private void addViewToSpecificCell(View view, int row, int col) {
        GridLayout.LayoutParams cell = new GridLayout.LayoutParams(
                GridLayout.spec(row),
                GridLayout.spec(col)
        );
        cell.width = width;
        cell.height = height;
        cell.leftMargin = margin;
        cell.rightMargin = margin;
        grid.addView(view, cell);
    }

    public void updateMatchInfo(int matchId) {
        while (matchId != Match.BYE) {
            updateSingleMatchInfo(matchId);
            Match m = tournament.getMatch(matchId);
            int nextMatchId = m.getNextMatchId();
            matchId = nextMatchId;
        }
        checkIfTournamentIsOver();
    }

    private void updateSingleMatchInfo(int matchId) {
        MatchBracketHolder mbl = matchBracketHolders[matchId];
        Match m = tournament.getMatch(matchId);
        String[] names = getMatchParticipantNames(m);
        mbl.setMatchText(names);
        mbl.setWinner();
    }

    private String[] getMatchParticipantNames(Match m) {
        int[] seeds = m.getParticipantSeeds();
        String[] names = new String[seeds.length];
        for (int i = 0; i < seeds.length; i++) {
            if (seeds[i] == Match.NOT_YET_ASSIGNED) {
                names[i] = "_____";
            }
            else if (seeds[i] == Match.BYE) {
                names[i] = "BYE";
            } else
                names[i] = tournament.getParticipant(seeds[i]).getName();
        }
        return names;
    }

    private void checkIfTournamentIsOver() {
        if (tournament.isOver()) displayFinishDialog();
    }

    private void displayFinishDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.over_title)
                .setMessage(R.string.over_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.saveTournament();
                        mCallback.exitToMain();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                })
                .show();
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
        void saveTournament();
        void exitToMain();
    }

}
