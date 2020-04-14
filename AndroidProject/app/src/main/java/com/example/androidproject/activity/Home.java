package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.data.models.DiscoverMovieResponse;
import com.example.androidproject.data.services.IWebService;
import com.example.androidproject.data.services.WebService;

public class Home extends AppCompatActivity implements IWebService {

    private WebService mWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Movies");

        mWebService = WebService.getInstance();
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
                    Toast.makeText(Home.this,"Ho preso i film vecchio", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Home.this,"Qualcosa è andato storto"+ errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("errore: ", errorMessage + "codice errore: "+ errorCode);
                }
            }
        });
    }


    @Override
    public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {

    }
}
