package com.example.android.group30FOAAS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.group30FOAAS.utils.FOAASUtils;
import com.example.android.group30FOAAS.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements FOAASSearchAdapter.OnSearchResultClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_URL_KEY = "githubSearchURL";
    private static final int GITHUB_SEARCH_LOADER_ID = 0;

    private RecyclerView mSearchResultsRV;
    private EditText mSearchBoxET;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private FOAASSearchAdapter mFOAASSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxET = (EditText)findViewById(R.id.et_search_box);
        mLoadingIndicatorPB = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView)findViewById(R.id.tv_loading_error_message);
        mSearchResultsRV = (RecyclerView)findViewById(R.id.rv_search_results);

        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRV.setHasFixedSize(true);

        mFOAASSearchAdapter = new FOAASSearchAdapter(this);
        mSearchResultsRV.setAdapter(mFOAASSearchAdapter);

        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER_ID, null, this);

        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxET.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doGitHubSearch(searchQuery);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doGitHubSearch(String searchQuery) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        String language = sharedPreferences.getString(getString(R.string.pref_language_key),
                getString(R.string.pref_language_default));
        String user = sharedPreferences.getString(getString(R.string.pref_user_key), "");
        boolean searchInName = sharedPreferences.getBoolean(getString(R.string.pref_in_name_key), true);
        boolean searchInDescription = sharedPreferences.getBoolean(getString(R.string.pref_in_description_key), true);
        boolean searchInReadme = sharedPreferences.getBoolean(getString(R.string.pref_in_readme_key), false);

        String githubSearchUrl = FOAASUtils.buildGitHubSearchURL(searchQuery, sort, language, user,
                searchInName, searchInDescription, searchInReadme);

        Bundle argsBundle = new Bundle();
        argsBundle.putString(SEARCH_URL_KEY, githubSearchUrl);
        getSupportLoaderManager().restartLoader(GITHUB_SEARCH_LOADER_ID, argsBundle, this);
    }

    @Override
    public void onSearchResultClick(FOAASUtils.SearchResult searchResult) {
        /*if(Personal result button){
        Intent intent = new Intent(this, PersonalResultDetailActivity.class);
        intent.putExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(intent);
        */
        /*if(random result button){
        Intent intent = new Intent(this, RandomResultDetailActivity.class);
        intent.putExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(intent);
        */

        //if(List result button){
        Intent intent = new Intent(this, ListResultDetailActivity.class);
        intent.putExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(intent);
        //}
    }
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mSearchResultsJSON;

            @Override
            protected void onStartLoading() {
                if (args != null) {
                    if (mSearchResultsJSON != null) {
                        Log.d(TAG, "AsyncTaskLoader delivering cached results");
                        deliverResult(mSearchResultsJSON);
                    } else {
                        mLoadingIndicatorPB.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }
            }

            @Override
            public String loadInBackground() {
                if (args != null) {
                    String githubSearchUrl = args.getString(SEARCH_URL_KEY);
                    Log.d(TAG, "AsyncTaskLoader making network call: " + githubSearchUrl);
                    String searchResults = null;
                    try {
                        searchResults = NetworkUtils.doHTTPGet(githubSearchUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return searchResults;
                } else {
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mSearchResultsJSON = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "AsyncTaskLoader's onLoadFinished called");
        mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
        if (data != null) {
            mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
            mSearchResultsRV.setVisibility(View.VISIBLE);
            ArrayList<FOAASUtils.SearchResult> searchResultsList = FOAASUtils.parseGitHubSearchResultsJSON(data);
            mFOAASSearchAdapter.updateSearchResults(searchResultsList);
        } else {
            mSearchResultsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do...
    }
}
