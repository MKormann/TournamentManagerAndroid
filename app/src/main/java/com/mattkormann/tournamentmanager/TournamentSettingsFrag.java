package com.mattkormann.tournamentmanager;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Matt on 6/22/2016.
 */
public class TournamentSettingsFrag extends PreferenceFragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences);
    }
}
