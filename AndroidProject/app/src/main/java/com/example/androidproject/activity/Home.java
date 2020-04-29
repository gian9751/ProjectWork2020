package com.example.androidproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.adapter.MovieAdapter;
import com.example.androidproject.data.models.DiscoverMovieResponse;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.data.services.IWebService;
import com.example.androidproject.data.services.WebService;
import com.example.androidproject.fragment.DialogPreferiti;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.androidproject.localdata.MovieTableHelper.PAGE;

public class Home extends AppCompatActivity implements IWebService, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_LOADER_ID = 0;

    private WebService mWebService;

    ListView mListView;
    MovieAdapter mAdapter;
    FloatingActionButton mFABFavorites;
    View footerView;

    int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("CineWave");

        if (savedInstanceState != null) {
            mPage = savedInstanceState.getInt(PAGE);
        }

        mWebService = WebService.getInstance();

        mListView = findViewById(R.id.listView);
        mFABFavorites = findViewById(R.id.fab_favorite_list);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = li.inflate(R.layout.footer_view, null);

        gestioneDellePage();

        mAdapter = new MovieAdapter(Home.this,null);
        mListView.setAdapter(mAdapter);

        mFABFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPreferiti vDialogPreferiti = new DialogPreferiti();
                vDialogPreferiti.show(getSupportFragmentManager(), null);
            }
        });

        getSupportLoaderManager().initLoader(MY_LOADER_ID,null,this);

        gestioneDellEndlessScroll();
    }

    private void gestioneDellEndlessScroll() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.d("Page scroll", ""+view.getLastVisiblePosition());

                if (view.getLastVisiblePosition()/10 == mPage-1) {
                    loadMovie(++mPage);
                    mListView.addFooterView(footerView);
                }
            }
        });
    }

    private void gestioneDellePage() {
        if (getContentResolver().query(Provider.MOVIES_URI,null,null,null,null).getCount()==0){
            mPage = 1;
            loadMovie(mPage++);
            loadMovie(mPage);

        }else{
            Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,null,null, PAGE + " DESC");
            vCursor.moveToNext();
            mPage = vCursor.getInt(vCursor.getColumnIndex(PAGE));
            Log.d("Page", mPage+"");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void loadMovie(int page) {
        mWebService.getDiscoverMovie(page, new IWebService() {
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
                        vValues.put(PAGE, response.getPage());
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
        if (mListView.getFooterViewsCount()==1)
            mListView.removeFooterView(footerView);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, Provider.MOVIES_URI,null,null,null, PAGE + " ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE, mPage);
    }
}
