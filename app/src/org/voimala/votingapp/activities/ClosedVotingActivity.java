package org.voimala.votingapp.activities;

import org.voimala.votingapp.R;
import org.voimala.votingapp.activities.adapters.ClosedVotingAdapter;
import org.voimala.votingapp.datasource.voting.Voting;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class ClosedVotingActivity extends Activity {
    private ClosedVotingAdapter closedVotingAdapter = null;
    private String votingId = "";
    private Voting voting = null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_closed_voting);
        getActionBar().setTitle(R.string.closed_votings);
        
        initializeVotingInfo();
        initializeGUI();
        initializeAdapter();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeVotingInfo() {
        votingId = getIntent().getExtras().getString("votingId");
        voting = VotingContainer.getInstance().findClosedVotingById(votingId);
    }
    
    private void initializeGUI() {
        TextView votingTitle = (TextView) findViewById(R.id.activityClosedVotingTitle);
        votingTitle.setText(voting.getTitle());
        
        TextView votingDescription = (TextView) findViewById(R.id.activityClosedVotingDescription);
        votingDescription.setText(voting.getDescription());
        
        TextView votingCreator = (TextView) findViewById(R.id.activityClosedVotingCreator);
        votingCreator.setText(this.getString(R.string.voting_created_by) + ": " + voting.getCreator());
        
        TextView votingStartTime = (TextView) findViewById(R.id.activityClosedVotingStartTime);
        votingStartTime.setText(this.getString(R.string.voting_started) + ": " + voting.getEndTimeAsString());
        
        TextView votingEndTime = (TextView) findViewById(R.id.activityClosedVotingEndTime);
        votingEndTime.setText(this.getString(R.string.voting_ended) + ": " + voting.getEndTimeAsString());
        
        TextView votesAmount = (TextView) findViewById(R.id.activityClosedVotesAmount);
        votesAmount.setText(getString(R.string.voting_votesAmount) + ": " +
                String.valueOf(voting.getNumberOfVotes()));
    }

    private void initializeAdapter() {
        closedVotingAdapter = new ClosedVotingAdapter(this, voting);
        ListView votingOptionsList = (ListView) findViewById(R.id.activityClosedListViewOptions);
        votingOptionsList.setAdapter(closedVotingAdapter);
    }

}
