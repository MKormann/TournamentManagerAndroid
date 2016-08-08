package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mattkormann.tournamentmanager.util.SeekBarPreference;
import com.mattkormann.tournamentmanager.util.SeekBarPreferenceDialogFragmentCompat;
import com.mattkormann.tournamentmanager.util.StatEntryPreference;
import com.mattkormann.tournamentmanager.util.StatEntryPreferenceDialogFragmentCompat;

/**
 * Created by Matt on 6/22/2016.
 */
public class TournamentSettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    Preference switchPref;
    Preference statEntryPref;
    SharedPreferences sharedPreferences;
    private TournamentSettingsListener mCallback;

    public TournamentSettingsFragment() {

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String currentPrefFilename = sharedPreferences.getString(MainActivity.CURRENT_PREFS, null);
        if (currentPrefFilename != null)
            sharedPreferences = getContext().getSharedPreferences(currentPrefFilename, Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.preferences);

        statEntryPref = findPreference("pref_statCategories");
        switchPref = findPreference("pref_useStats");
        switchPref.setOnPreferenceChangeListener(switchListener);

        boolean stats = sharedPreferences.getBoolean("pref_useStats", false);
        statEntryPref.setEnabled(stats);

        setHasOptionsMenu(true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.accept_settings) :
                mCallback.advanceFromSettings(getPreferenceManager().getSharedPreferences());
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
        void advanceFromSettings(SharedPreferences sp);
    }
}
