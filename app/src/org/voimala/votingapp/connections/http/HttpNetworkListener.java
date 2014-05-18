package org.voimala.votingapp.connections.http;

public interface HttpNetworkListener {
    // Called when got a correct HTTP response from the server
    void httpRequestCompleted(final AaniHttpResponse httpResponse,
            final HttpRequestType httpRequestType);
    // Called when unable to establish connection to the server
    void httpRequestFailedUnableToConnect(final HttpRequestType httpRequestType);
}
