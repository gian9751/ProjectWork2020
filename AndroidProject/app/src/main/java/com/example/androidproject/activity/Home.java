package com.example.androidproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.adapter.MovieAdapter;
import com.example.androidproject.data.models.DiscoverMovieResponse;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.data.services.IWebService;
import com.example.androidproject.data.services.WebService;
import com.example.androidproject.fragment.DialogPreferiti;
import com.example.androidproject.internetManagement.NetworkChangeReceiver;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.androidproject.localdata.MovieTableHelper.PAGE;

public class Home extends AppCompatActivity implements IWebService, LoaderManager.LoaderCallbacks<Cursor>, MovieAdapter.IMovieAdapter {

    private static final int MY_LOADER_ID = 0;
    private static final String QUERY = "QUERY" ;

    private WebService mWebService;

    ListView mListView;
    MovieAdapter mAdapter;
    FloatingActionButton mFABFavorites;
    View mFooterView;
    SearchView mSearchView;

    MenuItem mMenuItem;
    String mQuery;

    public int mPage;

    public NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("CineWave");

        mListView = findViewById(R.id.listView);
        mFABFavorites = findViewById(R.id.fab_favorite_list);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterView = li.inflate(R.layout.footer_view, null);

        if (savedInstanceState != null) {
            //mPage = savedInstanceState.getInt(PAGE);

            if (savedInstanceState.getString(QUERY)!=null)
                mQuery = savedInstanceState.getString(QUERY);
        }

        mWebService = WebService.getInstance();

        gestioneDellePage();

        mAdapter = new MovieAdapter(Home.this,null);
        mListView.setAdapter(mAdapter);

        mFABFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPreferiti vDialogPreferiti = new DialogPreferiti();
                vDialogPreferiti.show(getSupportFragmentManager(), null);
            }
        });

        getSupportLoaderManager().initLoader(MY_LOADER_ID,null,this);

        gestioneDellEndlessScroll();
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
          return false;
    }

    private void gestioneDellEndlessScroll() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("Page scroll", ""+view.getLastVisiblePosition());


                if (getResources().getConfiguration().orientation==1){
                    if (view.getLastVisiblePosition()/10 == mPage-1) {
                        loadMovie(++mPage);
                        mListView.addFooterView(mFooterView);
                    }
                } else {
                    if (view.getLastVisiblePosition() / 5 == mPage-1) {
                        loadMovie(++mPage);
                        mListView.addFooterView(mFooterView);
                    }
                }
            }
        });
    }

    private void gestioneDellePage() {
        if (getContentResolver().query(Provider.MOVIES_URI,null,null,null,null).getCount()==0){
            if (!checkConnection()){
                new AlertDialog.Builder(Home.this)
                        .setTitle("No internet connection available")
                        .setMessage("Please turn on your connection to use app")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("open network settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                                startActivity(intent);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("close app", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        })
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                mNetworkReceiver = new NetworkChangeReceiver();
                IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                this.registerReceiver(mNetworkReceiver,filter);

                return;
            }

            mPage=1;
            loadMovie(mPage);
            loadMovie(++mPage);

        }else{
            Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,null,null, PAGE + " DESC");
            vCursor.moveToNext();
            mPage = vCursor.getInt(vCursor.getColumnIndex(PAGE));
        }
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        mPage = 2;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void loadMovie(int page) {
        mWebService.getDiscoverMovie(page, new IWebService() {
            @Override
            public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {
                if (success){
                    for (Movie movie : response.getResults()) {
                        ContentValues vValues = new ContentValues();
                        vValues.put(MovieTableHelper._ID, movie.getId());
                        vValues.put(MovieTableHelper.TITLE, movie.getTitle());
                        vValues.put(MovieTableHelper.PLOT, movie.getOverview());
                        vValues.put(MovieTableHelper.POSTER_PATH, "https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
                        vValues.put(MovieTableHelper.BACKDROP_PATH,"https://image.tmdb.org/t/p/w500" + movie.getBackdropPath());
                        vValues.put(MovieTableHelper.RELEASE_DATE, movie.getReleaseDate());
                        vValues.put(MovieTableHelper.USER_SCORE, movie.getVoteAverage());
                        vValues.put(PAGE, response.getPage());
                        Log.d("asda", "insert" +  movie.getId());

                        Uri vResultUri = getContentResolver().insert(Provider.MOVIES_URI, vValues);
                        Log.d("asda", "insert" +  vResultUri);

                    }
                }else{
                    Toast.makeText(Home.this,"Caricamento dei film non riuscito: "+ errorMessage, Toast.LENGTH_SHORT).show();
                    Log.d("errore: ", errorMessage + "codice errore: "+ errorCode);

                    //if ()
                }
            }
        });
    }


    @Override
    public void onDiscoverMovieFetched(boolean success, DiscoverMovieResponse response, int errorCode, String errorMessage) {
        if (mListView.getFooterViewsCount()>0)
            mListView.removeFooterView(mFooterView);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, Provider.MOVIES_URI,null,null,null, PAGE + " ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE, mPage);
        if (!mSearchView.isIconified())
            outState.putString(QUERY,mSearchView.getQuery().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kebab_menu, menu);
        inflater.inflate(R.menu.search_bar, menu);
        // Retrieve the SearchView and plug it into SearchManager

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mMenuItem = menu.findItem(R.id.action_search);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI, null, MovieTableHelper.TITLE + " LIKE '%" + query + "%'", null, null);
                    mAdapter.swapCursor(vCursor);
                    mSearchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI, null, MovieTableHelper.TITLE + " LIKE '%" + newText+"%'", null, null);
                    mAdapter.swapCursor(vCursor);
                }else{
                    Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,null,null, PAGE + " ASC");
                    mAdapter.swapCursor(vCursor);
                }

                return true;
            }
        });

        menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Log.d("ciao","onMenuItemActionExpand");
                if (mListView.getFooterViewsCount()>0)
                    mFooterView.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Log.d("ciao","onMenuItemActionCollapse");
                Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI, null, null, null, PAGE + " ASC");
                mAdapter.swapCursor(vCursor);
                if (mListView.getFooterViewsCount() > 0)
                    mFooterView.setVisibility(View.VISIBLE);

                return true;
            }
        });

        return true;
    }

    private void chiudiSearchBar() {
        mSearchView.setQuery("",false);
        mSearchView.setIconified(true);
        mMenuItem.collapseActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_credits:
                startActivity(new Intent(Home.this, Credits.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListViewItemSelected(boolean aResponse) {
        if (!mSearchView.isIconified())
            chiudiSearchBar();
    }
}
