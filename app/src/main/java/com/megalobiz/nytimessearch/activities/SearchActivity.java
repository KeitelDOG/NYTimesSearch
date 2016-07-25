package com.megalobiz.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megalobiz.nytimessearch.R;
import com.megalobiz.nytimessearch.adapters.ArticleArrayAdapter;
import com.megalobiz.nytimessearch.models.Article;
import com.megalobiz.nytimessearch.models.SearchSettings;
import com.megalobiz.nytimessearch.utils.EndlessScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    SearchSettings settings;
    String searchQuery;
    int searchPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSearchParameters();
        setupViews();

    }

    public void setupSearchParameters() {
        // initialize settings with default values
        settings = new SearchSettings();
    }

    public void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        searchPage = 0;

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // get article object at position i
                Article article = (Article) adapterView.getItemAtPosition(position);

                // create intent to display article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // pass the article into intent
                i.putExtra("article", article);
                startActivity(i);
            }
        });

        // Attach the listener to the AdapterView onCreate
        gvResults.setOnScrollListener(new EndlessScrollListener(5, 0) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView

                searchPage = page;
                searchArticles(false);
                Toast.makeText(SearchActivity.this, "LoadMore "+searchPage+" - "+totalItemsCount, Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                searchArticles(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchArticles(final Boolean clear) {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("page", searchPage);
        params.put("q", searchQuery);

        // if settings begin date has been set apply begin_date
        if(settings.getBeginDate() != null && settings.getBeginDate().getCalendar() != null) {
            params.put("begin_date", settings.formatBeginDate());
        }

        // if settings has sort oder apply sort
        if(settings.getSortOrder() != SearchSettings.Sort.none) {
            params.put("sort", settings.getSortOrder().name());
        }

        // if settings filters contains at least one filter, apply filter
        if(settings.getFilters().size() > 0) {
            params.put("fq", settings.generateNewsDeskFiltersOR());
        }

        params.put("api-key", "e99049db3b3b4269bf5ad04308f38415");

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    if (clear)
                        adapter.clear();

                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void showSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("settings", settings);
        startActivityForResult(i, 100);
    }

    // Handle the result once the activity returns a result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                settings = (SearchSettings) data.getSerializableExtra("settings");
                //Toast.makeText(this, "sort passed: "+ settings.getSortOrder(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
