package com.example.android.group30FOAAS.utils;

import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;


public class FOAASUtils {
    private static final String TAG = FOAASUtils.class.getSimpleName();


    //Variables for query
    private final static String FOAAS_URL = "http://foaas.com";


    public static class SearchResult implements Serializable {
        public static final String EXTRA_SEARCH_RESULT = "FOAASUtils.SearchResult";
        public String message;
        public String subtitle;
    }

    public static String buildFOAASURL(String type, String name, String from) {
        Uri.Builder builder = Uri.parse(FOAAS_URL).buildUpon();

        if(!type.equals("")) {
            builder.path(type);
        } else {
            Log.d(TAG, "ERROR: URI BUILD - Type contains nothing");
        }

        if(!name.equals("")) {
            builder.appendPath(name);
        }

        if(!from.equals("")) {
            builder.appendPath(from);
        } else {
            Log.d(TAG, "ERROR: URI BUILD - From contains nothing");
        }

        return builder.build().toString();
    }

    public static ArrayList<SearchResult> parseFOAASResultsJSON(String searchResultsJSON) {

            Document doc = Jsoup.parse(searchResultsJSON);
            Elements metaTags = doc.getElementsByTag("meta");
            String ret_content = null;

            for(Element metaTag: metaTags)
            {
                String content = metaTag.attr("content");
                String name = metaTag.attr("name");

                Log.d(TAG, "parseFOAASResultsJSON: name: " + name);

                if("twitter:description".equals(name))
                {
                    Log.d(TAG, "parseFOAASResultsJSON: FOUND: " + content);
                    ret_content = content;
                }
            }


            Log.d(TAG, "parseFOAASResultsJSON: searchResultsJSON: " + searchResultsJSON);
            Log.d(TAG, "parseFOAASResultsJSON: new string: " + ret_content);

            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();
            SearchResult searchResult = new SearchResult();
            searchResult.message = ret_content;
            searchResultsList.add(searchResult);

            //for (int i = 0; i < searchResultsItems.length(); i++) {
            //    SearchResult searchResult = new SearchResult();
            //    JSONObject searchResultItem = searchResultsItems.getJSONObject(i);
            //    searchResult.message = searchResultItem.getString("message");
            //    searchResult.subtitle = searchResultItem.getString("subtitle");
            //    searchResultsList.add(searchResult);
            //    Log.d(TAG, "VALID RUN: Json Response = " + searchResult);
            //}
            return searchResultsList;
    }
}
