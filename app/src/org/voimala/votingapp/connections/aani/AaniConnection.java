package org.voimala.votingapp.connections.aani;

import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.voimala.votingapp.connections.http.HttpConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

/** This class is used for communicating with the server using AANI protocol. */

public class AaniConnection implements AaniNetworkListener {
    
    private AaniNetworkListener aaniNetworkListener = null;
    private static Context context = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private String deviceUuid = "";
    
    public AaniConnection(final AaniNetworkListener aaniNetworkListener) {
        this.aaniNetworkListener = aaniNetworkListener;
    }
    
    public static final void setContext(final Context appContext) {
        context = appContext;
    }

    public final void requestVotings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = sharedPreferences.getString("server_url", "");
        serverUrl += "votings";
        
        HttpConnection httpConnection = new HttpConnection(this, AaniRequestType.GET_VOTINGS);
        httpConnection.sendGet(serverUrl);
    }
    
    public final void requestVotingResults(final String votingId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = sharedPreferences.getString("server_url", "");
        serverUrl += "votings/" + votingId + "/results";
        
        HttpConnection httpConnection = new HttpConnection(this, AaniRequestType.GET_VOTING_RESULTS);
        httpConnection.sendGet(serverUrl);
    }
    
    public final void requestVotingsUsingLongPolling() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = sharedPreferences.getString("server_url", "");
        serverUrl += "votings";
        
        HttpConnection httpConnection = new HttpConnection(this, AaniRequestType.GET_VOTINGS_LONG_POLL);
        httpConnection.sendGetLongPoll(serverUrl);
    }

    public final void sendVote(final String votingId,
            final int optionNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String serverUrl = sharedPreferences.getString("server_url", "");
        String voteId = getDeviceUUID();
        serverUrl += "votings/" + votingId + "/votes/" + voteId;
        
        String jsonToBeSent = constructVotingOptionJSON(optionNumber);
        
        HttpConnection httpConnection = new HttpConnection(this, AaniRequestType.SEND_VOTE);
        httpConnection.sendPut(serverUrl, jsonToBeSent);
    }
    
    private String getDeviceUUID() {
        if (deviceUuid.isEmpty()) {
            createDeviceUuid();
        }
        
        return deviceUuid;
    }

    private void createDeviceUuid() {
        String deviceId = Secure.ANDROID_ID;
        String uuidTemplate = "xxxxxxxx-xxxx-4xxx-axxx-xxxxxxxxxxxx";
        StringBuilder uuid = new StringBuilder();
        // Generate uuid from deviceId
        for (int i = 0; i < uuidTemplate.length(); i++) {
            if (uuidTemplate.charAt(i) == 'x') {
                try {
                    /* Convert the corresponding deviceID char to int (first digit of ascii code)
                     * and append it to the uuid. */
                    uuid.append(String.valueOf((int) deviceId.charAt(i)).substring(0, 1));
                } catch (IndexOutOfBoundsException e) {
                    uuid.append(String.valueOf((int) deviceId.charAt(deviceId.length() - 1)).substring(0, 1));
                }
            } else {
                uuid.append(uuidTemplate.charAt(i));
            }
        }
        
        this.deviceUuid = uuid.toString();
    }

    private String constructVotingOptionJSON(final int optionNumber) {
        JSONObject jsonObjectVote = null;
        try {
            jsonObjectVote = new JSONObject();
            jsonObjectVote.put("option", optionNumber);
        } catch (JSONException e) {
            // Continue.
        }
        
        return jsonObjectVote.toString();
    }

    @Override
    public void aaniRequestCompleted(final String result, final AaniRequestType aaniRequestType) {
        if (aaniNetworkListener != null) {
            aaniNetworkListener.aaniRequestCompleted(result, aaniRequestType);
        }
    }

    @Override
    public void aaniRequestFailedUnexpectedResponse(final AaniRequestType aaniRequestType) {
        if (aaniNetworkListener != null) {
            aaniNetworkListener.aaniRequestFailedUnexpectedResponse(aaniRequestType);
        }
    }

    @Override
    public void aaniRequestFailedUnableToConnect(final AaniRequestType aaniRequestType) {
        if (aaniNetworkListener != null) {
            aaniNetworkListener.aaniRequestFailedUnableToConnect(aaniRequestType);
        }
    }

}
