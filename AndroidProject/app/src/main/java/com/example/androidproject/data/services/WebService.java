package com.example.androidproject.data.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.data.models.DiscoverMovieResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

    private static final String API_KEY = "a0b9434f84bb09e8285dc149418b7955";
    private static WebService instance;
    private DiscoverMovieService discoverMovieService;
    //private Context mContext;

    private WebService(/*Context context*/){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        discoverMovieService = retrofit.create(DiscoverMovieService.class);

        //mContext = context;
    }

    public static WebService getInstance(/*Context context*/){
        if (instance == null)
            instance = new WebService(/*context*/);

        return instance;
    }

    public void getDiscoverMovie(int page, final IWebService serviceListener) {
        discoverMovieService.discoverMovie(API_KEY, page).enqueue(new Callback<DiscoverMovieResponse>() {

            @Override
            public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
                Log.d("Test", "Server response");
                if (response.code()==200)
                    serviceListener.onDiscoverMovieFetched(true, response.body(), -1, null);
                else
                    serviceListener.onDiscoverMovieFetched(false, null, response.code(), response.errorBody().toString());

            }

            @Override
            public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
                serviceListener.onDiscoverMovieFetched(false, null, -1, t.getLocalizedMessage());
            }
        });
    }
}
