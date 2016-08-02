package com.mattkormann.tournamentmanager.util;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 5/26/2016.
 */
public class ParticipantsSeedAdapter extends RecyclerView.Adapter<ParticipantsSeedAdapter.SeedViewHolder> {

    public interface SeedClickListener {
        void onClick(int seed, LinearLayout row);
    }

    public class SeedViewHolder extends RecyclerView.ViewHolder {
        public final TextView seedView;
        public final TextView nameView;
        private int seed;
        private Participant participant;

        public SeedViewHolder(View itemView) {
            super(itemView);
            seedView = (TextView)itemView.findViewById(R.id.list_view_id);
            nameView = (TextView)itemView.findViewById(R.id.list_view_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout row = (LinearLayout)v.findViewById(R.id.chooseTextView);
                    clickListener.onClick(seed, row);
                }
            });
        }

        public void setSeed(int seed) {
            this.seed = seed;
            seedView.setText(String.valueOf(seed));
        }

        public void setParticipant(Participant participant) {
            this.participant = participant;
            if (participant == null) {
                nameView.setText("-");
            } else {
                nameView.setText(participant.getName());
            }
        }
    }

    private Map<Integer, Participant> participantsMap;
    private final SeedClickListener clickListener;

    public ParticipantsSeedAdapter(SeedClickListener clickListener, Map<Integer, Participant> participantsMap) {
        this.clickListener = clickListener;
        this.participantsMap = participantsMap;
    }

    @Override
    public SeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.choose_list_text_view, parent, false);
        return new SeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SeedViewHolder holder, int position) {
        holder.setSeed(position + 1);
        holder.setParticipant(participantsMap.get(position + 1));
    }

    @Override
    public int getItemCount() {
        return participantsMap.size();
    }

    public void swapParticipantToSeedMap(Map<Integer, Participant> participantsMap) {
        this.participantsMap = participantsMap;
        notifyDataSetChanged();
    }
}
