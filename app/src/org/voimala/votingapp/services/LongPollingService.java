package org.voimala.votingapp.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.voimala.votingapp.connections.aani.AaniConnection;
import org.voimala.votingapp.connections.aani.AaniNetworkListener;
import org.voimala.votingapp.connections.aani.AaniRequestType;
import org.voimala.votingapp.connections.http.HttpNetworkListener;
import org.voimala.votingapp.datasource.voting.VotingContainer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LongPollingService extends Service implements AaniNetworkListener {
    
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final int requestDelayMs = 5000;
    
    @Override
    public void onCreate() {
    }
    
    public final int onStartCommand(final Intent intent, final int flags, final int startId) {
        requestVotingsUsingLongPolling();
        
        return START_NOT_STICKY;
      }
    
    /** Requests votings using long polling after requestDelayMs milliseconds have passed.
     * This delay has been implemented so that the service does not overload the server too much. */
    private void requestVotingsUsingLongPolling() {
        Timer timer = new Timer();
        timer.schedule(new RequestVotingsUsingLongPollingTask(this), requestDelayMs); 
    }

    class RequestVotingsUsingLongPollingTask extends TimerTask {
        private LongPollingService longPollingService;
        
        public RequestVotingsUsingLongPollingTask(final LongPollingService longPollingService) {
            this.longPollingService = longPollingService;
        }
        
        @Override
        public void run() {
            AaniConnection aaniConnectionManager = new AaniConnection(LongPollingService.this);
            aaniConnectionManager.requestVotingsUsingLongPolling();
        }
    }
    
    /** Requests votings after requestDelayMs milliseconds have passed.
     * This delay has been implemented so that the service does not overload the server too much. */
    private void requestVotings() {
        Timer timer = new Timer();
        timer.schedule(new RequestVotingsTask(this), requestDelayMs); 
    }

    class RequestVotingsTask extends TimerTask {
        private LongPollingService longPollingService;
        
        public RequestVotingsTask(final LongPollingService longPollingService) {
            this.longPollingService = longPollingService;
        }
        
        @Override
        public void run() {
            AaniConnection aaniConnectionManager = new AaniConnection(LongPollingService.this);
            aaniConnectionManager.requestVotings();
        }
    }

    @Override
    public IBinder onBind(final Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void aaniRequestCompleted(final String result, final AaniRequestType aaniRequestType) {
        logger.log(Level.INFO, "We got a long poll response from the server: " + result);
        VotingContainer.getInstance().aaniRequestCompleted(result,
                AaniRequestType.GET_VOTINGS);
        requestVotingsUsingLongPolling();
    }

    @Override
    public void aaniRequestFailedUnexpectedResponse(final AaniRequestType aaniRequestType) {
        if (!VotingContainer.getInstance().getAllVotings().isEmpty()) {
            requestVotingsUsingLongPolling();
        } else {
            /* Voting container is empty, which means that we have not received any votings.
             * Send the next request without long polling. */
            requestVotings();
        }
    }

    @Override
    public void aaniRequestFailedUnableToConnect(final AaniRequestType aaniRequestType) {
        if (!VotingContainer.getInstance().getAllVotings().isEmpty()) {
            requestVotingsUsingLongPolling();
        } else {
            /* Voting container is empty, which means that we have not received any votings.
             * Send the next request without long polling. */
            requestVotings();
        }
    }

}
