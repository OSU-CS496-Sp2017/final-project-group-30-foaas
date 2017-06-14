package com.example.android.group30FOAAS.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        try {
            JSONObject searchResultsObj = new JSONObject(searchResultsJSON);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("items");

            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();

            for (int i = 0; i < searchResultsItems.length(); i++) {
                SearchResult searchResult = new SearchResult();
                JSONObject searchResultItem = searchResultsItems.getJSONObject(i);
                searchResult.message = searchResultItem.getString("message");
                searchResult.subtitle = searchResultItem.getString("subtitle");
                searchResultsList.add(searchResult);
                Log.d(TAG, "VALID RUN: Json Response = " + searchResult);
            }
            return searchResultsList;
        } catch (JSONException e) {
            return null;
        }
    }
}
