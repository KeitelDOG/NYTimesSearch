package com.megalobiz.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by KeitelRobespierre on 7/20/2016.
 */
public class Article {

    String webUrl;
    String headLine;
    String thumbnail;
    String snippet;
    String leadParagraph;
    String source;
    SimpleDateFormat pubDate;

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length() > 0) {
                // Best way to get the real thumbnail on NYTimes Search, cause it is specified
                // in the subtype inside of JSONObject in multimedia
                /*JSONObject thumbnailJson;
                for (int i = 0; i < multimedia.length()) {
                    if(multimedia.getJSONObject(i).getString("subtype").equals("thumbnail"))
                        this.thumbnail = multimedia.getJSONObject(i).getString("url");
                }*/

                //The way of the Tutorial to get thumbnail
                this.thumbnail = "http://www.nytimes.com/" + multimedia.getJSONObject(0).getString("url");
            } else {
                this.thumbnail = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getLeadParagraph() {
        return leadParagraph;
    }

    public String getSource() {
        return source;
    }

    public SimpleDateFormat getPubDate() {
        return pubDate;
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> articles = new ArrayList<Article>();

        for (int i = 0; i < array.length(); i++){
            try {
                articles.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return articles;
    }

}
