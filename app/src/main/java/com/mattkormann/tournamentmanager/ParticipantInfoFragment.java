package com.mattkormann.tournamentmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;


public class ParticipantInfoFragment extends DialogFragment
        implements TextView.OnEditorActionListener {

    private EditText mEditText;
    private ToggleButton teamToggle;
    private Uri participantUri;
    private String name;
    private boolean isTeam;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreateDialog(bundle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View participantInfoFragment = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_participant_info, null);
        builder.setView(participantInfoFragment);

        builder.setTitle(getString(R.string.enter_info));

        mEditText = (EditText) participantInfoFragment.findViewById(R.id.name_text_box);
        mEditText.requestFocus();
        mEditText.setOnEditorActionListener(this);

        teamToggle = (ToggleButton) participantInfoFragment.findViewById(R.id.teamToggle);

        Bundle arguments = getArguments();
        participantUri = arguments.getParcelable(ParticipantsFragment.PARTICIPANT_URI);

        if (participantUri != null) {
            Cursor c = getActivity().getContentResolver().query(participantUri, null, null, null, null);
            name = c.getString(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME));
            mEditText.setText(name);
            isTeam = c.getInt(c.getColumnIndex(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)) == 1;
            teamToggle.setChecked(isTeam);
        }

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
        name = mEditText.getText().toString();
        isTeam = teamToggle.isChecked();
        if (EditorInfo.IME_ACTION_DONE == actionId && !name.equals("")) {
            //Return info to activity
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_NAME, name);
            values.put(DatabaseContract.ParticipantTable.COLUMN_NAME_IS_TEAM, isTeam ? 1 : 0);
            ParticipantsFragment.ParticipantInfoListener activity = (ParticipantsFragment.ParticipantInfoListener)getActivity();
            activity.onFinishParticipantInformationDialog(participantUri, values);
            this.dismiss();
            return true;
        }
        return false;
    }
}
