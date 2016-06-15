package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.tournaments.SimpleTournamentInfo;
import com.mattkormann.tournamentmanager.tournaments.Tournament;

import java.util.ArrayList;

/**
 * Created by Matt on 6/1/2016.
 */
public class TournamentAdapter extends ArrayAdapter<SimpleTournamentInfo> {

    public TournamentAdapter(Context context, ArrayList<SimpleTournamentInfo> tournaments) {
        super(context, 0, tournaments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleTournamentInfo tournament = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.choose_list_tournament_view,
                    parent, false);
        }

        TextView idView = (TextView) convertView.findViewById(R.id.list_tournament_id);
        TextView nameView = (TextView) convertView.findViewById(R.id.list_tournament_name);
        TextView sizeView = (TextView) convertView.findViewById(R.id.list_tournament_size);
        TextView dateView = (TextView) convertView.findViewById(R.id.list_tournament_date);

        idView.setText(String.valueOf(tournament.getId()));
        nameView.setText(tournament.getName());
        sizeView.setText("Size: " + tournament.getSize());
        dateView.setText(tournament.getDate());

        return convertView;
    }
}
