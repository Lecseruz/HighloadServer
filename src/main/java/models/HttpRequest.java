package models;

import java.util.Vector;

public class HttpRequest {
    private String method;
    private String uri;
    private Vector<PairNameValue> headers;
    private Vector<PairNameValue> queryParameters;
    private int httpVersionMajor;
    private int httpVersionMinor;

    public HttpRequest(String method, String uri, Vector<PairNameValue> headers, Vector<PairNameValue> queryParameters, int httpVersionMajor, int httpVersionMinor){
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.httpVersionMajor = httpVersionMajor;
        this.httpVersionMinor = httpVersionMinor;
    }

    @Override
    public String toString() {
        return "method " + method + "\n" +
                "uri " + uri + "\n" +
                "httpMajor " + httpVersionMajor + "\n" +
                "httpminor " + httpVersionMinor + "\n" +
                (queryParameters == null ? "query" + queryParameters.lastElement().getName() : "");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Vector<PairNameValue> getHeaders() {
        return headers;
    }

    public void setHeaders(Vector<PairNameValue> headers) {
        this.headers = headers;
    }

    public Vector<PairNameValue> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Vector<PairNameValue> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public int getHttpVersionMajor() {
        return httpVersionMajor;
    }

    public void setHttpVersionMajor(int httpVersionMajor) {
        this.httpVersionMajor = httpVersionMajor;
    }

    public int getHttpVersionMinor() {
        return httpVersionMinor;
    }

    public void setHttpVersionMinor(int httpVersionMinor) {
        this.httpVersionMinor = httpVersionMinor;
    }


    public void addHeader(PairNameValue header) {
        headers.add(header);
    }


    public void pushBackMethod(char ch) {
        method += ch;
    }

    public void addQueryParameter(PairNameValue parameter) {
        queryParameters.add(parameter);
    }

    public void pushBackUri(char ch) {
        uri += ch;
    }
}
