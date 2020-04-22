package com.example.androidproject.localdata;

import android.provider.BaseColumns;

public class FavouritesTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "favouritemovie";
    public static final String MOVIE_ID = "movie_id";//foreign key

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MOVIE_ID + " INTEGER FOREIGN KEY);";

}
