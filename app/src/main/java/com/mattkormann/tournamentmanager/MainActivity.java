    package com.mattkormann.tournamentmanager;

    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;

    import com.mattkormann.tournamentmanager.tournaments.Tournament;

    import java.util.logging.Level;
    import java.util.logging.Logger;

    public class MainActivity extends FragmentActivity
            implements MainMenuFragment.onMenuButtonPressedListener,
            ParticipantsFragment.ParticipantInfoListener,
            TournamentSettingsFragment.TournamentSettingsListener,
            StatEntryFragment.StatEntryFragmentListener {

        private Tournament currentTournament;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //Check if there is a fragment container
            if (findViewById(R.id.fragment_container) != null) {

                //Check if there is already a fragment displayed
                if (savedInstanceState != null) {
                    return;
                }

                //Create and display fragment for the main menu
                MainMenuFragment mainFragment = MainMenuFragment.newInstance();
                mainFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mainFragment).commit();
            }

        }

        //Replace fragment in fragment_container with id of corresponding button passed.
        //Implementation of interface from MainMenuFragment.java
        public void onMainMenuButtonPressed(int buttonID) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, createFragment(buttonID));
            transaction.addToBackStack(null);

            transaction.commit();
        }

        //Returns an instance of the requested fragment
        private Fragment createFragment(int id) {
            Bundle args = new Bundle();
            switch (id) {
                case (R.id.start_tournament_create_new):
                    TournamentSettingsFragment tsfc = new TournamentSettingsFragment();
                    return tsfc;
                case (R.id.start_tournament_load_saved):
                    TournamentSettingsFragment tsfs = new TournamentSettingsFragment();
                    return tsfs;
                case (R.id.create_tournament_new_menu):
                    TournamentSettingsFragment tsfn = new TournamentSettingsFragment();
                    return tsfn;
                case (R.id.create_tournament_load_menu):
                    TournamentSettingsFragment tsfl = new TournamentSettingsFragment();
                    return tsfl;
                case (R.id.button_participants):
                    ParticipantsFragment pfi = new ParticipantsFragment();
                    args.putInt(ParticipantsFragment.TYPE_TO_DISPLAY, ParticipantsFragment.INDIVIDUALS);
                    pfi.setArguments(args);
                    return pfi;
                case (R.id.button_teams):
                    ParticipantsFragment pft = new ParticipantsFragment();
                    args.putInt(ParticipantsFragment.TYPE_TO_DISPLAY, ParticipantsFragment.TEAMS);
                    pft.setArguments(args);
                    return pft;
                case (R.id.button_history):
                    //fragment = new HistoryFragment();
                    break;
            }

            return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        public void setCurrentTournament(Tournament tournament) {
            this.currentTournament = tournament;
        }

        //Methods implemented from Participants fragment to show info editor and retrieve new data
        @Override
        public void showParticipantInfoDialog(int type) {
            FragmentManager fm = getSupportFragmentManager();
            ParticipantInfoFragment participantInfo = ParticipantInfoFragment.newInstance();
            Bundle args = new Bundle();
            args.putInt(ParticipantsFragment.TYPE_TO_DISPLAY, type);
            participantInfo.setArguments(args);
            participantInfo.show(fm, "fragment_participant_info");
        }

        @Override
        public void onFinishParticipantInformationDialog(String name, int type) {
            ParticipantsFragment pf = (ParticipantsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (pf != null) {
                pf.saveInformation(name, type);
            }
        }

        //Methods implemented from Tournament Settings fragment
        @Override
        public void generateTournament() {

        }

        @Override
        public void displayStatEntry(String[] statCategories) {
            FragmentManager fm = getSupportFragmentManager();
            StatEntryFragment statEntryFragment = StatEntryFragment.newInstance();
            Bundle args = new Bundle();
            if (statCategories != null) {
                args.putStringArray(TournamentSettingsFragment.STAT_CATEGORIES, statCategories);
            }
            statEntryFragment.setArguments(args);
            statEntryFragment.show(fm, "fragment_stat_entry");
        }

        @Override
        public void onOkButtonPressed(String[] statCategories) {
            TournamentSettingsFragment tsf = (TournamentSettingsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (tsf != null) {
                tsf.setStatCategories(statCategories);
            }
        }

    }
