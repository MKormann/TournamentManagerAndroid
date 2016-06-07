package com.mattkormann.tournamentmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Matt on 6/7/2016.
 */
public class FragmentFactory {

    public final static String MAIN_MENU_FRAGMENT = "MAIN_MENU_FRAGMENT";
    public final static String CHOOSE_PARTICIPANT_FRAGMENT = "CHOOSE_PARTICIPANT_FRAGMENT";
    public final static String CHOOOSE_TOURNAMENT_FRAGMENT = "CHOOSE_TOURNAMENT_FRAGMENT";
    public final static String HISTORY_FRAGMENT = "HISTORY_FRAGMENT";
    public final static String MATCH_DISPLAY_FRAGMENT = "MATCH_DISPLAY_FRAGMENT";
    public final static String PARTICIPANT_INFO_FRAGMENT = "PARTICIPANT_INFO_FRAGMENT";
    public final static String PARTICIPANTS_FRAGMENT = "PARTICIPANTS_FRAGMENT";
    public final static String POPULATE_FRAGMENT = "POPULATE_FRAGMENT";
    public final static String STAT_ENTRY_FRAGMENT = "STAT_ENTRY_FRAGMENT";
    public final static String TOURNAMENT_DISPLAY_FRAGMENT = "TOURNAMENT_DISPLAY_FRAGMENT";
    public final static String TOURNAMENT_SETTINGS_FRAGMENT = "TOURNAMENT_SETTINGS_FRAGMENT";

    public static Fragment getFragment(String fragmentClass) {
        return getFragment(fragmentClass, new Bundle());
    }

    public static Fragment getFragment(String fragmentClass, Bundle args) {
        Fragment fragment = new Fragment();
        switch (fragmentClass) {
            case (MAIN_MENU_FRAGMENT):
                fragment = new MainMenuFragment();
            case (CHOOSE_PARTICIPANT_FRAGMENT):
                fragment = new ChooseParticipantFragment();
            case (CHOOOSE_TOURNAMENT_FRAGMENT):
                fragment = new ChooseTournamentFragment();
            case (HISTORY_FRAGMENT):
                fragment = new HistoryFragment();
            case (MATCH_DISPLAY_FRAGMENT):
                fragment = new MatchDisplayFragment();
            case (PARTICIPANT_INFO_FRAGMENT):
                fragment = new ParticipantInfoFragment();
            case (PARTICIPANTS_FRAGMENT):
                fragment = new ParticipantsFragment();
            case (POPULATE_FRAGMENT):
                fragment = new PopulateFragment();
            case (STAT_ENTRY_FRAGMENT):
                fragment = new StatEntryFragment();
            case (TOURNAMENT_DISPLAY_FRAGMENT):
                fragment = new TournamentDisplayFragment();
            case (TOURNAMENT_SETTINGS_FRAGMENT):
                fragment = new TournamentSettingsFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }
}
