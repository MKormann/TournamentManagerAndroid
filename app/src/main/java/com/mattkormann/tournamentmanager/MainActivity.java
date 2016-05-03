    package com.mattkormann.tournamentmanager;

    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.app.FragmentTransaction;

    public class MainActivity extends FragmentActivity
            implements MainMenuFragment.onMenuButtonPressedListener {

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
                MainMenuFragment mainFragment = new MainMenuFragment();
                mainFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mainFragment).commit();
            }

        }

        //Replace fragment in fragment_container with id of corresponding button passed.
        //Implementation of interface from MainMenuFragment.java
        public void onMainMenuButtonPressed(int buttonID) {

            Fragment fragment = new Fragment();

            switch (buttonID) {
                case (R.id.button_start_tournament):
                    //fragment = new StartTournamentFragment();
                    break;
                case (R.id.button_create_tournament):
                    //fragment = new CreateTournamentFragment();
                    break;
                case (R.id.button_participants):
                    //fragment = new ParticipantsFragment();
                    break;
                case (R.id.button_teams):
                    //fragment = new TeamsFragment();
                    break;
                case (R.id.button_history):
                    //fragment = new HistoryFragment();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

    }
