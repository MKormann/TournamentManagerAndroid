    package com.mattkormann.tournamentmanager;

    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;

    import java.util.logging.Level;
    import java.util.logging.Logger;

    public class MainActivity extends FragmentActivity
            implements MainMenuFragment.onMenuButtonPressedListener,
            ParticipantsFragment.ParticipantInfoListener {

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
            switch (id) {
                case (R.id.button_start_tournament):
                    //fragment = new StartTournamentFragment();
                    break;
                case (R.id.button_create_tournament):
                    //fragment = new CreateTournamentFragment();
                    break;
                case (R.id.button_participants):
                    return new ParticipantsFragment();
                case (R.id.button_teams):
                    //fragment = new TeamsFragment();
                    break;
                case (R.id.button_history):
                    //fragment = new HistoryFragment();
                    break;
            }

            return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        //Methods implemented from Participants fragment to show info editor and retrieve new data
        @Override
        public void showParticipantInfoDialog() {
            FragmentManager fm = getSupportFragmentManager();
            ParticipantInfoFragment participantInfo = ParticipantInfoFragment.newInstance();
            participantInfo.show(fm, "fragment_participant_info");
        }

        @Override
        public void onFinishParticipantInformationDialog(String name, boolean team) {
            ParticipantsFragment pf = (ParticipantsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (pf != null) {
                pf.saveInformation(name, team);
            }
        }

    }
