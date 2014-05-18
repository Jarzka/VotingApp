package org.voimala.votingapp.fragments;

import org.voimala.votingapp.R;
import org.voimala.votingapp.activities.UpcomingVotingActivity;
import org.voimala.votingapp.datasource.voting.VotingContainer;
import org.voimala.votingapp.fragments.adapters.UpcomingVotingsAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class UpcomingVotingsFragment extends Fragment {

    private UpcomingVotingsAdapter upcomingVotingsAdapter = null;
    
    public final void onAttach(final Activity activity) {
        super.onAttach(activity);
    }
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_votings, container, false);
        initializeAdapter(view);
        initializeListeners(view);
        return view;
    }

    private void initializeAdapter(final View view) {
        upcomingVotingsAdapter = new UpcomingVotingsAdapter(getActivity());
        VotingContainer.getInstance().setUpcomingVotingsAdapter(upcomingVotingsAdapter);
        ListView votingsList = (ListView) view.findViewById(R.id.upcomingVotingsList);
        votingsList.setAdapter(upcomingVotingsAdapter);
    }
    
    private void initializeListeners(final View view) {
        ListView votingsList = (ListView) view.findViewById(R.id.upcomingVotingsList);
        votingsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Intent intent = new Intent(getActivity(), UpcomingVotingActivity.class);
                intent.putExtra("votingId",
                        VotingContainer.getInstance().getUpcomingVotings().get(position).getId());
                startActivity(intent);
            }
            
        });
    }
    
}
