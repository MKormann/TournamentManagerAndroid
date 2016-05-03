package com.mattkormann.tournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuFragment extends Fragment implements
        View.OnClickListener {

    private onMenuButtonPressedListener mCallback;

    public MainMenuFragment() {
    }

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Listeners for buttons
        Button startButton = (Button)view.findViewById(R.id.button_start_tournament);
        startButton.setOnClickListener(this);
        Button createButton = (Button)view.findViewById(R.id.button_create_tournament);
        createButton.setOnClickListener(this);
        Button participantsButton = (Button)view.findViewById(R.id.button_participants);
        participantsButton.setOnClickListener(this);
        Button teamsButton = (Button)view.findViewById(R.id.button_teams);
        teamsButton.setOnClickListener(this);
        Button historyButton = (Button)view.findViewById(R.id.button_history);
        historyButton.setOnClickListener(this);

        return view;
    }

    //Checks interface is implemented in the activity.
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            mCallback = (onMenuButtonPressedListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement onMenuButtonPressedListener");
        }
        MainMenuFragment fg = new MainMenuFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onClick(View view) {
        mCallback.onMainMenuButtonPressed(view.getId());
    }

    //Interface to be implemented by containing activity.
    public interface onMenuButtonPressedListener {
        public void onMainMenuButtonPressed(int buttonID);
    }

}
