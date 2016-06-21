    package com.mattkormann.tournamentmanager;

    import android.content.DialogInterface;
    import android.os.Bundle;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentTransaction;
    import android.support.v7.app.AlertDialog;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.LinearLayout;

    import com.mattkormann.tournamentmanager.participants.Participant;
    import com.mattkormann.tournamentmanager.sql.DatabaseHelper;
    import com.mattkormann.tournamentmanager.tournaments.Match;
    import com.mattkormann.tournamentmanager.tournaments.SqliteTournamentDAO;
    import com.mattkormann.tournamentmanager.tournaments.Tournament;
    import com.mattkormann.tournamentmanager.tournaments.TournamentDAO;

    import java.util.Map;

    public class MainActivity extends AppCompatActivity
            implements WelcomeFragment.WelcomeFragmentListener,
            MainMenuFragment.onMenuButtonPressedListener,
            ParticipantsFragment.ParticipantInfoListener,
            TournamentSettingsFragment.TournamentSettingsListener,
            StatEntryFragment.StatEntryFragmentListener,
            TournamentDisplayFragment.TournamentDisplayListener,
            PopulateFragment.PopulateFragmentListener,
            ChooseParticipantFragment.ChooseParticipantListener,
            ChooseTournamentFragment.ChooseTournamentListener,
            HistoryFragment.HistoryFragmentListener,
            MatchDisplayFragment.MatchDisplayListener {

        private Tournament currentTournament;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_welcome);

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

        //Replace fragment in fragment_container with id of corresponding button passed.
        //Implementation of interface from MainMenuFragment.java
        @Override
        public void swapFragment(int id) {
            swapFragment(getFragmentFromButton(id));
        }

        public void swapFragment(Fragment fragment) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        //Returns an instance of the requested fragment
        private Fragment getFragmentFromButton(int id) {
            Bundle args = new Bundle();
            switch (id) {
                case (R.id.start_tournament_create_new):
                    args.putBoolean(TournamentSettingsFragment.START_TOURNAMENT_AFTER, true);
                    args.putInt(TournamentSettingsFragment.TEMPLATE_ID, TournamentDAO.NEW_TOURNAMENT_TEMPLATE);
                    return FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_SETTINGS_FRAGMENT, args);
                case (R.id.start_tournament_load_saved):
                    args.putBoolean(ChooseTournamentFragment.START_AFTER, true);
                    args.putString(ChooseTournamentFragment.TOURNAMENT_TYPE, ChooseTournamentFragment.TEMPLATES);
                    return FragmentFactory.getFragment(FragmentFactory.CHOOOSE_TOURNAMENT_FRAGMENT, args);
                case (R.id.start_tournament_load_in_progress):
                    args.putBoolean(ChooseTournamentFragment.START_AFTER, true);
                    args.putString(ChooseTournamentFragment.TOURNAMENT_TYPE, ChooseTournamentFragment.IN_PROGRESS);
                    return FragmentFactory.getFragment(FragmentFactory.CHOOOSE_TOURNAMENT_FRAGMENT, args);
                case (R.id.create_tournament_new_menu):
                    args.putBoolean(TournamentSettingsFragment.START_TOURNAMENT_AFTER, false);
                    args.putInt(TournamentSettingsFragment.TEMPLATE_ID, TournamentDAO.NEW_TOURNAMENT_TEMPLATE);
                    return FragmentFactory.getFragment(FragmentFactory.TOURNAMENT_SETTINGS_FRAGMENT, args);
                case (R.id.create_tournament_load_menu):
                    args.putBoolean(ChooseTournamentFragment.START_AFTER, false);
                    args.putString(ChooseTournamentFragment.TOURNAMENT_TYPE, ChooseTournamentFragment.TEMPLATES);
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

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (f instanceof PopulateFragment) {
                PopulateFragment pf = (PopulateFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (pf != null) {
                    assignChosenParticipant(pf.saveNewParticipant(name, type), pf.getSelectedSeed());
                }
            }
            else if (f instanceof ParticipantsFragment) {
                ParticipantsFragment pf = (ParticipantsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (pf != null) {
                    pf.saveInformation(name, type);
                }
            }
        }

        //Methods implemented from Tournament Settings fragment
        @Override
        public void advanceFromSettings(boolean startTournament) {
            if (startTournament) {
                Bundle args = new Bundle();
                args.putInt(PopulateFragment.TOURNAMENT_SIZE, currentTournament.getSize());
                swapFragment(FragmentFactory.getFragment(FragmentFactory.POPULATE_FRAGMENT, args));
            } else {
                swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
            }
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

        //Method implemented from Stat Entry Fragment
        @Override
        public void onOkButtonPressed(String[] statCategories) {
            TournamentSettingsFragment tsf = (TournamentSettingsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (tsf != null) {
                tsf.setStatCategories(statCategories);
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
            args.putString(Match.PARTICIPANT_ONE, currentTournament.getParticipant(match.getParticipantIndex(0)).getName());
            args.putString(Match.PARTICIPANT_TWO, currentTournament.getParticipant(match.getParticipantIndex(1)).getName());
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

        //Method implemented from Populate Fragment
        public void showChooseParticipantFragment(int seed, Map<Integer, Participant> participantMap) {
            FragmentManager fm = getSupportFragmentManager();
            ChooseParticipantFragment cpf = ChooseParticipantFragment.newInstance(participantMap);
            Bundle args = new Bundle();
            args.putInt(PopulateFragment.SEED_TO_ASSIGN, seed);
            cpf.setArguments(args);
            cpf.show(fm, "fragment_choose_participant");
        }

        //Methods implemented from Choose Participant Fragment
        @Override
        public void addAndAssignNewParticipant(int seed) {
            showParticipantInfoDialog(ParticipantsFragment.INDIVIDUALS); //TODO type
        }

        @Override
        public void assignChosenParticipant(int id, int seed) {
            PopulateFragment pf = (PopulateFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (pf != null) {
                pf.assignSeed(id, seed);
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

        public void saveAndExit() {
            DatabaseHelper mDbHelper = new DatabaseHelper(this);
            SqliteTournamentDAO tDao = new SqliteTournamentDAO(mDbHelper);
            tDao.saveFullTournament(currentTournament);
            swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
            currentTournament = null;
        }
    }
