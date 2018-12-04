package com.example.erkan.mobilewebsearchengine.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable{

    private String          url;
    private ArrayList<Page> outgoingPageList;
    private ArrayList<Page> incomingPageList;
    private Double         hubScore;
    private Double         authorityScore;

    public Page(String url) {
        this.url                = url;
        this.outgoingPageList   = new ArrayList<>();
        this.incomingPageList   = new ArrayList<>();
        this.hubScore           = 1.0;
        this.authorityScore     = 1.0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<Page> getOutgoingPageList() {
        return outgoingPageList;
    }

    public void setOutgoingPageList(ArrayList<Page> outgoingPageList) {
        this.outgoingPageList = outgoingPageList;
    }

    public ArrayList<Page> getIncomingPageList() {
        return incomingPageList;
    }

    public void setIncomingPageList(ArrayList<Page> incomingPageList) {
        this.incomingPageList = incomingPageList;
    }

    public Double getHubScore() {
        return hubScore;
    }

    public void setHubScore(Double hubScore) {
        this.hubScore = hubScore;
    }

    public Double getAuthorityScore() {
        return authorityScore;
    }

    public void setAuthorityScore(Double authorityScore) {
        this.authorityScore = authorityScore;
    }

}
