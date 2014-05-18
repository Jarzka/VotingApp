package org.voimala.votingapp.connections.http;

/** This class is used for communicating with the server using HTTP (and nonstandard HTTP) protocol. */

import org.apache.http.HttpStatus;
import org.voimala.votingapp.connections.aani.AaniNetworkListener;
import org.voimala.votingapp.connections.aani.AaniRequestType;

import android.os.AsyncTask;
public class HttpConnection implements HttpNetworkListener {
    
    private AaniNetworkListener aaniNetworkListener = null;
    private AaniRequestType aaniRequestType = null;
    
    public HttpConnection(final AaniNetworkListener aaniNetworkListener,
            final AaniRequestType aaniRequestType) {
        this.aaniNetworkListener = aaniNetworkListener;
        this.aaniRequestType = aaniRequestType;
    }
    
    public void sendGet(final String serverUrl) {
        HttpGetTask httpRequestTask = new HttpGetTask(this);
        httpRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverUrl);
    }
    
    public void sendGetLongPoll(final String serverUrl) {
        HttpGetLongPollTask httpGetLongPollTask = new HttpGetLongPollTask(this);
        httpGetLongPollTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverUrl);
    }
    
    public void sendPut(final String serverUrl, final String data) {
        HttpPutTask httpPostTask = new HttpPutTask(this, data);
        httpPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverUrl);
    }

    @Override
    public void httpRequestCompleted(final AaniHttpResponse httpResponse,
            final HttpRequestType httpRequestType) {
        if (aaniNetworkListener != null) {
            if (aaniRequestType == AaniRequestType.GET_VOTING_RESULTS) {
                handleResponseRequestVotingResult(httpResponse);
            } else if (aaniRequestType == AaniRequestType.GET_VOTINGS) {
                handleResponseRequestVotings(httpResponse);
            } else if (aaniRequestType == AaniRequestType.GET_VOTINGS_LONG_POLL) {
                handleResponseRequestVotingsUsingLongPoll(httpResponse);
            } else if (aaniRequestType == AaniRequestType.SEND_VOTE) {
                handleResponseSendVote(httpResponse);
            }
        }

    }

    private void handleResponseRequestVotingResult(final AaniHttpResponse httpResponse) {
        if (httpResponse.getHttpStatusCode() == HttpStatus.SC_OK) {
            aaniNetworkListener.aaniRequestCompleted(httpResponse.getContent(), aaniRequestType);
        } else {
            aaniNetworkListener.aaniRequestFailedUnexpectedResponse(aaniRequestType);
        }
    }

    private void handleResponseRequestVotings(final AaniHttpResponse httpResponse) {
        if (httpResponse.getHttpStatusCode() == HttpStatus.SC_OK) {
            aaniNetworkListener.aaniRequestCompleted(httpResponse.getContent(), aaniRequestType);
        } else {
            aaniNetworkListener.aaniRequestFailedUnexpectedResponse(aaniRequestType);
        }
    }

    private void handleResponseRequestVotingsUsingLongPoll(final AaniHttpResponse httpResponse) {
        if (httpResponse.getHttpStatusCode() == HttpStatus.SC_OK) {
            aaniNetworkListener.aaniRequestCompleted(httpResponse.getContent(), aaniRequestType);
        } else {
            aaniNetworkListener.aaniRequestFailedUnexpectedResponse(aaniRequestType);
        }
    }

    private void handleResponseSendVote(final AaniHttpResponse httpResponse) {
        if (httpResponse.getHttpStatusCode() == HttpStatus.SC_OK
                || httpResponse.getHttpStatusCode() == HttpStatus.SC_NO_CONTENT) {
            aaniNetworkListener.aaniRequestCompleted(httpResponse.getContent(), aaniRequestType);
        } else {
            aaniNetworkListener.aaniRequestFailedUnexpectedResponse(aaniRequestType);
        }
    }

    @Override
    public void httpRequestFailedUnableToConnect(final HttpRequestType httpRequestType) {
        if (aaniNetworkListener != null) {
            aaniNetworkListener.aaniRequestFailedUnableToConnect(aaniRequestType);
        }
    }

}
