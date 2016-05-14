package com.mattkormann.tournamentmanager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
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
                PopupMenu popup = new PopupMenu(getActivity(), startButton);
                popup.getMenu().add(Menu.NONE, R.id.start_tournament_create_new,
                        Menu.NONE, getString(R.string.start_tournament_create_new));
                popup.getMenu().add(Menu.NONE, R.id.start_tournament_load_saved,
                        Menu.NONE, getString(R.string.start_tournament_load_saved));

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        mCallback.onMainMenuButtonPressed(item.getItemId());
                        return true;
                    }
                });

                popup.show();
            }
        });

        //Listener for createButton includes a popup menu.
        final Button createButton = (Button)view.findViewById(R.id.button_create_tournament);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), createButton);
                popup.getMenu().add(Menu.NONE, R.id.create_tournament_new_menu,
                        Menu.NONE, getString(R.string.createNew));
                popup.getMenu().add(Menu.NONE, R.id.create_tournament_load_menu,
                        Menu.NONE, getString(R.string.load));

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                      mCallback.onMainMenuButtonPressed(item.getItemId());
                      return true;
                    }
                });

                popup.show();
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
        void onMainMenuButtonPressed(int buttonID);
    }

}
