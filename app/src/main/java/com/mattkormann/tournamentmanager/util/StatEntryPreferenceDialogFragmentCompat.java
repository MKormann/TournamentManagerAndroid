package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.tournaments.Tournament;

/**
 * Created by Matt on 7/18/2016.
 */
public class StatEntryPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
        implements TextView.OnEditorActionListener {

    private GridLayout layout;
    private EditText[] editTexts;
    private String[] statCategoriesArray;
    private int maxStats;
    private final String strSeparator = "_,____";

    @Override
    public View onCreateDialogView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_stat_entry, null);

        maxStats = getContext().getResources().getInteger(R.integer.max_number_of_stats);
        statCategoriesArray = new String[maxStats];

        view.findViewById(R.id.stat_entry_1).requestFocus();

        return view;
    }

    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);

        StatEntryPreference preference = (StatEntryPreference)getPreference();
        String statCategories = preference.statCategoriesString;

        assignEditTexts(view);
        addExistingStats(view, statCategories);
    }

    private void assignEditTexts(View view) {

        editTexts = new EditText[maxStats];
        layout = (GridLayout)view.findViewById(R.id.stat_grid_layout);

        //Add EditTexts to an array and disable
        int count = 0;
        for (int i = 0; i < layout.getChildCount(); i++) {
            if (layout.getChildAt(i) instanceof EditText) {
                EditText text = (EditText) layout.getChildAt(i);
                text.setOnEditorActionListener(this);
                editTexts[count++] = text;
                if (text.getId() != R.id.stat_entry_1) text.setEnabled(false);
            }
        }
    }

    private void addExistingStats(View view, String statCategories) {

        if (statCategories == null) return;
        statCategoriesArray = stringToArray(statCategories);
        for (int i = 0; i < statCategoriesArray.length; i++) {
            if (statCategoriesArray[i] != null && !statCategoriesArray[i].equals("")) {
                editTexts[i].setText(statCategoriesArray[i]);
                if (i < editTexts.length -1) {
                    EditText next = (EditText) view.findViewById(editTexts[i].getNextFocusForwardId());
                    next.setEnabled(true);
                    next.requestFocus();
                }
            }
        }
    }

    //Converts array into a string to store in database
    private String arrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str += array[i].toString();
            if (i < array.length - 1) {
                str += strSeparator;
            }
        }
        return str;
    }

    //Converts database string into array object
    private String[] stringToArray(String statCategories) {
        return statCategories.split(strSeparator);
    }

    //Collects all text entered into text boxes
    public void collectEntries() {
        for (int i = 0; i < editTexts.length; i++) {
            statCategoriesArray[i] = editTexts[i].getText().toString();
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            collectEntries();

            StatEntryPreference preference = (StatEntryPreference)getPreference();
            String statCategories = arrayToString(statCategoriesArray);

            preference.statCategoriesString = statCategories;
            preference.persistStringValue(preference.statCategoriesString);
        }
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            View parent = (View)view.getParent();
            if (view.getId() == R.id.stat_entry_8) {
                parent.findViewById(R.id.stat_OK_button).requestFocus();
            }
            else {
                EditText next = (EditText) parent.findViewById(view.getNextFocusForwardId());
                next.setEnabled(true);
                next.requestFocus();
            }

            //Minimize keyboard
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        return true;
    }

}
