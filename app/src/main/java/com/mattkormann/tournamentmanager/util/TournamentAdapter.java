package com.mattkormann.tournamentmanager.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.R;
import com.mattkormann.tournamentmanager.sql.DatabaseContract;
import com.mattkormann.tournamentmanager.tournaments.SimpleTournamentInfo;
import com.mattkormann.tournamentmanager.tournaments.Tournament;

import java.util.ArrayList;

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
        public final TextView dateView;
        private int tournamentId;

        public ViewHolder(View itemView) {
            super(itemView);
            idView = (TextView)itemView.findViewById(R.id.list_tournament_id);
            nameView = (TextView)itemView.findViewById(R.id.list_tournament_name);
            sizeView = (TextView)itemView.findViewById(R.id.list_tournament_size);
            dateView = (TextView)itemView.findViewById(R.id.list_tournament_date);

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
        viewHolder.dateView.setText(cursor.getString(cursor.getColumnIndex(
                DatabaseContract.TournamentHistory.COLUMN_NAME_SAVE_TIME)));
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
