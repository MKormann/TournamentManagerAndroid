package com.mattkormann.tournamentmanager.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by Matt on 7/1/2016.
 */
public class SeekBarPreference extends DialogPreference {

    private SeekBar seekBar;

    public SeekBarPreference(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    public View onCreateDialogView() {
        return new View(getContext());
    }
}
