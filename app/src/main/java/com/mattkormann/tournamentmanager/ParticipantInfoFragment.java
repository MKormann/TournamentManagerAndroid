package com.mattkormann.tournamentmanager;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class ParticipantInfoFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText mEditText;

    public ParticipantInfoFragment() {
        // Required empty public constructor
    }

    public static ParticipantInfoFragment newInstance() {
        ParticipantInfoFragment fragment = new ParticipantInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participant_info, container, false);
        mEditText = (EditText) view.findViewById(R.id.name_text_box);
        getDialog().setTitle("Participant Info");

        //Show keyboard
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);
        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            //Return text to activity
            ParticipantsFragment.ParticipantInfoListener activity = (ParticipantsFragment.ParticipantInfoListener)getActivity();
            activity.onFinishParticipantInformationDialog(mEditText.getText().toString(),
                    getArguments().getInt(ParticipantsFragment.TYPE_TO_DISPLAY));
            this.dismiss();
            return true;
        }
        return false;
    }
}
