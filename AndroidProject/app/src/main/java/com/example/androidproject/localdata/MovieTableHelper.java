package com.example.androidproject.localdata;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "movie";
    public static final String TITLE = "title"; //titolo
    public static final String PLOT = "plot"; //trama
    public static final String POSTER_PATH = "poster_path"; //immagine da utilizzare nella home
    public static final String BACKDROP_PATH = "backdrop_path"; //immagine da utilizzare nel dettaglio

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY, " +
            TITLE + " TEXT, " +
            PLOT + " TEXT, " +
            POSTER_PATH + " TEXT, " +
            BACKDROP_PATH + " TEXT);";

}
