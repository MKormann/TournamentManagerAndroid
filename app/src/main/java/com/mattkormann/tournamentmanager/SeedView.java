package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.participants.Participant;

/**
 * Created by Matt on 5/25/2016.
 */
public class SeedView extends TextView {

    private int seed;
    private Participant participant;

    public SeedView(Context context) {
        super(context);
        setText("(Add participant)");
        setClickable(true);
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
        setText(participant.getName());
    }

    public boolean isAssigned() {
        return participant != null;
    }

    public Participant getParticipant() {
        return participant;
    }

    public int getParticipantId() {
        return participant.getID();
    }
}
