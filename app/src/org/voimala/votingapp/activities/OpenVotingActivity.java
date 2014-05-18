package org.voimala.votingapp.activities;

import org.voimala.votingapp.R;
import org.voimala.votingapp.activities.adapters.OpenVotingAdapter;
import org.voimala.votingapp.datasource.voting.Voting;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class OpenVotingActivity extends Activity {
    private OpenVotingAdapter openVotingAdapter = null;
    private String votingId = "";
    private Voting voting = null;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_open_voting);
        getActionBar().setTitle(R.string.open_votings);
        
        initializeVotingInfo();
        initializeGUI();
        initializeAdapter();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeVotingInfo() {
        votingId = getIntent().getExtras().getString("votingId");
        voting = VotingContainer.getInstance().findOpenVotingById(votingId);
    }
    
    private void initializeGUI() {
        TextView votingTitle = (TextView) findViewById(R.id.activityOpenVotingTitle);
        votingTitle.setText(voting.getTitle());
        
        TextView votingDescription = (TextView) findViewById(R.id.activityOpenVotingDescription);
        votingDescription.setText(voting.getDescription());
        
        TextView votingCreator = (TextView) findViewById(R.id.activityOpenVotingCreator);
        votingCreator.setText(this.getString(R.string.voting_created_by) + ": " + voting.getCreator());
        
        TextView votingEndTime = (TextView) findViewById(R.id.activityOpenVotingEndTime);
        votingEndTime.setText(this.getString(R.string.voting_ends) + ": " + voting.getEndTimeAsString());
    }

    private void initializeAdapter() {
        openVotingAdapter = new OpenVotingAdapter(this, voting);
        ListView votingOptionsList = (ListView) findViewById(R.id.activityOpenVotingListViewOptions);
        votingOptionsList.setAdapter(openVotingAdapter);
    }

}
