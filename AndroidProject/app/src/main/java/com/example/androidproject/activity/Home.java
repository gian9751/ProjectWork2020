package com.example.androidproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.adapter.MovieAdapter;
import com.example.androidproject.data.models.DiscoverMovieResponse;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.data.services.IWebService;
import com.example.androidproject.data.services.WebService;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Home extends AppCompatActivity implements IWebService, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_LOADER_ID = 0;

    private WebService mWebService;

    ListView mListView;
    MovieAdapter mAdapter;
    FloatingActionButton mFABFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Movies");

        mWebService = WebService.getInstance();

        loadMovie();

        mListView = findViewById(R.id.listView);
        mAdapter = new MovieAdapter(Home.this,null);
        mListView.setAdapter(mAdapter);
        mFABFavorites = findViewById(R.id.fab_favorite_list);
        mFABFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Premuto FAB lista preferiti", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportLoaderManager().initLoader(MY_LOADER_ID,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void loadMovie() {
        mWebService.getDiscoverMovie(new IWebService() {
            @Override
            public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {
                if (success){
                    for (Movie movie : response.getResults()) {
                        ContentValues vValues = new ContentValues();
                        vValues.put(MovieTableHelper._ID, movie.getId());
                        vValues.put(MovieTableHelper.TITLE, movie.getTitle());
                        vValues.put(MovieTableHelper.PLOT, movie.getOverview());
                        vValues.put(MovieTableHelper.POSTER_PATH, "https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
                        vValues.put(MovieTableHelper.BACKDROP_PATH,"https://image.tmdb.org/t/p/w500" + movie.getBackdropPath());

                        Log.d("asda", "insert" +  movie.getId());

                        Uri vResultUri = getContentResolver().insert(Provider.MOVIES_URI, vValues);
                        Log.d("asda", "insert" +  vResultUri);

                    }
                }else{
                    Toast.makeText(Home.this,"Caricamento dei film non riuscito: "+ errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("errore: ", errorMessage + "codice errore: "+ errorCode);
                    //setContentView(R.layout.activity_movie_detail);
                }
            }
        });
    }


    @Override
    public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, Provider.MOVIES_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
