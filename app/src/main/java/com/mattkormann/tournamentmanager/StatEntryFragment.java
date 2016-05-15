package com.mattkormann.tournamentmanager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

public class StatEntryFragment extends DialogFragment implements TextView.OnEditorActionListener,
        View.OnClickListener {

    private StatEntryFragmentListener mCallback;
    private GridLayout layout;

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

        layout = (GridLayout)view.findViewById(R.id.stat_grid_layout);

        for (int i = 0; i < layout.getChildCount(); i++) {
            if (layout.getChildAt(i) instanceof EditText) {
                EditText text = (EditText) layout.getChildAt(i);
                text.setOnEditorActionListener(this);
                if (text.getId() != R.id.stat_entry_1) text.setEnabled(false);
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
        mCallback.onOkButtonPressed();
        this.dismiss();
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        return true;
    }

    //Interface to be implemented by MainActivity
    public interface StatEntryFragmentListener {
        void onOkButtonPressed();
    }
}
