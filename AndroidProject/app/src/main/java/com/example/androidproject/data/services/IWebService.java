package com.example.androidproject.data.services;

import com.example.androidproject.data.models.DiscoverMovieResponse;
import com.example.androidproject.data.models.Movie;

import java.util.List;

public interface IWebService {
    void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage);
}
