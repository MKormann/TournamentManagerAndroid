package com.mattkormann.tournamentmanager.util;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matt on 7/25/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface HistoryClickListener {
        void onClick(int tournamentId, String tournamentName);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameView;
        public final TextView sizeView;
        public final TextView winnerView;
        public final TextView lastSavedView;
        private int tournamentId;

        public ViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.nameView);
            sizeView = (TextView)itemView.findViewById(R.id.sizeView);
            winnerView = (TextView)itemView.findViewById(R.id.winnerView);
            lastSavedView = (TextView)itemView.findViewById(R.id.savedView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(tournamentId, nameView.getText().toString());
                }
            });
        }

        public void setTournamentId(int tournamentId) {
            this.tournamentId = tournamentId;
        }

    }

    private Cursor cursor = null;
    private final HistoryClickListener clickListener;
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat df2 = new SimpleDateFormat("MM/dd/yy hh:mm a");


    public HistoryAdapter(HistoryClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.single_tournament_history_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setTournamentId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.TournamentHistory._ID)));
        holder.nameView.setText(cursor.getString(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME)));
        holder.sizeView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE))));
        holder.winnerView.setText(cursor.getString(cursor.getColumnIndex(
                DatabaseContract.ParticipantTable.COLUMN_NAME_NAME)));
        String dateString = cursor.getString(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME));
        try {
            Date date = df.parse(dateString);
            String displayDate = df2.format(date);
            holder.lastSavedView.setText(displayDate);
        } catch (ParseException pe) {
            holder.lastSavedView.setText("Unknown");
        }
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
