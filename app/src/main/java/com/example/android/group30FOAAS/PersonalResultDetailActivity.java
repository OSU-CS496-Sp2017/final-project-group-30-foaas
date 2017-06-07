package com.example.android.group30FOAAS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.group30FOAAS.utils.FOAASUtils;

public class PersonalResultDetailActivity extends AppCompatActivity {
    private TextView mSearchResultNameTV;
    private TextView mSearchResultDescriptionTV;
    private TextView mSearchResultStarsTV;
    private FOAASUtils.SearchResult mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mSearchResultNameTV = (TextView)findViewById(R.id.tv_search_result_name);
        mSearchResultDescriptionTV = (TextView)findViewById(R.id.tv_search_result_description);
        mSearchResultStarsTV = (TextView)findViewById(R.id.tv_search_result_stars);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (FOAASUtils.SearchResult)intent.getSerializableExtra(FOAASUtils.SearchResult.EXTRA_SEARCH_RESULT);
            mSearchResultNameTV.setText(mSearchResult.fullName);
            mSearchResultDescriptionTV.setText(mSearchResult.description);
            mSearchResultStarsTV.setText(Integer.toString(mSearchResult.stars));
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    public void viewRepoOnWeb() {
        if (mSearchResult != null) {
            Uri repoUri = Uri.parse(mSearchResult.htmlURL);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, repoUri);
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }
        }
    }

    public void shareRepo() {
        if (mSearchResult != null) {
            String shareText = mSearchResult.fullName + ": " + mSearchResult.htmlURL;
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(shareText)
                    .setChooserTitle(R.string.share_chooser_title)
                    .startChooser();
        }
    }
}
