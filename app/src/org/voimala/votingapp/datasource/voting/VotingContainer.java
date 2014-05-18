package org.voimala.votingapp.datasource.voting;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voimala.util.Rfc3339;
import org.voimala.votingapp.R;
import org.voimala.votingapp.connections.aani.AaniConnection;
import org.voimala.votingapp.connections.aani.AaniNetworkListener;
import org.voimala.votingapp.connections.aani.AaniRequestType;
import org.voimala.votingapp.fragments.adapters.ClosedVotingsAdapter;
import org.voimala.votingapp.fragments.adapters.OpenVotingsAdapter;
import org.voimala.votingapp.fragments.adapters.UpcomingVotingsAdapter;
import org.voimala.votingapp.services.NotificationService;
import org.voimala.votingapp.services.NotificationService.LocalBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

/** Container for all votings. */

public class VotingContainer implements AaniNetworkListener {
    
    private ArrayList<Voting> votings = new ArrayList<Voting>();
    private int openVotingsAmount = 0; // Used to check if a new open voting has been added.
    private boolean votingsLoadedAtLeastOnce = false;
    private static VotingContainer instanceOfThis = null;
    private OpenVotingsAdapter openVotingsAdapter = null;
    private ClosedVotingsAdapter closedVotingsAdapter = null;
    private UpcomingVotingsAdapter upcomingVotingsAdapter = null;
    private Context context = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private NotificationService notificationService = null;
    
    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            notificationService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
        }
    };
    
    public static VotingContainer getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new VotingContainer();
        }
        
        return instanceOfThis;
    }
    
    /** Initializes notification servie and requests the votings from the server. */
    public void initialize() {
        initializeNotificationService();
        
        Toast toast = Toast.makeText(context, R.string.toast_requesting_votings, Toast.LENGTH_LONG);
        toast.show();
        
        updateAllVotings();
    }
    
    private void initializeNotificationService() {
        Intent intent = new Intent(context, NotificationService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public final void setContext(final Context context) {
        this.context = context;
    }
    
    public final void setOpenVotingsAdapter(final OpenVotingsAdapter adapter) {
        openVotingsAdapter = adapter;
    }
    
    public final void setClosedVotingsAdapter(final ClosedVotingsAdapter adapter) {
        closedVotingsAdapter = adapter;
    }
    
    public final void setUpcomingVotingsAdapter(final UpcomingVotingsAdapter adapter) {
        upcomingVotingsAdapter = adapter;
    }

    public final void updateAllVotings() {
        votings.clear();
        AaniConnection aaniConnection = new AaniConnection(this);
        aaniConnection.requestVotings();
    }
    
    @Override
    public void aaniRequestCompleted(final String result, final AaniRequestType aaniRequestType) {
        if (aaniRequestType == AaniRequestType.GET_VOTINGS) {
            try {
                handleResponseVotings(result);
            } catch (JSONException e) {
                logger.log(Level.WARNING, "handleResponsveVoting caused an exception: " + e.getMessage());
                Toast toast = Toast.makeText(context, R.string.toast_votings_parse_error, Toast.LENGTH_LONG);
                toast.show();
            }
        } else if (aaniRequestType == aaniRequestType.GET_VOTING_RESULTS) {
            try {
                handleResponseVotingResults(result);
            } catch (JSONException e) {
                logger.log(Level.WARNING, "handleResponseVotingResults caused an exception: " + e.getMessage());
                Toast toast = Toast.makeText(context, R.string.toast_votings_parse_error, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public void aaniRequestFailedUnexpectedResponse(final AaniRequestType aaniRequestType) {
        if (aaniRequestType == AaniRequestType.GET_VOTINGS) {
            Toast toast = Toast.makeText(context, R.string.toast_unable_to_get_votings, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void aaniRequestFailedUnableToConnect(final AaniRequestType aaniRequestType) {
        if (aaniRequestType == AaniRequestType.GET_VOTINGS) {
            Toast toast = Toast.makeText(context, R.string.toast_unable_to_get_votings, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void handleResponseVotings(final String result) throws JSONException {
        votings = (ArrayList<Voting>) createVotingsFromJSON(result);
        requestVotingResultsForClosedVotings();
        
        checkNewOpenVotings();
        votingsLoadedAtLeastOnce = true;
        
        // Data changed, notify the adapters and the user
        notifyAdapters();
        Toast toast = Toast.makeText(context, R.string.toast_votings_updated, Toast.LENGTH_SHORT);
        toast.show();
    }

    /** Converts the voting server's response to the votings request to a list of Voting objects.
     * @param json String. The voting server's response for votings request.
     * */
    private List<Voting> createVotingsFromJSON(final String json) throws JSONException {
        ArrayList<Voting> votings = new ArrayList<Voting>();

        JSONArray jsonArrayVotings = new JSONArray(json);
        for (int i = 0; i < jsonArrayVotings.length(); i++) {
            JSONObject jsonObjectVoting = jsonArrayVotings.getJSONObject(i);
            
            String uuid = jsonObjectVoting.getString("id");
            String creator = jsonObjectVoting.getString("creator");
            String startTime = jsonObjectVoting.getString("start-time");
            String endTime = jsonObjectVoting.getString("end-time");
            String title = jsonObjectVoting.getString("title");
            String description = jsonObjectVoting.getString("text");
            
            // Parse options
            ArrayList<VotingOption> options = new ArrayList<VotingOption>();
            JSONArray jsonArrayVotingOptions = jsonObjectVoting.getJSONArray("options");
            for (int j = 0; j < jsonArrayVotingOptions.length(); j++) {
                VotingOption option = new VotingOption(jsonArrayVotingOptions.getString(j));
                options.add(option);
            }
            
            Voting voting = new Voting(uuid, title, options);
            try {
                voting.setStartTime(Rfc3339.parse(startTime));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Voting start time caused an exception: " + e.getMessage());
                continue; // Do not add an illegal voting object.
            }
            try {
                voting.setEndTime(Rfc3339.parse(endTime));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Voting end time caused an exception: " + e.getMessage());
                continue; // Do not add an illegal voting object.
            }
            voting.setDescription(description);
            voting.setCreator(creator);
            
            votings.add(voting);
        }
        
        return votings;
    }
    
    private void checkNewOpenVotings() {
        if (votingsLoadedAtLeastOnce) {
            // Did we get new votings?
            if (getOpenVotings().size() > openVotingsAmount) {
                showNotificationNewOpenVoting();
            }
        }
        
        openVotingsAmount = getOpenVotings().size();
    }

    private void showNotificationNewOpenVoting() {
        if (notificationService != null) {
            notificationService.showNotificationNewOpenVoting(); 
        }  
    }

    /** The voting server's response to the votings request does not include the voting results
     * so they have to be added to the voting objects separately.
     * This method asks the voting result for each CLOSED voting object from the server. */
    private void requestVotingResultsForClosedVotings() {
        for (Voting voting : votings) {
            if (voting.isClosed()) {
                AaniConnection aaniConnection = new AaniConnection(this);
                aaniConnection.requestVotingResults(voting.getId());
            }
        }
    }
    
    /** Adds the voting results to the corresponding voting object. */
    private void handleResponseVotingResults(final String json) throws JSONException {
        JSONObject jsonObjectVotingResults = new JSONObject(json);
        Voting voting = findClosedVotingById(jsonObjectVotingResults.getString("id"));
        for (int i = 0; i < voting.getOptions().size(); i++) {
            JSONArray jsonArrayVotingResults = jsonObjectVotingResults.getJSONArray("results");
            VotingOption votingOption = voting.getOptions().get(i);
            votingOption.setCount(jsonArrayVotingResults.getInt(i));
        }
    }

    /** Notifies the adapters about the changed data so that they update the view. */
    private void notifyAdapters() {
        if (openVotingsAdapter != null) {
            openVotingsAdapter.notifyDataSetChanged();
        }
        
        if (closedVotingsAdapter != null) {
            closedVotingsAdapter.notifyDataSetChanged();
        }
        
        if (upcomingVotingsAdapter != null) {
            upcomingVotingsAdapter.notifyDataSetChanged();
        }
    }
    
    public final ArrayList<Voting> getAllVotings() {
        return votings;
    }
    
    public final ArrayList<Voting> getOpenVotings() {
        ArrayList<Voting> openVotings = new ArrayList<Voting>();
        
        for (Voting voting : votings) {
            if (voting.isOpen()) {
                openVotings.add(voting);
            }
        }
        
        return openVotings;
    }
    
    /** @return Returns null if Voting is not found. */
    public final Voting findOpenVotingById(final String id) {
        for (Voting voting : votings) {
            if (voting.isOpen() && voting.getId().contains(id)) {
                return voting;
            }
        }
        
        return null;
    }
    
    public final ArrayList<Voting> getClosedVotings() {
        ArrayList<Voting> closedVotings = new ArrayList<Voting>();
        
        for (Voting voting : votings) {
            if (voting.isClosed()) {
                closedVotings.add(voting);
            }
        }
        
        return closedVotings;
    }
    
    /** @return Returns null if Voting is not found. */
    public final Voting findClosedVotingById(final String id) {
        for (Voting voting : votings) {
            if (voting.isClosed() && voting.getId().contains(id)) {
                return voting;
            }
        }
        
        return null;
    }

    public final ArrayList<Voting> getUpcomingVotings() {
        ArrayList<Voting> upcomingVotings = new ArrayList<Voting>();
        
        for (Voting voting : votings) {
            if (voting.isUpcoming()) {
                upcomingVotings.add(voting);
            }
        }
        
        return upcomingVotings;
    }
    
    /** @return Returns null if Voting is not found. */
    public final Voting findUpcomingVotingById(final String id) {
        for (Voting voting : votings) {
            if (voting.isUpcoming() && voting.getId().contains(id)) {
                return voting;
            }
        }
        
        return null;
    }

    /** @return Returns null if Voting is not found. */
    public final Voting findVotingById(final String id) {
        Voting voting = findClosedVotingById(id);
        
        if (voting != null) {
            return voting;
        }
        
        voting = findOpenVotingById(id);
        
        if (voting != null) {
            return voting;
        }
        
        return findUpcomingVotingById(id);
    }
    
}
