package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.tournaments.Tournament;

/**
 * Created by Matt on 7/15/2016.
 */
public class SeekBarPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private SeekBar seekBar;
    private TextView sizeDisplay;

    @Override
    public View onCreateDialogView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.seekbar_preference, null);
        sizeDisplay = (TextView)view.findViewById(R.id.sizeDisplay);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        seekBar.setMax(Tournament.MAX_TOURNAMENT_SIZE - Tournament.MIN_TOURNAMENT_SIZE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = seekBar.getProgress() + Tournament.MIN_TOURNAMENT_SIZE;
                displaySize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    //Display size in textview
    private void displaySize(int size) {
        sizeDisplay.setText(String.valueOf(size));
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        SeekBarPreference preference = (SeekBarPreference)getPreference();
        int size = preference.size;
        sizeDisplay.setText(String.valueOf(size));
        seekBar.setProgress(size - Tournament.MIN_TOURNAMENT_SIZE);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SeekBarPreference preference = (SeekBarPreference)getPreference();
            preference.size = seekBar.getProgress() + Tournament.MIN_TOURNAMENT_SIZE;

            preference.persistIntValue(preference.size);
        }
    }
}
