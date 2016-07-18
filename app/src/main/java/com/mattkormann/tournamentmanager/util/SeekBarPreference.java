package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.mattkormann.tournamentmanager.tournaments.Tournament;

/**
 * Created by Matt on 7/1/2016.
 */
public class SeekBarPreference extends DialogPreference {


    public int size;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, Tournament.MIN_TOURNAMENT_SIZE);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int value;
        if (restoreValue) {
            if (defaultValue == null) value = getPersistedInt(Tournament.MIN_TOURNAMENT_SIZE);
            else value = getPersistedInt((int)defaultValue);
        } else value = (int)defaultValue;

        size = value;
    }

    public void persistIntValue(int value) {
        persistInt(value);
    }


}
