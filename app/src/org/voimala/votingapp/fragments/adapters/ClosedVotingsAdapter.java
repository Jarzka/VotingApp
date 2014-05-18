package org.voimala.votingapp.fragments.adapters;

import org.voimala.votingapp.R;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ClosedVotingsAdapter extends BaseAdapter {
    
    private Context context = null;
    
    public ClosedVotingsAdapter(final Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return VotingContainer.getInstance().getClosedVotings().size();
    }

    @Override
    public Object getItem(final int position) {
        return VotingContainer.getInstance().getClosedVotings().get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_closed_votings, null);
            
            TextView votingTitle = (TextView) convertView.findViewById(R.id.listItemClosedVotingTitle);
            votingTitle.setText(VotingContainer.getInstance().getClosedVotings().get(position).getTitle());
            
            TextView votingDescription = (TextView) convertView.findViewById(R.id.listItemClosedVotingDescription);
            votingDescription.setText(VotingContainer.getInstance().getClosedVotings().get(position).getDescription());
            
            TextView votingCreator = (TextView) convertView.findViewById(R.id.listItemClosedVotingCreator);
            votingCreator.setText(context.getString(R.string.voting_created_by) + ": "
                    + VotingContainer.getInstance().getClosedVotings().get(position).getCreator());
           
            TextView votingStartTime = (TextView) convertView.findViewById(R.id.listItemClosedVotingStartTime);
            votingStartTime.setText(context.getString(R.string.voting_started) + ": "
                    + VotingContainer.getInstance().getClosedVotings().get(position).getStartTimeAsString());
            
            TextView votingEndTime = (TextView) convertView.findViewById(R.id.listItemClosedVotingEndTime);
            votingEndTime.setText(context.getString(R.string.voting_ended) + ": "
                    + VotingContainer.getInstance().getClosedVotings().get(position).getEndTimeAsString());

            return convertView;
        }
        
        return convertView;
    }

}
