package com.mattkormann.tournamentmanager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.tournaments.Match;

import org.w3c.dom.Text;

public class MatchDisplayFragment extends Fragment {

    private Match match;

    MatchDisplayListener mCallback;

    public MatchDisplayFragment() {
        // Required empty public constructor
    }

    public static MatchDisplayFragment newInstance() {
        MatchDisplayFragment fragment = new MatchDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MatchDisplayFragment newInstanceWithMatch(Match match) {
        MatchDisplayFragment fragment = new MatchDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setMatch(match);
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
        if (match != null) {
            TextView textViewLeft =  (TextView)view.findViewById(R.id.participant_name_left);
            textViewLeft.setText("");
        }
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

    public void setMatch(Match match) {
        this.match = match;
    }

    public interface MatchDisplayListener {

    }


}
