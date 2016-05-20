package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Simple extension of LinearLayout to hold an int variable
 * Created by Matt on 5/18/2016.
 */
public class MatchBracketLayout extends LinearLayout {

    private int matchId;

    public MatchBracketLayout(Context context, int matchId) {
        super(context);
        this.matchId = matchId;
    }

    public int getMatchId() {
        return matchId;
    }
}
