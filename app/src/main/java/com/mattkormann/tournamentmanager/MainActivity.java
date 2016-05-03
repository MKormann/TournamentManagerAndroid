    package com.mattkormann.tournamentmanager;

    import android.net.Uri;
    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.app.FragmentTransaction;

    import com.mattkormann.tournamentmanager.participants.Participant;

    public class MainActivity extends FragmentActivity
            implements MainMenuFragment.onMenuButtonPressedListener,
            ParticipantsFragment.OnFragmentInteractionListener {

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

            Fragment fragment = new Fragment();

            switch (id) {
                case (R.id.button_start_tournament):
                    //fragment = new StartTournamentFragment();
                    break;
                case (R.id.button_create_tournament):
                    //fragment = new CreateTournamentFragment();
                    break;
                case (R.id.button_participants):
                    fragment = new ParticipantsFragment();
                    break;
                case (R.id.button_teams):
                    //fragment = new TeamsFragment();
                    break;
                case (R.id.button_history):
                    //fragment = new HistoryFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public void onFragmentInteraction(Uri uri) {

        }

    }
