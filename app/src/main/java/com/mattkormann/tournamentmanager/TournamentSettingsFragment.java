package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mattkormann.tournamentmanager.tournaments.Tournament;
import com.mattkormann.tournamentmanager.util.SeekBarPreference;
import com.mattkormann.tournamentmanager.util.SeekBarPreferenceDialogFragmentCompat;
import com.mattkormann.tournamentmanager.util.StatEntryPreference;
import com.mattkormann.tournamentmanager.util.StatEntryPreferenceDialogFragmentCompat;

/**
 * Created by Matt on 6/22/2016.
 */
public class TournamentSettingsFragment extends PreferenceFragmentCompat {

    Preference switchPref;
    Preference statEntryPref;
    SharedPreferences sharedPreferences;
    boolean hasACurrentTournament;
    private TournamentSettingsListener mCallback;

    public TournamentSettingsFragment() {

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        addPreferencesFromResource(R.xml.preferences);

        statEntryPref = findPreference("pref_statCategories");
        switchPref = findPreference("pref_useStats");
        switchPref.setOnPreferenceChangeListener(switchListener);

        boolean stats = sharedPreferences.getBoolean("pref_useStats", false);
        statEntryPref.setEnabled(stats);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.accept_settings) :
                hasACurrentTournament = mCallback.getCurrentTournament() != null;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(getString(R.string.createTournamentTitle));
                if (hasACurrentTournament)
                    alertDialogBuilder.setMessage(getString(R.string.createTournamentMessageOverwrite));
                else
                    alertDialogBuilder.setMessage(getString(R.string.createTournamentMessage));
                alertDialogBuilder.setPositiveButton(getString(R.string.buttonOK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.advanceFromSettings();
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.buttonCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof SeekBarPreference) {
            dialogFragment = new SeekBarPreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }
        if (preference instanceof StatEntryPreference) {
            dialogFragment = new StatEntryPreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }
        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TournamentSettingsListener) {
            mCallback = (TournamentSettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ParticipantInfoListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private Preference.OnPreferenceChangeListener switchListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ((boolean)newValue) statEntryPref.setEnabled(true);
            else statEntryPref.setEnabled(false);
            return true;
        }
    };

    public interface TournamentSettingsListener {
        void advanceFromSettings();
        Tournament getCurrentTournament();
    }
}
