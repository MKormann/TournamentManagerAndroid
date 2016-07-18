package com.mattkormann.tournamentmanager;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

public class StatEntryFragment extends DialogFragment implements TextView.OnEditorActionListener,
        View.OnClickListener {

    private StatEntryFragmentListener mCallback;
    private GridLayout layout;
    private EditText[] editTexts;
    private final String strSeparator = "_,____";

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View statEntryFragment = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_stat_entry, null
        );

        statEntryFragment.findViewById(R.id.stat_entry_1).requestFocus();
        assignEditTexts(statEntryFragment);
        addExistingStats(statEntryFragment);

        Button okButton = (Button)statEntryFragment.findViewById(R.id.stat_OK_button);
        okButton.setOnClickListener(this);

        builder.setView(statEntryFragment);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void assignEditTexts(View view) {

        editTexts = new EditText[getResources().getInteger(R.integer.max_number_of_stats)];
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

    private void addExistingStats(View view) {
        //Check for passed argument of entries
        Bundle args = getArguments();
        String[] statCategories;
        if (args != null && args.containsKey(TournamentSettingsFragment.STAT_CATEGORIES)) {
            statCategories = (String[]) args.get(TournamentSettingsFragment.STAT_CATEGORIES);
            for (int i = 0; i < statCategories.length; i++) {
                if (statCategories[i] != null && !statCategories[i].equals("")) {
                    editTexts[i].setText(statCategories[i]);
                    if (i < editTexts.length -1) {
                        EditText next = (EditText) view.findViewById(editTexts[i].getNextFocusForwardId());
                        next.setEnabled(true);
                        next.requestFocus();
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StatEntryFragmentListener) {
            mCallback = (StatEntryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StatEntryFragmentListener");
        }
    }

    @Override
    public void onClick(View view) {
        mCallback.onOkButtonPressed(collectEntries());
        this.dismiss();
    }

    //Collects all text entered into text boxes
    public String[] collectEntries() {
        String[] statCategories = new String[getResources().getInteger(R.integer.max_number_of_stats)];
        for (int i = 0; i < editTexts.length; i++) {
            statCategories[i] = editTexts[i].getText().toString();
        }
        return statCategories;
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
    private String[] stringToArray(String databaseString) {
        return databaseString.split(strSeparator);
    }

    //Interface to be implemented by MainActivity
    public interface StatEntryFragmentListener {
        void onOkButtonPressed(String[] statCategories);
    }
}
