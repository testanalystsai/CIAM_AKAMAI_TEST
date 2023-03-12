package com.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class DataModel {
    private String odiurl;
    private Map<String,String> clientID;

    public String getOdiurl() {
        return odiurl;
    }

    public void setOdiurl(String odiurl) {
        this.odiurl = odiurl;
    }

    public Map<String, String> getClientID() {
        return clientID;
    }

    public void setClientID(Map<String, String> clientID) {
        this.clientID = clientID;
    }
}
