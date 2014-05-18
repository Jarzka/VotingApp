package org.voimala.votingapp.connections.http;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;


class HttpGetLongPollTask extends AsyncTask<String, String, AaniHttpResponse> {
    
    private HttpNetworkListener httpNetworkListener = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    
    public HttpGetLongPollTask(final HttpNetworkListener httpNetworkListener) {
        this.httpNetworkListener = httpNetworkListener;
    }
    
    @Override
    /** @return String The result of the request, or empty string if the request failed. */
    protected AaniHttpResponse doInBackground(final String... url) {
        try {
            HttpGet httpGet = new HttpGet(url[0]);
            httpGet.setHeader("When-Modified-After", getHttpDate());
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 900000; // 15 minutes
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(out);
            out.close();
            String result = out.toString();
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
    
    private String getHttpDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        return dateFormat.format(calendar.getTime());
    }

    @Override
    protected void onPostExecute(final AaniHttpResponse httpResponse) {
        super.onPostExecute(httpResponse);
        
        if (httpNetworkListener != null) {
            if (httpResponse != null) {
                httpNetworkListener.httpRequestCompleted(httpResponse, HttpRequestType.GET_MODIFIED_AFTER);  
            } else {
                httpNetworkListener.httpRequestFailedUnableToConnect(HttpRequestType.GET_MODIFIED_AFTER);
            }
        }
    }

}
