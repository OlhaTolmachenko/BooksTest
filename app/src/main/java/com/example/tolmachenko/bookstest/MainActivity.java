package com.example.tolmachenko.bookstest;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

import com.example.tolmachenko.bookstest.model.CustomResponse;
import com.example.tolmachenko.bookstest.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private SharedPreferences sharedPrefs;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                sharedPrefs.edit().putString(SearchManager.QUERY, query).apply();
                searchView.clearFocus();
                Log.d(TAG, "onQueryTextSubmit: " + query);
                getBooks(query, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "handleIntent: onQueryTextChange" + newText);
                //TODO turn spaces to +
                //TODO create regex to validate data input
                return false;
            }
        });

        searchView.getQuery();
        return true;
    }

    private Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private BooksApiInterface createService() {
        return buildRetrofit().create(BooksApiInterface.class);
    }

    private void getBooks(String query, int startIndex) {
        Call<CustomResponse> call = createService().getBooks(query, startIndex, "AIzaSyB-plaYU1DeedN5v3DGBu3p3sL4qsXflK8");
        call.enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                Log.d(TAG, "RESPONSE CODE:" + response.code() + " " + response.message());
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Log.d(TAG, "call object: " + call);
                Log.d(TAG, "error message: " + t.getMessage());
            }
        });

    }
}
