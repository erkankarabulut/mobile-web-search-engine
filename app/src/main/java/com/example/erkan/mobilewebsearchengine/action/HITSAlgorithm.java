package com.example.erkan.mobilewebsearchengine.action;

import com.example.erkan.mobilewebsearchengine.beans.Page;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HITSAlgorithm {

    private final String agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

    public ArrayList<String> findOutgoingPages(String content){
        ArrayList<String> outgoingURLList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        int index = 0;
        while (matcher.find(index)) {
            if(!outgoingURLList.contains(matcher.group(1))){
                outgoingURLList.add(matcher.group(1));
            }
            index = matcher.end();
        }

        return outgoingURLList;
    }

    public ArrayList<Page> getBasePageSet(ArrayList<Page> rootSet){
        ArrayList<Page> baseSet             = new ArrayList<>();
        ArrayList<Page> pagesToBeRemoved    = new ArrayList<>(); // Pages that gives 404 or other errors

        baseSet.addAll(rootSet);
        URL url;
        URLConnection con;
        for(Page page : rootSet){
            String pageContent = new String();

            try{
                url             = new URL(page.getUrl());
                con             = url.openConnection();
                con.setConnectTimeout(500);
                con.setReadTimeout(500);
                con.setRequestProperty("User-Agent", agent);
                con.connect();

                InputStream in  = con.getInputStream();
                String encoding = con.getContentEncoding();
                encoding        = (encoding == null ? "UTF-8" : encoding);
                pageContent     = IOUtils.toString(in, encoding);

                ArrayList<String> outgoingPages = findOutgoingPages(pageContent);
                for(String outgoingPage : outgoingPages){
                    Page temp;

                    if(!checkIfURLExist(baseSet, outgoingPage)){
                        temp = new Page(outgoingPage);
                    }else {
                        temp = getPageWithURL(baseSet, outgoingPage);
                    }

                    baseSet.add(temp);
                    page.getOutgoingPageList().add(temp);
                    temp.getIncomingPageList().add(page);
                }
            }catch (Exception e){
                pagesToBeRemoved.add(page);
            }
        }

        baseSet.remove(pagesToBeRemoved);
        return baseSet;
    }

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
