package com.example.erkan.mobilewebsearchengine.action;

import com.example.erkan.mobilewebsearchengine.beans.Page;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HITSAlgorithm {

    public ArrayList<String> findOutgoingPages(String content){
        ArrayList<String> outgoingURLList = new ArrayList<>();
        String regex = "<a href\\s?=\\s?\"([^\"]+)\">";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        int index = 0;
        while (matcher.find(index)) {
            outgoingURLList.add(matcher.group(1));
            index = matcher.end();
        }

        System.out.println("Outgoing urls: " + outgoingURLList.toString());
        return outgoingURLList;
    }

    public ArrayList<Page> getBasePageSet(ArrayList<Page> rootSet){
        ArrayList<Page> baseSet = new ArrayList<>();

        baseSet.addAll(rootSet);
        for(Page page : rootSet){
            String pageContent = new String();

            try{
                //System.out.println("URL: " + page.getUrl());
                URL url = new URL(page.getUrl());
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                pageContent = IOUtils.toString(in, encoding);
            }catch (Exception e){
                e.printStackTrace();
            }

            findOutgoingPages(pageContent);
        }

        return baseSet;
    }

}
