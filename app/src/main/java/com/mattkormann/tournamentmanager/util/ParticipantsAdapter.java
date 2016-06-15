package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.participants.Participant;

import java.util.ArrayList;

/**
 * Created by Matt on 5/26/2016.
 */
public class ParticipantsAdapter extends ArrayAdapter<Participant> {

    public ParticipantsAdapter(Context context, ArrayList<Participant> participants) {
        super(context, 0, participants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Participant participant = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.choose_list_text_view,
                    parent, false);
        }

        TextView idView = (TextView) convertView.findViewById(R.id.list_view_id);
        TextView nameView = (TextView) convertView.findViewById(R.id.list_view_name);

        idView.setText(String.valueOf(participant.getID()));
        nameView.setText(participant.getName());

        return convertView;
    }
}
