package com.mattkormann.tournamentmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.mattkormann.tournamentmanager.util.SeekBarPreference;
import com.mattkormann.tournamentmanager.util.SeekBarPreferenceDialogFragmentCompat;
import com.mattkormann.tournamentmanager.util.StatEntryPreference;
import com.mattkormann.tournamentmanager.util.StatEntryPreferenceDialogFragmentCompat;

/**
 * Created by Matt on 6/22/2016.
 */
public class TournamentSettingsFrag extends PreferenceFragmentCompat {

    Preference switchPref;
    Preference statEntryPref;

    public TournamentSettingsFrag() {

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.preferences);

        statEntryPref = findPreference("pref_statCategories");
        switchPref = findPreference("pref_useStats");
        switchPref.setOnPreferenceChangeListener(switchListener);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean stats = prefs.getBoolean("pref_useStats", false);
        if (!stats) statEntryPref.setEnabled(false);
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

    private Preference.OnPreferenceChangeListener switchListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ((boolean)newValue) statEntryPref.setEnabled(true);
            else statEntryPref.setEnabled(false);
            return true;
        }
    };
}
