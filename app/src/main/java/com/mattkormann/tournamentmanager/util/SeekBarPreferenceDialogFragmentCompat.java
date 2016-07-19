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
    private int seekBarSize = Tournament.MAX_TOURNAMENT_SIZE - Tournament.MIN_TOURNAMENT_SIZE;
    private int seekBarQuarter = seekBarSize / 4;

    @Override
    public View onCreateDialogView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.seekbar_preference, null);
        sizeDisplay = (TextView)view.findViewById(R.id.sizeDisplay);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        seekBar.setMax(seekBarSize + seekBarQuarter);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = getProportionalSeekBarSize(seekBar.getProgress()) + Tournament.MIN_TOURNAMENT_SIZE;
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
        seekBar.setProgress(getSeekBarProgressFromSize(size - Tournament.MIN_TOURNAMENT_SIZE));
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SeekBarPreference preference = (SeekBarPreference)getPreference();
            preference.size = getProportionalSeekBarSize(seekBar.getProgress()) + Tournament.MIN_TOURNAMENT_SIZE;

            preference.persistIntValue(preference.size);
        }
    }

    //Returns a value so that the first 1/4 of size options scroll twice as slow as the other 3/4
    //Improvement in GUI that assumes users will much more frequently choose tournaments on the smaller end of allowable range
    private int getProportionalSeekBarSize(int progress) {
        if (progress <= (seekBarQuarter * 2)) {
            return progress / 2;
        } else {
            return progress - seekBarQuarter;
        }
    }

    private int getSeekBarProgressFromSize(int size) {
        if (size < seekBarQuarter) {
            return size * 2;
        } else return seekBarQuarter + size;
    }
}
