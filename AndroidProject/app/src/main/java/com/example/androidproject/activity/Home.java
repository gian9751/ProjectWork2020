package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.adapter.MovieAdapter;
import com.example.androidproject.data.models.DiscoverMovieResponse;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.data.services.IWebService;
import com.example.androidproject.data.services.WebService;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements IWebService {

    private WebService mWebService;

    ArrayList<Movie> mMovies;
    ArrayList<String> mMovieImmagini;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Movies");

        mWebService = WebService.getInstance();

        mMovieImmagini = new ArrayList<String>();
        mMovies = new ArrayList<Movie>();

        mRecyclerView = findViewById(R.id.movieRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter(mMovieImmagini,Home.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadMovie();
    }

    private void loadMovie() {
        mWebService.getDiscoverMovie(new IWebService() {
            @Override
            public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {
                if (success){
                    for (Movie movie : response.getResults()) {
                        mMovies.add(movie);
                        mMovieImmagini.add("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
                        mAdapter.notifyDataSetChanged();
                    }
                    //Toast.makeText(Home.this,"Ho preso i film vecchio", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Home.this,"Qualcosa è andato storto: "+ errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("errore: ", errorMessage + "codice errore: "+ errorCode);
                }
            }
        });
    }


    @Override
    public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {

    }
}
