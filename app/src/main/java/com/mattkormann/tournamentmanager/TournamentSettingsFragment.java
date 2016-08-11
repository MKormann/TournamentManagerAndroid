package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
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
public class TournamentSettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    StatEntryPreference statEntryPref;
    SharedPreferences sharedPreferences;
    boolean hasACurrentTournament;
    private TournamentSettingsListener mCallback;

    public TournamentSettingsFragment() {

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        addPreferencesFromResource(R.xml.preferences);

        statEntryPref = (StatEntryPreference)findPreference("pref_statCategories");

        //Set summaries of preferences
        EditTextPreference namePref = (EditTextPreference)findPreference("pref_tournamentName");
        namePref.setSummary(namePref.getText());
        SeekBarPreference sizePref = (SeekBarPreference)findPreference("pref_tournamentSize");
        sizePref.setSummary(String.valueOf(sizePref.size));

        SwitchPreferenceCompat statTrackPref = (SwitchPreferenceCompat)findPreference("pref_useStats");
        boolean useStats = statTrackPref.isChecked();
        statEntryPref.setEnabled(useStats);
        if (useStats) {
            statTrackPref.setSummary(getString(R.string.on));
            setStatEntryPrefSummary();
        } else statTrackPref.setSummary(getString(R.string.off));

        ListPreference elimPref = (ListPreference)findPreference("pref_eliminationType");
        elimPref.setSummary(elimPref.getValue());
        ListPreference teamPref = (ListPreference)findPreference("pref_teamSize");
        teamPref.setSummary(teamPref.getValue());

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
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreference, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference)pref;
            etp.setSummary(etp.getText());
        } else if (pref instanceof SeekBarPreference) {
            SeekBarPreference sbp = (SeekBarPreference)pref;
            sbp.setSummary(String.valueOf(sbp.size));
        } else if (pref instanceof SwitchPreferenceCompat) {
            SwitchPreferenceCompat spc = (SwitchPreferenceCompat)pref;
            if (spc.isChecked()) {
                statEntryPref.setEnabled(true);
                spc.setSummary(getString(R.string.on));
            } else {
                statEntryPref.setEnabled(false);
                spc.setSummary(getString(R.string.off));
            }
        } else if (pref instanceof StatEntryPreference) {
            setStatEntryPrefSummary();
        } else if (pref instanceof ListPreference) {
            ListPreference lp = (ListPreference)pref;
            lp.setSummary(lp.getValue());
        }
    }

    public void setStatEntryPrefSummary() {
        //Convert categoriesString to array and back for readability
        String statCategoriesString = statEntryPref.statCategoriesString;
        if (statCategoriesString == null || statCategoriesString.equals("")) {
            statEntryPref.setSummary(getString(R.string.stat_categories_summary));
        } else {
            String[] statCategories = StatEntryPreferenceDialogFragmentCompat.stringToArray(statCategoriesString);
            String summary = "";
            for (int i = 0; i < statCategories.length; i++) {
                summary += statCategories[i];
                if (i < statCategories.length - 1) summary += ", ";
            }
            statEntryPref.setSummary(summary);
        }
    }

    public interface TournamentSettingsListener {
        void advanceFromSettings();
        Tournament getCurrentTournament();
    }
}
