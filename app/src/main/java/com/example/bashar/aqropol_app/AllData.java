package com.example.bashar.aqropol_app;


import java.util.HashMap;

/**
 * Created by admin on 21/03/2018.
 */

class AllData {

    private String urlnuc="http://10.42.0.1:8080/nucs";
    private String urlmeasures="http://10.42.0.1:8080/measures";
    private HashMap<String, byte[]> hashTab = new HashMap<>();

    private String RequestedMethod="";
    private String request="";

    public AllData(String requestedMethod, String request) {
        this.RequestedMethod = requestedMethod;
        this.request = request;
    }

    public String getUrlnuc() {
        return urlnuc;
    }

    public void setUrlnuc(String urlnuc) {
        this.urlnuc = urlnuc;
    }

    public String getUrlmeasures() {
        return urlmeasures;
    }

    public void setUrlmeasures(String urlmeasures) {
        this.urlmeasures = urlmeasures;
    }

    public String getRequestedMethod() {
        return RequestedMethod;
    }

    public void setRequestedMethod(String requestedMethod) {
        RequestedMethod = requestedMethod;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setHashTab(HashMap<String, byte[]> hashTab) {
        this.hashTab = hashTab;
    }

    public HashMap<String, byte[]> getHashTab() {
        return hashTab;
    }
}
