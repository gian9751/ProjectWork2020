package com.example.androidproject.localdata;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Provider extends ContentProvider {

    public static final String AUTORITY = "com.example.androidproject.movie.Provider";

    public static final String BASE_PATH_MOVIES = "movies";
    public static final String BASE_PATH_FAVOURITES = "favourites";

    public static final int ALL_MOVIE = 0;
    public static final int ALL_FAVOURITE = 1;
    public static final int SINGLE_MOVIE = 2;
    public  static final int SINGLE_FAVOURITE = 3;

    public static final String MIME_TYPE_MOVIES = ContentResolver.CURSOR_DIR_BASE_TYPE+"vnd.all_movies";
    public static final String MIME_TYPE_MOVIE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"vnd.single_movie";
    public static final String MIME_TYPE_FAVOURITES = ContentResolver.CURSOR_DIR_BASE_TYPE+"vnd.all_favourites";
    public static final String MIME_TYPE_FAVOURITE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"vnd.single_movie";

    public static Uri MOVIES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT+"://"+AUTORITY+"/"+BASE_PATH_MOVIES);
    public static Uri FAVOURITES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT+"://"+AUTORITY+"/"+BASE_PATH_FAVOURITES);

    private DB mDb;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES, ALL_MOVIE);
        mUriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES+"/#", SINGLE_MOVIE);

        mUriMatcher.addURI(AUTORITY, BASE_PATH_FAVOURITES, ALL_FAVOURITE);
        mUriMatcher.addURI(AUTORITY, BASE_PATH_FAVOURITES+"/#", SINGLE_FAVOURITE);
    }

    @Override
    public boolean onCreate() {
        mDb = new DB(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase vDb = mDb.getReadableDatabase();
        SQLiteQueryBuilder vBuilder = new SQLiteQueryBuilder();

        switch (mUriMatcher.match(uri)){
            case ALL_MOVIE:
                vBuilder.setTables(MovieTableHelper.TABLE_NAME);
                break;
            case SINGLE_MOVIE:
                vBuilder.setTables(MovieTableHelper.TABLE_NAME);
                vBuilder.appendWhere(MovieTableHelper._ID+" = "+uri.getLastPathSegment());
                break;
            case ALL_FAVOURITE:
                vBuilder.setTables(FavouritesTableHelper.TABLE_NAME);
                break;
            case SINGLE_FAVOURITE:
                vBuilder.setTables(FavouritesTableHelper.TABLE_NAME);
                vBuilder.appendWhere(FavouritesTableHelper._ID+" = "+uri.getLastPathSegment());
                break;
        }

        Cursor vCursor = vBuilder.query(vDb,strings,s,strings1,null,null,s1);
        vCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return vCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)){
            case ALL_MOVIE:
                return MIME_TYPE_MOVIES;
            case SINGLE_MOVIE:
                return MIME_TYPE_MOVIE;
            case ALL_FAVOURITE:
                return MIME_TYPE_FAVOURITES;
            case SINGLE_FAVOURITE:
                return  MIME_TYPE_FAVOURITE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase vDb = mDb.getWritableDatabase();

        long vResult = 0;
        String vString = "";

        switch (mUriMatcher.match(uri)) {
            case ALL_MOVIE:
                vResult = vDb.insert(MovieTableHelper.TABLE_NAME, null, values);
                vString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_MOVIES + "/" + vResult;
                break;
            case ALL_FAVOURITE:
                vResult = vDb.insert(FavouritesTableHelper.TABLE_NAME, null, values);
                vString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_FAVOURITES + "/" + vResult;
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri.toString()+"/"+vString);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String vTableName = "";
        String vQuery = "";

        SQLiteDatabase vDb = mDb.getWritableDatabase();

        switch (mUriMatcher.match(uri)){
            case ALL_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = selection;
                break;
            case SINGLE_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = MovieTableHelper._ID+" = "+ uri.getLastPathSegment();
                if (selection!= null){
                    vQuery+=" AND " + selection;
                }
                break;
            case ALL_FAVOURITE:
                vTableName = FavouritesTableHelper.TABLE_NAME;
                vQuery = selection;
                break;
            case SINGLE_FAVOURITE:
                vTableName = FavouritesTableHelper.TABLE_NAME;
                vQuery = FavouritesTableHelper._ID + " = " + uri.getLastPathSegment();
                if (selection != null){
                    vQuery+= "AND " + selection;
                }
                break;
        }
        int vDeletedRows = vDb.delete(vTableName,vQuery,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return vDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String vTableName = "";
        String vQuery = "";

        SQLiteDatabase vDb = mDb.getWritableDatabase();

        switch (mUriMatcher.match(uri)){
            case ALL_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = selection;
                break;
            case SINGLE_MOVIE:
                vTableName = MovieTableHelper.TABLE_NAME;
                vQuery = MovieTableHelper._ID+" = "+uri.getLastPathSegment();
                if (selection != null){
                    vQuery+= "AND " + selection;
                }
                break;
            case ALL_FAVOURITE:
                vTableName = FavouritesTableHelper.TABLE_NAME;
                vQuery = selection;
                break;
            case SINGLE_FAVOURITE:
                vTableName = FavouritesTableHelper.TABLE_NAME;
                vQuery = FavouritesTableHelper._ID+" = " + uri.getLastPathSegment();
                if(selection!=null){
                    vQuery+= "AND " + selection;
                }
                break;
        }
        int vUpdatedRows = vDb.update(vTableName, values, vQuery, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return vUpdatedRows;
    }

    public static class FavouritesAdapter {
    }
}
