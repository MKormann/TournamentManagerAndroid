package com.mattkormann.tournamentmanager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mattkormann.tournamentmanager.tournaments.Match;
import com.mattkormann.tournamentmanager.tournaments.Tournament;

public class MatchDisplayFragment extends DialogFragment {

    MatchDisplayListener mCallback;
    private ToggleButton[] winButtons;
    private Button enterButton;
    private int matchId;
    private int winner = Match.NOT_YET_ASSIGNED;
    private double[] stats;

    public MatchDisplayFragment() {
        // Required empty public constructor
    }

    public static MatchDisplayFragment newInstance() {
        MatchDisplayFragment fragment = new MatchDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_display, container, false);

        setMatchInformation(view);

        return view;
    }

    private void setMatchInformation(View view) {
        //TODO currently setup for two participants only

        //Set text views with arguments passed
        Bundle args = getArguments();
        if (args != null) {
            matchId = args.getInt(Match.MATCH_ID);
            TextView title = (TextView)view.findViewById(R.id.match_no_title);
            title.setText(getString(R.string.matchNo) + " " + (matchId + 1));
            TextView textParticipantOne = (TextView) view.findViewById(R.id.participant_name_left);
            textParticipantOne.setText(args.getString(Match.PARTICIPANT_ONE));
            TextView textParticipantTwo = (TextView) view.findViewById(R.id.participant_name_right);
            textParticipantTwo.setText(args.getString(Match.PARTICIPANT_TWO));
            winner = args.getInt(Match.MATCH_WINNER);

            //Display stats if used
            String[] categories = args.getStringArray(Tournament.STAT_CATEGORIES);
            stats = args.getDoubleArray(Match.MATCH_STATS);
            if (categories != null && stats != null) {

                //Get references to the three linear layouts
                LinearLayout left = (LinearLayout)view.findViewById(R.id.match_layout_left);
                LinearLayout center = (LinearLayout)view.findViewById(R.id.match_layout_center);
                LinearLayout right = (LinearLayout)view.findViewById(R.id.match_layout_right);

                //Create parameters for text displays
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;

                //Add views to layout
                for (int i = 0; i < categories.length; i++) {
                    TextView cat = new TextView(getContext());
                    cat.setText(categories[i]);
                    cat.setLayoutParams(lp);
                    center.addView(cat);

                    TextView statLeft = new TextView(getContext());
                    statLeft.setText(String.valueOf(stats[i * 2]));
                    statLeft.setLayoutParams(lp);
                    left.addView(statLeft);

                    TextView statRight = new TextView(getContext());
                    statRight.setText(String.valueOf(stats[i * 2 + 1]));
                    statRight.setLayoutParams(lp);
                    right.addView(statRight);
                }

            }
        }
        winButtons = new ToggleButton[2];
        winButtons[0] = (ToggleButton)view.findViewById(R.id.set_winner_left);
        winButtons[1] = (ToggleButton) view.findViewById(R.id.set_winner_right);
        for (int i = 0; i < winButtons.length; i++) {
            if (i == winner) winButtons[i].setChecked(true);
            winButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton tb = (ToggleButton)v;
                    if (tb.isChecked()) {
                        for (int i = 0; i < winButtons.length; i++) {
                            if (winButtons[i] != tb) winButtons[i].setChecked(false);
                            else winner = i;
                        }
                    } else if (!tb.isChecked()) {
                        winner = Match.NOT_YET_ASSIGNED;
                    }
                }
            });
        }

        enterButton = (Button)view.findViewById(R.id.enter_result_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.setWinner(matchId, winner, stats);
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MatchDisplayListener) {
            mCallback = (MatchDisplayListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MatchDisplayListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface MatchDisplayListener {
        void setWinner(int matchId, int winner, double[] stats);
    }


}
