package com.mattkormann.tournamentmanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.util.ParticipantsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChooseParticipantFragment extends DialogFragment {

    private ChooseParticipantListener mCallback;
    private ParticipantsAdapter participantsAdapter;
    private Map<Integer, Participant> participantMap;
    private ListView listView;

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View chooseParticipantFragment = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_choose_participant, null
        );

        listView = (ListView)chooseParticipantFragment.findViewById(R.id.choose_list);
        setListItems();

        builder.setView(chooseParticipantFragment);

        return builder.create();
    }

    public void setParticipantsMap(Map<Integer, Participant> participantsMap) {
        this.participantMap = participantsMap;
    }

    public void setListItems() {

        //Create adapter for list view to display participants
        ArrayList<Participant> arrayOfParticipants = new ArrayList<>();
        participantsAdapter = new ParticipantsAdapter(getContext(), arrayOfParticipants);
        listView.setAdapter(participantsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView idView = (TextView)view.findViewById(R.id.list_view_id);
                int clickedId = Integer.valueOf(idView.getText().toString());
                mCallback.assignChosenParticipant(clickedId,
                        getArguments().getInt(PopulateFragment.SEED_TO_ASSIGN));
                dismiss();
            }
        });

        //Sort in order of Participant ID
        arrayOfParticipants.addAll(participantMap.values());
        Collections.sort(arrayOfParticipants, new Comparator<Participant>() {
            @Override
            public int compare(Participant p1, Participant p2) {
                if (p2.getID() < p1.getID()) return 1;
                else return -1;
            }
        });

        //Add header view to list for creating a new participant
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addNew = inflater.inflate(R.layout.choose_list_text_view, null);
        TextView nameView = (TextView)addNew.findViewById(R.id.list_view_name);
        nameView.setText(getString(R.string.create_new_participant));

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addAndAssignNewParticipant(getArguments().getInt(PopulateFragment.SEED_TO_ASSIGN));
                dismiss();
            }
        });
        listView.addHeaderView(addNew);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChooseParticipantListener) {
            mCallback = (ChooseParticipantListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChooseParticipantListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface ChooseParticipantListener {
        void addAndAssignNewParticipant(int seed);
        void assignChosenParticipant(int id, int seed);
    }
}
