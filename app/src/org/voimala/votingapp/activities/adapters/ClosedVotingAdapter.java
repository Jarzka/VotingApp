package org.voimala.votingapp.activities.adapters;

import org.voimala.votingapp.R;
import org.voimala.votingapp.datasource.voting.Voting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ClosedVotingAdapter extends BaseAdapter {
    
    private Voting voting = null;
    private Context context = null;
    
    public ClosedVotingAdapter(final Context context, final Voting voting) {
        this.context = context;
        this.voting = voting;
    }

    @Override
    public int getCount() {
        return voting.getOptions().size();
    }

    @Override
    public Object getItem(final int position) {
        return voting.getOptions().get(position);
    }

    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_closed_voting_result, null);
            
            TextView optionText = (TextView) convertView.findViewById(R.id.optionText);
            optionText.setText(voting.getOptions().get(position).getText() + " (" +
                    String.valueOf(voting.getOptions().get(position).getCount()) + ")");
            
            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.optionProgressBar);
            progressBar.setMax(voting.getNumberOfVotes());
            progressBar.setProgress(voting.getOptions().get(position).getCount());
            
            return convertView;
        }
        
        return convertView;
    }

}
