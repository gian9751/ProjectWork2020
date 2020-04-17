package com.example.androidproject.favouritesmovies;

import android.provider.BaseColumns;

public class FavouritesTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "favouritemovie";
    public static final String TITLE = "title"; //titolo
    public static final String FAVOURITE_ID = "favourite_id";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            FAVOURITE_ID + " TEXT);";

}
