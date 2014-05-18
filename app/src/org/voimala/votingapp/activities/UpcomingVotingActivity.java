package org.voimala.votingapp.activities;

import org.voimala.votingapp.R;
import org.voimala.votingapp.activities.adapters.UpcomingVotingAdapter;
import org.voimala.votingapp.datasource.voting.Voting;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class UpcomingVotingActivity extends Activity {
    private UpcomingVotingAdapter upcomingVotingAdapter = null;
    private String votingId = "";
    private Voting voting = null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_upcoming_voting);
        getActionBar().setTitle(R.string.upcoming_votings);
        initializeVotingInfo();
        initializeGUI();
        initializeAdapter();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeVotingInfo() {
        votingId = getIntent().getExtras().getString("votingId");
        voting = VotingContainer.getInstance().findUpcomingVotingById(votingId);
    }
    
    private void initializeGUI() {
        TextView votingTitle = (TextView) findViewById(R.id.activityUpcomingVotingTitle);
        votingTitle.setText(voting.getTitle());
        
        TextView votingDescription = (TextView) findViewById(R.id.activityUpcomingVotingDescription);
        votingDescription.setText(voting.getDescription());
        
        TextView votingCreator = (TextView) findViewById(R.id.activityUpcomingVotingCreator);
        votingCreator.setText(this.getString(R.string.voting_created_by) + ": " + voting.getCreator());
        
        TextView votingStartTime = (TextView) findViewById(R.id.activityUpcomingVotingStartTime);
        votingStartTime.setText(this.getString(R.string.voting_starts) + ": " + voting.getEndTimeAsString());
        
        TextView votingEndTime = (TextView) findViewById(R.id.activityUpcomingVotingEndTime);
        votingEndTime.setText(this.getString(R.string.voting_ends) + ": " + voting.getEndTimeAsString());
    }

    private void initializeAdapter() {
        upcomingVotingAdapter = new UpcomingVotingAdapter(this, voting);
        ListView votingOptionsList = (ListView) findViewById(R.id.activityUpcomingListViewOptions);
        votingOptionsList.setAdapter(upcomingVotingAdapter);
    }

}
