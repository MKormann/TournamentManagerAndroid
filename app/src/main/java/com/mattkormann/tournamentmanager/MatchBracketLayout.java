package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.tournaments.Match;

/**
 * Simple extension of LinearLayout to hold an int variable
 * Created by Matt on 5/18/2016.
 */
public class MatchBracketLayout extends LinearLayout {

    private Match match;
    private int matchId;
    private TextView[] seedViews;
    private TextView[] nameViews;
    private TextView[] statViews;
    private int numParticipants;

    public static final LayoutParams lp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);

    public MatchBracketLayout(Context context, Match match) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(lp);

        this.match = match;
        this.numParticipants = match.getParticipantSeeds().length;
        seedViews = new TextView[numParticipants];
        nameViews = new TextView[numParticipants];
        statViews = new TextView[numParticipants];

        createBracketView();
    }

    private void createBracketView() {
        LayoutInflater li = LayoutInflater.from(getContext());
        for (int i = 0; i < numParticipants; i++) {
            View v = li.inflate(R.layout.single_participant_bracket_display, null);
            TextView seed = (TextView) v.findViewById(R.id.display_seed);
            TextView name = (TextView) v.findViewById(R.id.display_name);
            TextView stat = (TextView) v.findViewById(R.id.display_stat);
            seedViews[i] = seed;
            nameViews[i] = name;
            statViews[i] = stat;
            addView(v);
        }
    };

    public void setMatchText(String[] participantNames) {
        for (int i = 0; i < numParticipants; i++) {
            int index = match.getParticipantSeed(i);
            if (index == Match.BYE || index == Match.NOT_YET_ASSIGNED) seedViews[i].setText("-");
                else seedViews[i].setText(String.valueOf(match.getParticipantSeed(i)));
            nameViews[i].setText(participantNames[i]);
            if (match.getStatistics().length != 0) statViews[i]
                    .setText(String.valueOf(match.getSingleStatistic(i)));
        }
        setWinner();
    }

    public void setWinner() {
        for (int i = 0; i < numParticipants; i++) {
            if (match.getWinner() == i) setTypeface(i, Typeface.BOLD);
            else setTypeface(i, Typeface.NORMAL);
        }
    }

    public void setTypeface(int index, int style) {
        seedViews[index].setTypeface(null, style);
        nameViews[index].setTypeface(null, style);
        statViews[index].setTypeface(null, style);
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }
}
