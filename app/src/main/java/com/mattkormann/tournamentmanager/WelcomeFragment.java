package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class WelcomeFragment extends Fragment {

    private WelcomeFragmentListener mCallback;

    public WelcomeFragment() {
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        final AppBarLayout appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.appBar);
        appBarLayout.setExpanded(false, true);

        LinearLayout welcomeLayout = (LinearLayout)view.findViewById(R.id.welcomeFragment);
        welcomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setExpanded(true, true);
                mCallback.swapFragment(FragmentFactory.getFragment(FragmentFactory.MAIN_MENU_FRAGMENT));
            }
        });

        return view;
    }

    //Checks interface is implemented in the activity.
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            mCallback = (WelcomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement WelcomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface WelcomeFragmentListener {
        void swapFragment(Fragment fragment);
    }
}
