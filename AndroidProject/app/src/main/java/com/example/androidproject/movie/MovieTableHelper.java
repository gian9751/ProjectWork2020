package com.example.androidproject.movie;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "movie";
    public static final String TITLE = "title"; //titolo
    public static final String PLOT = "plot"; //trama

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            PLOT + " TEXT);";

}
