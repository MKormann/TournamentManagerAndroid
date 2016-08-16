package com.mattkormann.tournamentmanager.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.participants.Participant;

import java.util.SortedMap;

/**
 * Created by Matt on 8/11/2016.
 */
public class ParticipantsPoolAdapter extends RecyclerView.Adapter<ParticipantsPoolAdapter.ViewHolder> {

    public interface PoolClickListener {
        void onClick(Participant participant, LinearLayout row);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView idView;
        public final TextView nameView;
        private Participant participant;

        public ViewHolder(View itemView) {
            super(itemView);
            idView = (TextView)itemView.findViewById(R.id.list_view_id);
            nameView = (TextView)itemView.findViewById(R.id.list_view_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout row = (LinearLayout)v.findViewById(R.id.chooseTextView);
                    clickListener.onClick(participant, row);
                }
            });
        }

        public void setParticipant(Participant participant) {
            this.participant = participant;
            if (participant == null) {
                nameView.setText("-");
                idView.setText("-");
            } else {
                nameView.setText(participant.getName());
                idView.setText(String.valueOf(participant.getID()));
            }
        }
    }

    private SortedMap<Integer, Participant> participantsPool;
    private Integer[] keys;
    private final PoolClickListener clickListener;

    public ParticipantsPoolAdapter(PoolClickListener clickListener, SortedMap<Integer, Participant> participantsPool) {
        this.clickListener = clickListener;
        this.participantsPool = participantsPool;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.choose_list_text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        keys = participantsPool.keySet().toArray(new Integer[0]);
        Participant p = participantsPool.get(keys[position]);
        holder.setParticipant(p);
    }

    @Override
    public int getItemCount() {
        return participantsPool.size();
    }

    public void swapParticipantPoolMap(SortedMap<Integer, Participant> participantsPool) {
        this.participantsPool = participantsPool;
        notifyDataSetChanged();
    }
}

