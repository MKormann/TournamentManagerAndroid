package com.mattkormann.tournamentmanager.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.mattkormann.tournamentmanager.R;

public class StatEntryPreference extends DialogPreference {

    public String statCategoriesString;

    public StatEntryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setPositiveButtonText(R.string.buttonOK);
        setNegativeButtonText(R.string.buttonCancel);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            if (defaultValue == null) value = getPersistedString("");
            else value = getPersistedString(defaultValue.toString());
        } else value = defaultValue.toString();

        statCategoriesString = value;
    }

    public void persistStringValue(String value) {
        persistString(value);
    }
}
