    package com.mattkormann.tournamentmanager;

    import android.content.ContentValues;
    import android.content.DialogInterface;
    import android.content.SharedPreferences;
    import android.content.pm.ActivityInfo;
    import android.content.res.Configuration;
    import android.database.Cursor;
    import android.net.Uri;
    import android.os.Bundle;
    import android.preference.PreferenceManager;
    import android.support.v4.app.DialogFragment;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.Toolbar;
    import android.view.Menu;
    import android.view.MenuItem;

    import com.mattkormann.tournamentmanager.sql.DatabaseContract;
    import com.mattkormann.tournamentmanager.tournaments.Match;
    import com.mattkormann.tournamentmanager.tournaments.SingleElimTournament;
    import com.mattkormann.tournamentmanager.tournaments.Tournament;
    import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;
    import com.mattkormann.tournamentmanager.util.StatEntryPreferenceDialogFragmentCompat;

    public class MainActivity extends AppCompatActivity
            implements WelcomeFragment.WelcomeFragmentListener,
            MainMenuFragment.onMenuButtonPressedListener,
            ParticipantsFragment.ParticipantInfoListener,
            TournamentSettingsFragment.TournamentSettingsListener,
            TournamentDisplayFragment.TournamentDisplayListener,
            ChooseTournamentFragment.ChooseTournamentListener,
            HistoryFragment.HistoryFragmentListener,
            MatchDisplayFragment.MatchDisplayListener {

        private Tournament currentTournament;
        private boolean phoneDevice = true;
        private boolean startingNewTournament = false;

        public static final String URI_ARG = "URI_ARG";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);

            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;

            if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                    screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
                phoneDevice = false;

            if (phoneDevice) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            //Check if there is a fragment container
            if (findViewById(R.id.fragment_container) != null) {

                //Check if there is already a fragment displayed
                if (savedInstanceState != null) {
                    return;
                }

                WelcomeFragment wf = WelcomeFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, wf).commit();
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case (R.id.tournament_settings) :
                    if (item.getItemId() == R.id.tournament_settings) {
                        swapFragment(FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_SETTINGS_FRAGMENT));
                    }
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        //Replace fragment in fragment_container with id of corresponding button passed.
        //Implementation of interface from MainMenuFragment.java
        @Override
        public void swapFragment(int id) {
            swapFragment(getFragmentFromButton(id));
        }

        public void swapFragment(Fragment fragment) {

            if (fragment instanceof DialogFragment) {
                DialogFragment dialog = (DialogFragment)fragment;
                dialog.show(getSupportFragmentManager(), "choose dialog");
                return;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        //Returns an instance of the requested fragment
        private Fragment getFragmentFromButton(int id) {
            Bundle args = new Bundle();
            switch (id) {
                case (R.id.button_start_tournament):
                    startingNewTournament = true;
                    return FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_SETTINGS_FRAGMENT, args);
                case (R.id.button_continue_tournament):
                    startingNewTournament = true;
                    args.putBoolean(ChooseTournamentFragment.TOURNAMENT_TYPE, true);
                    return FragmentFactory.getFragment(FragmentFactory.CHOOOSE_TOURNAMENT_FRAGMENT, args);
                case (R.id.button_participants):
                    args.putInt(ParticipantsFragment.TYPE_TO_DISPLAY, ParticipantsFragment.INDIVIDUALS);
                    return FragmentFactory.getFragment(FragmentFactory.PARTICIPANTS_FRAGMENT, args);
                case (R.id.button_teams):
                    args.putInt(ParticipantsFragment.TYPE_TO_DISPLAY, ParticipantsFragment.TEAMS);
                    return FragmentFactory.getFragment(FragmentFactory.PARTICIPANTS_FRAGMENT, args);
                case (R.id.button_history):
                    return FragmentFactory.getFragment(FragmentFactory.HISTORY_FRAGMENT, args);
            }

            return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        @Override
        public Tournament getCurrentTournament() {
            return currentTournament;
        }

        public void setCurrentTournament(Tournament tournament) {
            this.currentTournament = tournament;
        }

        @Override
        public void advanceFromSettings() {
            //Check if beginning new tournament, and if so proceed to choosing participants
            //else return to main screen
            if (startingNewTournament) {
                createNewTournamentFromCurrentSettings();
                swapFragment(FragmentFactory.getFragment(FragmentFactory.POPULATE_FRAGMENT));
            } else swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
        }

        public void createNewTournamentFromCurrentSettings() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String name = sharedPreferences.getString("pref_tournamentName", "Tournament");
            int size = sharedPreferences.getInt("pref_tournamentSize", Tournament.MIN_TOURNAMENT_SIZE);
            boolean useStats = sharedPreferences.getBoolean("pref_useStats", false);
            String statCategoriesString = sharedPreferences.getString("pref_statCategories", null);
            String elimType = sharedPreferences.getString("pref_eliminationType", "Single");
            int teamSize = Integer.valueOf(sharedPreferences.getString("pref_teamSize", "1"));

            Tournament tournament = new SingleElimTournament(name, size, teamSize);
            tournament.setSavedId(TournamentDAO.NOT_YET_SAVED);
            if (useStats && statCategoriesString != null) {
                String[] statCategories = StatEntryPreferenceDialogFragmentCompat
                        .stringToArray(statCategoriesString);
                tournament.setStatCategories(statCategories);
            }

            setCurrentTournament(tournament);
        }

        @Override
        public void setCurrentTournamentAndDisplay(Uri uri) {
            Cursor tournamentCursor = getContentResolver().query(uri, null, null, null, null);
            Cursor participantCursor = getContentResolver().query(
                    DatabaseContract.ParticipantTable.CONTENT_URI, null, null, null, null);
            Tournament tournament = TournamentDAO.loadFullTournamentFromCursor(
                    tournamentCursor, participantCursor);
            tournament.setSavedId(Integer.parseInt(uri.getLastPathSegment()));
            setCurrentTournament(tournament);
            swapFragment(FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_DISPLAY_FRAGMENT));
        }

        //Methods implemented from Participants fragment
        @Override
        public void showParticipantInfoDialog(Uri uri, boolean addingNew) {
            ParticipantInfoFragment participantInfo =  new ParticipantInfoFragment();
            Bundle args = new Bundle();
            args.putParcelable(ParticipantsFragment.PARTICIPANT_URI, uri);
            args.putBoolean(ParticipantsFragment.ADDING_NEW, addingNew);
            participantInfo.setArguments(args);
            participantInfo.show(getSupportFragmentManager(), "participantInfoFragment");
        }

        @Override
        public void onFinishParticipantInformationDialog(Uri uri, ContentValues values) {

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (f instanceof PopulateFragment) {
                PopulateFragment pf = (PopulateFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (pf != null) {
                    pf.saveNewParticipant(uri, values);
                }
            }
            else if (f instanceof ParticipantsFragment) {
                ParticipantsFragment pf = (ParticipantsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (pf != null) {
                    pf.saveParticipant(uri, values);
                }
            }
        }

        //Method implemented from Tournament Display Fragment
        @Override
        public void displayMatch(int matchId) {
            FragmentManager fm = getSupportFragmentManager();
            Match match = currentTournament.getMatch(matchId);
            Bundle args = new Bundle();
            args.putInt(Match.MATCH_ID, matchId);
            args.putDoubleArray(Match.MATCH_STATS, match.getStatistics());
            args.putStringArray(Tournament.STAT_CATEGORIES, currentTournament.getStatCategories());
            args.putString(Match.PARTICIPANT_ONE, currentTournament.getParticipant(match.getParticipantSeed(0)).getName());
            args.putString(Match.PARTICIPANT_TWO, currentTournament.getParticipant(match.getParticipantSeed(1)).getName());
            args.putInt(Match.MATCH_WINNER, match.getWinner());
            MatchDisplayFragment mdf =
                    (MatchDisplayFragment)FragmentFactory.getFragment(FragmentFactory.MATCH_DISPLAY_FRAGMENT, args);
            mdf.show(fm, "fragment_match_display");
        }

        @Override
        public void setWinner(int matchId, int winner) {
            currentTournament.setMatchWinner(matchId, winner);
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (f instanceof TournamentDisplayFragment) {
                TournamentDisplayFragment tdf = (TournamentDisplayFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (tdf != null) {
                    tdf.updateMatchInfo(matchId);
                }
            }
        }

        @Override
        public void displayNoTournamentMessage() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.no_tournaments_title));
            alertDialogBuilder.setMessage(getString(R.string.no_tournaments_message));
            alertDialogBuilder.setPositiveButton(getString(R.string.buttonOK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
                }
            });
            alertDialogBuilder.show();
        }

        //Method from MatchDisplayFragment
        @Override
        public void setWinner(int matchId, int winner, double[] stats) {
            currentTournament.setMatchWinner(matchId, winner, stats);
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (f instanceof TournamentDisplayFragment) {
                TournamentDisplayFragment tdf = (TournamentDisplayFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (tdf != null) {
                    tdf.updateMatchInfo(matchId);

                }
            }
        }

        @Override
        public void saveTournament() {
            ContentValues values = TournamentDAO.getFullTournamentContentValues(currentTournament);
            Uri uri = DatabaseContract.TournamentHistory.buildSavedTournamentUri(currentTournament.getSavedId());
            int updatedRows = getContentResolver().update(uri, values, null, null);
            //if (updatedRows < 1) throw new //TODO exception if can't save
        }

        @Override
        public void exitToMain() {
            swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
            currentTournament = null;
        }
    }
