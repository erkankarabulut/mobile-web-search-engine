package com.example.erkan.mobilewebsearchengine.action;

import com.example.erkan.mobilewebsearchengine.beans.Page;
import com.example.erkan.mobilewebsearchengine.util.URLUtil;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HITSAlgorithm {

    private final String agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    private URLUtil urlUtil;

    public HITSAlgorithm(){
        urlUtil = new URLUtil();
    }

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

                    if(!urlUtil.checkIfURLExist(baseSet, outgoingPage)){
                        temp = new Page(outgoingPage);
                    }else {
                        temp = urlUtil.getPageWithURL(baseSet, outgoingPage);
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

    public ArrayList<String> applyHITSAlgorithm(ArrayList<Page> basePageSet, List<String> links){
        // Authority and hubs values of all pages are initially set to 1 while creating them
        Integer k = new Integer(3);  // Run the algoritm for 10 steps
        Double norm;                       // Normalization variable
        for(int i=0; i<k; i++){
            norm = new Double(0);
            for (Page page : basePageSet){
                page.setAuthorityScore(0.0);
                for(Page incoming : page.getIncomingPageList()){
                    page.setAuthorityScore(page.getAuthorityScore() + incoming.getHubScore());
                }

                norm += Math.sqrt(page.getAuthorityScore());
            }

            norm = Math.sqrt(norm);
            for (Page page : basePageSet){
                page.setAuthorityScore((page.getAuthorityScore() / norm));
            }

            norm = 0.0;
            for (Page page : basePageSet){
                page.setHubScore(0.0);
                for(Page outgoing : page.getOutgoingPageList()){
                    page.setHubScore(page.getHubScore() + outgoing.getAuthorityScore());
                }

                norm += Math.sqrt(page.getHubScore());
            }

            norm = Math.sqrt(norm);
            for(Page page : basePageSet){
                page.setHubScore((page.getHubScore() / norm));
            }
        }

        Double hubsAuthorityDifference;
        Double max;
        for(int i=0; i<basePageSet.size()-1; i++){
            max = Math.abs(basePageSet.get(i).getAuthorityScore() - basePageSet.get(i).getHubScore());
            for(int j=i+1; j<basePageSet.size(); j++){
               hubsAuthorityDifference = Math.abs(basePageSet.get(j).getAuthorityScore() - basePageSet.get(j).getHubScore());
               if(max < hubsAuthorityDifference){
                   Page temp = basePageSet.get(j);
                   basePageSet.set(j, basePageSet.get(i));
                   basePageSet.set(i, temp);

                   max = hubsAuthorityDifference;
               }
            }
        }

        ArrayList<String> urlList = new ArrayList<>();
        for(Page page : basePageSet){
            if(links.contains(page.getUrl())){
                urlList.add(page.getUrl());
            }
        }

        return urlList;
    }

}
