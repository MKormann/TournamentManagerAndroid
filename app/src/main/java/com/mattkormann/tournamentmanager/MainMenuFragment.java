package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

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
        final Button startButton = (Button)view.findViewById(R.id.button_start_tournament);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.swapFragment(startButton.getId());
            }
        });

        //Listener for createButton includes a popup menu.
        final Button continueButton = (Button)view.findViewById(R.id.button_continue_tournament);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.swapFragment(continueButton.getId());
            }
        });

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onClick(View view) {
        mCallback.swapFragment(view.getId());
    }

    //Interface to be implemented by containing activity.
    public interface onMenuButtonPressedListener {
        void swapFragment(int buttonID);
    }

}
