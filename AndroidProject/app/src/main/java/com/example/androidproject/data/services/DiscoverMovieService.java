package com.example.androidproject.data.services;

import com.example.androidproject.data.models.DiscoverMovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DiscoverMovieService {

    // base path https://api.themoviedb.org/3/
    // apikey a0b9434f84bb09e8285dc149418b7955
    @GET("discover/movie?language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false")
    Call<DiscoverMovieResponse> discoverMovie(@Query("api_key") String apikey, @Query("page") int page);
}
