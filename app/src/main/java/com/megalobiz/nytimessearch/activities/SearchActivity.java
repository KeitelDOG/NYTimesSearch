package com.megalobiz.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megalobiz.nytimessearch.R;
import com.megalobiz.nytimessearch.adapters.ArticleArrayAdapter;
import com.megalobiz.nytimessearch.models.Article;
import com.megalobiz.nytimessearch.models.SearchSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    Button btnSearch;
    GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    SearchSettings settings;

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

        //test filters
        settings.addFilter("Arts");
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
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

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "e99049db3b3b4269bf5ad04308f38415");
        params.put("page", 0);
        params.put("q", query);

        // if settings has sort oder apply sort
        if(settings.getSortOrder() != null) {
            params.put("sort", settings.getSortOrder());
        }

        // if settings filters contains at least one filter, apply filter
        if(settings.getFilters().size() > 0) {
            params.put("fq", settings.generateNewsDeskFiltersOR());
        }

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("DEBUG", response.toString());
            JSONArray articleJsonResults = null;

            try {
                articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                adapter.addAll(Article.fromJSONArray(articleJsonResults));
                adapter.notifyDataSetChanged();
                Log.d("DEBUG", articles.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            }
        });
    }

    public void showSettings() {
        //Toast.makeText(SearchActivity.this, "Settings Dialog Fragment will open", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("settings", settings);
        startActivityForResult(i, 100);

    }

    // Handle the result once the activity returns a result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                settings = (SearchSettings) data.getSerializableExtra("settings");
                Toast.makeText(this, "sort passed: "+ settings.getSortOrder(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
