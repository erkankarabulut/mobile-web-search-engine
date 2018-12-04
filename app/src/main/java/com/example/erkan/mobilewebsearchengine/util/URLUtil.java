package com.example.erkan.mobilewebsearchengine.util;

import com.example.erkan.mobilewebsearchengine.beans.Page;

import java.util.ArrayList;

public class URLUtil {

    public boolean checkIfURLExist(ArrayList<Page> pageList, String url){
        Boolean result = false;
        for (Page page : pageList){
            if(page.getUrl().equals(url)){
                result = true;
                break;
            }
        }

        return result;
    }

    public Page getPageWithURL(ArrayList<Page> pageList, String url){
        Page result = null;
        for(Page page : pageList){
            if(page.getUrl().equals(url)){
                result = page;
            }
        }

        return result;
    }

}
