package org.voimala.votingapp.connections.http;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;


class HttpPutTask extends AsyncTask<String, String, AaniHttpResponse> {
    
    private HttpNetworkListener httpNetworkListener = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private String data = "";
    
    public HttpPutTask(final HttpNetworkListener httpNetworkListener, final String data) {
        this.httpNetworkListener = httpNetworkListener;
        this.data = data;
    }
    
    @Override
    /** @return String "OK" if the request was ok, or empty string if the request failed. */
    protected AaniHttpResponse doInBackground(final String... url) {
        try {
            HttpPut httpPut = new HttpPut(url[0]);
            StringEntity stringEntity = new StringEntity(data);
            httpPut.setEntity(stringEntity);
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpResponse httpResponse = httpclient.execute(httpPut);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            String result = "";
            try {
                httpResponse.getEntity().writeTo(out);
                out.close();
                result = out.toString();   
            } catch (Exception e) {
                // Content may be empty. Continue...
            }
            
            AaniHttpResponse aaniHttpResponse = new AaniHttpResponse(result,
                    httpResponse.getStatusLine().getStatusCode());
            return aaniHttpResponse;
        } catch (SocketTimeoutException e) {
            logger.log(Level.WARNING, "HTTP GET caused an exception: " + e.getMessage());
            return null;
        }  catch (Exception e) {
            logger.log(Level.WARNING, "HTTP GET caused an exception: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(final AaniHttpResponse httpResponse) {
        super.onPostExecute(httpResponse);
        
        if (httpNetworkListener != null) {
            if (httpResponse != null) {
                httpNetworkListener.httpRequestCompleted(httpResponse, HttpRequestType.PUT);  
            } else {
                httpNetworkListener.httpRequestFailedUnableToConnect(HttpRequestType.PUT);
            }
        }
    }

}
