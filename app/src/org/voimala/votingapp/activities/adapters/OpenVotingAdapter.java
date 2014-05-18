package org.voimala.votingapp.activities.adapters;

import org.voimala.votingapp.R;
import org.voimala.votingapp.connections.aani.AaniConnection;
import org.voimala.votingapp.connections.aani.AaniNetworkListener;
import org.voimala.votingapp.connections.aani.AaniRequestType;
import org.voimala.votingapp.datasource.voting.Voting;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

public class OpenVotingAdapter extends BaseAdapter implements AaniNetworkListener {
    
    private Voting voting = null;
    private Context context = null;
    
    public OpenVotingAdapter(final Context context, final Voting voting) {
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
            convertView = inflater.inflate(R.layout.listitem_open_voting_option, null);
            
            Button votingOption = (Button) convertView.findViewById(R.id.buttonOption);
            votingOption.setText(voting.getOptions().get(position).getText());
            
            // Set listener
            votingOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (VotingContainer.getInstance().findVotingById(voting.getId()) != null
                            && voting.isOpen()) {
                        AaniConnection aaniConnection = new AaniConnection(OpenVotingAdapter.this);
                        aaniConnection.sendVote(voting.getId(), position);
                    } else {
                        Toast toast = Toast.makeText(context, R.string.toast_voting_ended, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                
            });

            return convertView;
        }
        
        return convertView;
    }

    @Override
    public void aaniRequestCompleted(final String result, final AaniRequestType aaniRequestType) {
        if (aaniRequestType == AaniRequestType.SEND_VOTE) {
            Toast toast = Toast.makeText(context, R.string.toast_vote_sent, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void aaniRequestFailedUnexpectedResponse(final AaniRequestType aaniRequestType) {
        Toast toast = Toast.makeText(context, R.string.toast_send_vote_failed, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void aaniRequestFailedUnableToConnect(final AaniRequestType aaniRequestType) {
        Toast toast = Toast.makeText(context, R.string.toast_send_vote_failed, Toast.LENGTH_LONG);
        toast.show();
    }

}
