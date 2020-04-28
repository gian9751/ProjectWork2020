package com.example.androidproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class Home extends AppCompatActivity implements IWebService, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_LOADER_ID = 0;

    private WebService mWebService;

    ListView mListView;
    MovieAdapter mAdapter;
    FloatingActionButton mFABFavorites;
    int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Movies");

        mWebService = WebService.getInstance();

        //loadMovie(mPage);

        mListView = findViewById(R.id.listView);
        mFABFavorites = findViewById(R.id.fab_favorite_list);

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


        //CODICE TEST ENDLESS SCROLL
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = li.inflate(R.layout.footer_view, null);
        mHandler = new MyHandler();
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Controlla quando si è all'ultimo elemento della list view
                if (view.getLastVisiblePosition() == totalItemCount - 1 && isLoading ==false ) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    //avvia thread
                    thread.start();
                }
            }
        });
        //FINE COD TEST
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
                        vValues.put(MovieTableHelper.PAGE, response.getPage());
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
        return new CursorLoader(this, Provider.MOVIES_URI,null,null,null, MovieTableHelper.PAGE + " ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }


    //TEST endless scroll GUARDA QUI: https://youtu.be/XwIKb_f0Y_w

    public Handler mHandler;
    public View footerView;
    public boolean isLoading = false;

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    //Aggiunge loading view durante il caricamento
                    mListView.addFooterView(footerView);
                    break;
                case 1:
                    //Aggiornamento dati e UI
                    loadMovie(mPage++);
                    //rimozione footer
                    mListView.removeFooterView(footerView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            //Aggiunge footer view dopo aver ricevuto i dati
            mHandler.sendEmptyMessage(0);
            //Cerca ulteriori dati
                loadMovie(mPage++);
            //Ritard durante debug. RIMUOVERE SE WORKA

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Mandare risultato all'Handle
            Message msg = mHandler.obtainMessage(1); //DEL CODICE QUI DOPO 1 (Cerca ulteriori dati)
            Log.d("DEV", "" + mPage);
            mHandler.sendMessage(msg);
        }
    }
    //FINE TEST

}
