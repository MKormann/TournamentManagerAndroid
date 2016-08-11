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
 * Created by Matt on 6/1/2016.
 */
public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {

    public interface TournamentClickListener {
        void onClick(int tournamentId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView idView;
        public final TextView nameView;
        public final TextView sizeView;
        public final TextView lastSavedView;
        private int tournamentId;

        public ViewHolder(View itemView) {
            super(itemView);

            idView = (TextView)itemView.findViewById(R.id.list_tournament_id);
            nameView = (TextView)itemView.findViewById(R.id.list_tournament_name);
            sizeView = (TextView)itemView.findViewById(R.id.list_tournament_size);
            lastSavedView = (TextView)itemView.findViewById(R.id.list_tournament_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(tournamentId);
                }
            });
        }

        public void setTournamentId(int tournamentId) {
            this.tournamentId = tournamentId;
            idView.setText(String.valueOf(tournamentId));
        }
    }

    private Cursor cursor = null;
    private final TournamentClickListener clickListener;
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat df2 = new SimpleDateFormat("MM/dd/yy hh:mm a");


    public TournamentAdapter(TournamentClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.choose_list_tournament_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        cursor.moveToPosition(position);
        viewHolder.setTournamentId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.TournamentHistory._ID)));
        viewHolder.nameView.setText(cursor.getString(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_TOURNAMENT_NAME)));
        viewHolder.sizeView.setText("Size: " + cursor.getInt(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_SIZE)));
        String dateString = cursor.getString(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME));
        try {
            Date date = df.parse(dateString);
            String displayDate = df2.format(date);
            viewHolder.lastSavedView.setText(displayDate);
        } catch (ParseException pe) {
            viewHolder.lastSavedView.setText("Unknown");
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
