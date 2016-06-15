package com.mattkormann.tournamentmanager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

    public StatEntryFragment() {
        // Required empty public constructor
    }

    public static StatEntryFragment newInstance() {
        StatEntryFragment fragment = new StatEntryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat_entry, container, false);

        editTexts = new EditText[getResources().getInteger(R.integer.max_number_of_stats)];

        getDialog().setTitle("Stat Categories");
        getDialog().setCanceledOnTouchOutside(false);

        view.findViewById(R.id.stat_entry_1).requestFocus();

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
        Button okButton = (Button)view.findViewById(R.id.stat_OK_button);
        okButton.setOnClickListener(this);

        return view;
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
            if (view.getId() == R.id.stat_entry_8) {
                getView().findViewById(R.id.stat_OK_button).requestFocus();
            }
            else {
                EditText next = (EditText) getView().findViewById(view.getNextFocusForwardId());
                next.setEnabled(true);
                next.requestFocus();
            }

            //Minimize keyboard
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        return true;
    }

    //Interface to be implemented by MainActivity
    public interface StatEntryFragmentListener {
        void onOkButtonPressed(String[] statCategories);
    }
}
