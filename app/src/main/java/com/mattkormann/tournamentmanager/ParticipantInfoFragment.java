package com.mattkormann.tournamentmanager;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class ParticipantInfoFragment extends DialogFragment
        implements TextView.OnEditorActionListener {

    private EditText mEditText;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View participantInfoFragment = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_participant_info, null);
        builder.setView(participantInfoFragment);

        builder.setTitle(getString(R.string.enter_info));

        mEditText = (EditText) participantInfoFragment.findViewById(R.id.name_text_box);
        mEditText.requestFocus();
        mEditText.setOnEditorActionListener(this);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //Hide keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
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
