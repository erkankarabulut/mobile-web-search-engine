package com.example.erkan.mobilewebsearchengine.beans;

import java.util.ArrayList;

public class Page {

    private String          url;
    private ArrayList<Page> outgoingPageList;
    private ArrayList<Page> incomingPageList;
    private Integer         hubScore;
    private Integer         authorityScore;

    public Page(String url) {
        this.url                = url;
        this.outgoingPageList   = new ArrayList<>();
        this.incomingPageList   = new ArrayList<>();
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

    public Integer getHubScore() {
        return hubScore;
    }

    public void setHubScore(Integer hubScore) {
        this.hubScore = hubScore;
    }

    public Integer getAuthorityScore() {
        return authorityScore;
    }

    public void setAuthorityScore(Integer authorityScore) {
        this.authorityScore = authorityScore;
    }
}
