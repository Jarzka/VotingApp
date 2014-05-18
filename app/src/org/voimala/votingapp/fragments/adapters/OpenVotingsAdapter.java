package org.voimala.votingapp.fragments.adapters;

import org.voimala.votingapp.R;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OpenVotingsAdapter extends BaseAdapter {
    
    private Context context = null;
    
    public OpenVotingsAdapter(final Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return VotingContainer.getInstance().getOpenVotings().size();
    }

    @Override
    public Object getItem(final int position) {
        return VotingContainer.getInstance().getOpenVotings().get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_open_votings, null);
            
            TextView votingTitle = (TextView) convertView.findViewById(R.id.listItemOpenVotingTitle);
            votingTitle.setText(VotingContainer.getInstance().getOpenVotings().get(position).getTitle());
            
            TextView votingDescription = (TextView) convertView.findViewById(R.id.listItemOpenVotingDescription);
            votingDescription.setText(VotingContainer.getInstance().getOpenVotings().get(position).getDescription());
            
            TextView votingCreator = (TextView) convertView.findViewById(R.id.listItemOpenVotingCreator);
            votingCreator.setText(context.getString(R.string.voting_created_by) + ": "
                    + VotingContainer.getInstance().getOpenVotings().get(position).getCreator());
            
            TextView votingEndTime = (TextView) convertView.findViewById(R.id.listItemOpenVotingEndTime);
            votingEndTime.setText(context.getString(R.string.voting_ends) + ": "
                        + VotingContainer.getInstance().getOpenVotings().get(position).getEndTimeAsString());

            return convertView;
        }
        
        return convertView;
    }

}
