package com.example.android.group30FOAAS;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.group30FOAAS.utils.FOAASUtils;
import com.example.android.group30FOAAS.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

public class ListResultDetailActivity extends AppCompatActivity
        implements FOAASListSearchAdapter.OnSearchResultClickListener, LoaderManager.LoaderCallbacks<String>{
    private TextView mSearchResultNameTV;
    private TextView mSearchResultDescriptionTV;
    private TextView mSearchResultStarsTV;
    //private TextView mSubtitleTV;
    private FOAASUtils.SearchResult mSearchResult;
    private ProgressBar mLoadingIndicatorPB;
    private RecyclerView rv_list_search_results;
    private static final String SEARCH_URL_KEY = "githubSearchURL";
    private static final int FOAAS_LOADER_ID = 0;
    private TextView mLoadingErrorMessageTV;
    private FOAASListSearchAdapter mFOAASListSearchAdapter;
    private static final String TAG = ListResultDetailActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_list);


        mLoadingIndicatorPB = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = (TextView)findViewById(R.id.tv_loading_error_message);
        rv_list_search_results = (RecyclerView)findViewById(R.id.rv_list_result);
        //mSubtitleTV = (TextView)findViewById(R.id.tv_subtitle);

        mFOAASListSearchAdapter = new FOAASListSearchAdapter(this);

        if(mFOAASListSearchAdapter == null)
        {
            Log.d(TAG, "Adapter is null");
        } else
        {
            Log.d(TAG, "help pls");
        }
        if(rv_list_search_results == null){
            Log.d(TAG, "rv_list_search_results IS NULL");
        }
        if(mLoadingErrorMessageTV==null){
            Log.d(TAG, "mLoadingErrorMessageTV is NULL");
        }

        rv_list_search_results.setAdapter(mFOAASListSearchAdapter);
        rv_list_search_results.setLayoutManager(new LinearLayoutManager(this));
        rv_list_search_results.setHasFixedSize(true);
        //mSearchResultNameTV = (TextView)findViewById(R.id.tv_search_result_name);
        //mSearchResultDescriptionTV = (TextView)findViewById(R.id.tv_search_result_description);
        //mSearchResultStarsTV = (TextView)findViewById(R.id.tv_search_result_stars);

        //Intent intent = getIntent();
        //if (intent != null && intent.hasExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT)) {
        //    mSearchResult = (FOAASUtils.SearchResult)intent.getSerializableExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT);
        //    mSubtitleTV.setText(mSearchResult.subtitle);
        //}

        getSupportLoaderManager().initLoader(FOAAS_LOADER_ID, null, this);
        doSearch();
    }

    private void doSearch()
    {
        String type = "bag";
        String name = "";
        String from = "me";
        String search = FOAASUtils.buildFOAASURL(type, name, from);

        Bundle argsBundle = new Bundle();
        argsBundle.putString(SEARCH_URL_KEY, search);
        getSupportLoaderManager().restartLoader(FOAAS_LOADER_ID, argsBundle, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_repo:
                viewRepoOnWeb();
                return true;
            case R.id.action_share:
                shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onSearchResultClick(FOAASUtils.SearchResult searchResult) {
        Intent intent = new Intent(this, ListResultDetailActivity.class);
        intent.putExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    public void viewRepoOnWeb() {
        //if (mSearchResult != null) {
        //Uri repoUri = Uri.parse(mSearchResult.htmlURL);
        //Intent webIntent = new Intent(Intent.ACTION_VIEW, repoUri);
        //if (webIntent.resolveActivity(getPackageManager()) != null) {
        //    startActivity(webIntent);
        //}
        //}
    }

    public void shareRepo() {
        //if (mSearchResult != null) {
            //String shareText = mSearchResult.fullName + ": " + mSearchResult.htmlURL;
            String shareText = "HI! I'm sharing!";
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                   .setText(shareText)
                    .setChooserTitle(R.string.share_chooser_title)
                    .startChooser();
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
            rv_list_search_results.setVisibility(View.VISIBLE);
            ArrayList<FOAASUtils.SearchResult> searchResultsList = FOAASUtils.parseFOAASResultsJSON(data);
            mFOAASListSearchAdapter.updateSearchResults(searchResultsList);
        } else {
            rv_list_search_results.setVisibility(View.INVISIBLE);
            mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do...
    }

//    public void shareRepo() {
//        if (mSearchResult != null) {
//            String shareText = "HI! I'm sharing!";
//            ShareCompat.IntentBuilder.from(this)
//                    .setType("text/plain")
//                    .setText(shareText)
//                    .setChooserTitle(R.string.share_chooser_title)
//                    .startChooser();
//        }
//    }


}
