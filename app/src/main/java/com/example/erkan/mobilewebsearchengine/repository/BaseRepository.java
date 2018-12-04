package com.example.erkan.mobilewebsearchengine.repository;

import com.example.erkan.mobilewebsearchengine.action.HITSAlgorithm;
import com.example.erkan.mobilewebsearchengine.beans.Page;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.erkan.mobilewebsearchengine.activities.MainActivity.result;
import static com.example.erkan.mobilewebsearchengine.activities.MainActivity.total;

public class BaseRepository {

    private final String agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    private Long rootSetTime;
    private Long baseSetTime;
    private Long hitsTime;

    /**
     * Method to convert the {@link InputStream} to {@link String}
     *
     * @param is
     *            the {@link InputStream} object
     * @return the {@link String} object returned
     */
    public static String getString(InputStream is) {
        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /** finally block to close the {@link BufferedReader} */
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * The method will return the search page result in a {@link String} object
     *
     * @param path
     *            the google search query
     * @return the content as {@link String} object
     * @throws Exception
     */
    public static String getSearchContent(String path) throws Exception {
        final String agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
        URL url = new URL(path);
        final URLConnection connection = url.openConnection();
        connection.setReadTimeout(500);
        connection.setConnectTimeout(500);
        /**
         * User-Agent is mandatory otherwise Google will return HTTP response
         * code: 403
         */
        connection.setRequestProperty("User-Agent", agent);
        final InputStream stream = connection.getInputStream();
        return getString(stream);
    }

    /**
     * Parse all links
     *
     * @param html
     *            the page
     * @return the list with all URLSs
     * @throws Exception
     */
    public static List<String> parseLinks(final String html) throws Exception {
        List<String> result = new ArrayList<String>();
        String pattern1 = "<h3 class=\"r\"><a href=\"/url?q=";
        String pattern2 = "\">";
        Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        Matcher m = p.matcher(html);

        while (m.find()) {
            String domainName = m.group(0).trim();

            /** remove the unwanted text */
            domainName = domainName.substring(domainName.indexOf("/url?q=") + 7);
            domainName = domainName.substring(0, domainName.indexOf("&amp;"));

            result.add(domainName);
        }
        return result;
    }

    public ArrayList<ArrayList<String>> getHeaders(ArrayList<String> links){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> headers           = new ArrayList<>();
        ArrayList<String> brokenLinks       = new ArrayList<>();
        URL url;
        URLConnection con;
        String pageContent;
        Document document;

        for(String link : links){
            try {
                url             = new URL(link);
                con             = url.openConnection();
                con.setConnectTimeout(500);
                con.setReadTimeout(500);
                con.setRequestProperty("User-Agent", agent);
                con.connect();

                InputStream in  = con.getInputStream();
                String encoding = con.getContentEncoding();
                encoding        = (encoding == null ? "UTF-8" : encoding);
                pageContent     = IOUtils.toString(in, encoding);

                document = Jsoup.parse(pageContent);
                headers.add(document.title());
            }catch (Exception e){
                brokenLinks.add(link);
                e.printStackTrace();
            }
        }

        links.removeAll(brokenLinks);
        result.add(links);
        result.add(headers);

        return result;
    }

    public ArrayList<String> getFinalUrlList(String inputString){
        long start;
        HITSAlgorithm hitsAlgorithm = new HITSAlgorithm();
        ArrayList<Page> rootPageSet = new ArrayList<>();
        ArrayList<Page> basePageSet = new ArrayList<>();
        ArrayList<String> urlList   = new ArrayList<>();

        try {
            inputString = inputString.replaceAll(" ", "+");
            String query = "https://www.google.com/search?q=" + inputString + "&num=" + total;
            String page = BaseRepository.getSearchContent(query);
            List<String> links = BaseRepository.parseLinks(page);

            start = System.currentTimeMillis();
            rootPageSet = new ArrayList<>();
            for (int i=0; i<links.subList(0, (result > links.size() ? links.size() : result)).size(); i++) {
                rootPageSet.add(new Page(links.get(i)));
            }

            setRootSetTime((System.currentTimeMillis() - start));
            System.out.println("Time to create root set: " + (System.currentTimeMillis() - start)
                    + "\nRoot set element count: " + rootPageSet.size());
            start = System.currentTimeMillis();
            basePageSet = hitsAlgorithm.getBasePageSet(rootPageSet);

            setBaseSetTime((System.currentTimeMillis() - start));
            System.out.println("Time to create base set: " + (System.currentTimeMillis() - start)
                    + "\nBase set element count: " + basePageSet.size());

            start = System.currentTimeMillis();
            urlList = hitsAlgorithm.applyHITSAlgorithm(basePageSet, links);
            setHitsTime((System.currentTimeMillis() - start));
            System.out.println("Time to apply HITS algorithm on base page set: " +
                    (System.currentTimeMillis() - start));
        }catch (Exception e){
            e.printStackTrace();
        }

        return urlList;
    }

    public Long getRootSetTime() {
        return rootSetTime;
    }

    public void setRootSetTime(Long rootSetTime) {
        this.rootSetTime = rootSetTime;
    }

    public Long getBaseSetTime() {
        return baseSetTime;
    }

    public void setBaseSetTime(Long baseSetTime) {
        this.baseSetTime = baseSetTime;
    }

    public Long getHitsTime() {
        return hitsTime;
    }

    public void setHitsTime(Long hitsTime) {
        this.hitsTime = hitsTime;
    }
}
