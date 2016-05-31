package com.mattkormann.tournamentmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mattkormann.tournamentmanager.participants.Participant;
import com.mattkormann.tournamentmanager.util.ParticipantsAdapter;

import java.util.ArrayList;
import java.util.Map;

public class ChooseParticipantFragment extends DialogFragment {

    private ChooseParticipantListener mCallback;
    private ParticipantsAdapter participantsAdapter;
    private ListView listView;

    public ChooseParticipantFragment() {
        // Required empty public constructor
    }

    public static ChooseParticipantFragment newInstance() {
        ChooseParticipantFragment fragment = new ChooseParticipantFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_participant, container, false);

        listView = (ListView)view.findViewById(R.id.choose_list);

        return view;
    }

    public void setListItems(Map<Integer, Participant> participantMap) {

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

        arrayOfParticipants.addAll(participantMap.values());

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
