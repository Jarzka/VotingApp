package org.voimala.votingapp.connections.aani;


public interface AaniNetworkListener {
    // Called when got a correct AANI response from the server
    void aaniRequestCompleted(final String result, final AaniRequestType requestType);
    // Called when got a response from the server, but the response is unexpected.
    void aaniRequestFailedUnexpectedResponse(final AaniRequestType requestType);
    // Called when unable to establish connection to the server.
    void aaniRequestFailedUnableToConnect(final AaniRequestType requestType);
}
