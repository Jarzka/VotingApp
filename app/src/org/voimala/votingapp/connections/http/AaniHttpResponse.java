package org.voimala.votingapp.connections.http;

/** This class is used when org.apache.http.HttpResponse can not be used since it may
 * throw NetworkOnMainThread exception even though it's used only for reading purposes. */

public class AaniHttpResponse {
    private int httpStatusCode = 0;
    private String content = "";
    
    public AaniHttpResponse(final String content, final int httpStatusCode) {
        this.content = content;
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getContent() {
        return content;
    }
    
    public boolean isEmpty() {
        return content.isEmpty();
    }
}
