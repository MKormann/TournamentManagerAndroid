package com.mattkormann.tournamentmanager.util;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;

/**
 * Created by Matt on 5/26/2016.
 */
public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    public interface ParticipantClickListener {
        void onClick(String name, int participantId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView idView;
        public final TextView nameView;
        private int participantId;

        public ViewHolder(View itemView) {
            super(itemView);
            idView = (TextView)itemView.findViewById(R.id.list_view_id);
            nameView = (TextView)itemView.findViewById(R.id.list_view_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(nameView.getText().toString(), participantId);
                }
            });
        }

        public void setParticipantId(int participantId) {
            this.participantId = participantId;
            idView.setText(String.valueOf(participantId));
        }
    }

    private Cursor cursor = null;
    private final ParticipantClickListener clickListener;

    public ParticipantsAdapter(ParticipantClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.choose_list_text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setParticipantId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ParticipantTable._ID)));
        holder.nameView.setText(cursor.getString(cursor.getColumnIndex(
                DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)));
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
