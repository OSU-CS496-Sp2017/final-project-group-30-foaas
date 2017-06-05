package com.example.android.group30FOAAS.utils;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hessro on 4/25/17.
 */

public class FOAASUtils {

    private final static String GITHUB_SEARCH_BASE_URL = "https://api.github.com/search/repositories";
    private final static String GITHUB_SEARCH_QUERY_PARAM = "q";
    private final static String GITHUB_SEARCH_SORT_PARAM = "sort";
    private final static String GITHUB_SEARCH_LANGUAGE_PARAM = "language";
    private final static String GITHUB_SEARCH_USER_PARAM = "user";
    private final static String GITHUB_SEARCH_IN_PARAM = "in";
    private final static String GITHUB_SEARCH_IN_NAME = "name";
    private final static String GITHUB_SEARCH_IN_DESCRIPTION = "description";
    private final static String GITHUB_SEARCH_IN_README = "readme";

    public static class SearchResult implements Serializable {
        public static final String EXTRA_SEARCH_RESULT = "FOAASUtils.SearchResult";
        public String fullName;
        public String description;
        public String htmlURL;
        public int stars;
    }

    public static String buildGitHubSearchURL(String searchQuery, String sort, String language,
                                              String user, boolean searchInName,
                                              boolean searchInDescription, boolean searchInReadme) {

        Uri.Builder builder = Uri.parse(GITHUB_SEARCH_BASE_URL).buildUpon();

        if (!sort.equals("")) {
            builder.appendQueryParameter(GITHUB_SEARCH_SORT_PARAM, sort);
        }

        String queryValue = searchQuery;
        if (!language.equals("")) {
            queryValue += " " + GITHUB_SEARCH_LANGUAGE_PARAM + ":" + language;
        }

        if (!user.equals("")) {
            queryValue += " " + GITHUB_SEARCH_USER_PARAM + ":" + user;
        }

        ArrayList searchIn = new ArrayList<String>();
        if (searchInName) {
            searchIn.add(GITHUB_SEARCH_IN_NAME);
        }
        if (searchInDescription) {
            searchIn.add(GITHUB_SEARCH_IN_DESCRIPTION);
        }
        if (searchInReadme) {
            searchIn.add(GITHUB_SEARCH_IN_README);
        }
        if (!searchIn.isEmpty()) {
            queryValue += " " + GITHUB_SEARCH_IN_PARAM + ":" + TextUtils.join(",", searchIn);
        }

        builder.appendQueryParameter(GITHUB_SEARCH_QUERY_PARAM, queryValue);

        return builder.build().toString();
    }

    public static ArrayList<SearchResult> parseGitHubSearchResultsJSON(String searchResultsJSON) {
        try {
            JSONObject searchResultsObj = new JSONObject(searchResultsJSON);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("items");

            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();
            for (int i = 0; i < searchResultsItems.length(); i++) {
                SearchResult searchResult = new SearchResult();
                JSONObject searchResultItem = searchResultsItems.getJSONObject(i);
                searchResult.fullName = searchResultItem.getString("full_name");
                searchResult.description = searchResultItem.getString("description");
                searchResult.htmlURL = searchResultItem.getString("html_url");
                searchResult.stars = searchResultItem.getInt("stargazers_count");
                searchResultsList.add(searchResult);
            }
            return searchResultsList;
        } catch (JSONException e) {
            return null;
        }
    }
}
