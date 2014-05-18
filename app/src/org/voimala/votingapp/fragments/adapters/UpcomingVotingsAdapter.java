package org.voimala.votingapp.fragments.adapters;

import org.voimala.votingapp.R;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class UpcomingVotingsAdapter extends BaseAdapter {
    
    private Context context = null;
    
    public UpcomingVotingsAdapter(final Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return VotingContainer.getInstance().getUpcomingVotings().size();
    }

    @Override
    public Object getItem(final int position) {
        return VotingContainer.getInstance().getUpcomingVotings().get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_upcoming_votings, null);
            
            TextView votingTitle = (TextView) convertView.findViewById(R.id.listItemUpcomingVotingTitle);
            votingTitle.setText(VotingContainer.getInstance().getUpcomingVotings().get(position).getTitle());
            
            TextView votingDescription = (TextView) convertView.findViewById(R.id.listItemUpcomingVotingDescription);
            votingDescription.setText(VotingContainer.getInstance().getUpcomingVotings().get(position).getDescription());
            
            TextView votingCreator = (TextView) convertView.findViewById(R.id.listItemUpcomingVotingCreator);
            votingCreator.setText(context.getString(R.string.voting_created_by) + ": "
                    + VotingContainer.getInstance().getUpcomingVotings().get(position).getCreator());
            
            TextView votingStartTime = (TextView) convertView.findViewById(R.id.listItemUpcomingVotingStartTime);
            votingStartTime.setText(context.getString(R.string.voting_starts) + ": "
                    + VotingContainer.getInstance().getUpcomingVotings().get(position).getStartTimeAsString());

            return convertView;
        }
        
        return convertView;
    }

}
