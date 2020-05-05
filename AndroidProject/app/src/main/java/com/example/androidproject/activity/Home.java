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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.example.androidproject.broadcastReceiver.NetworkChangeReceiver;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.androidproject.localdata.MovieTableHelper.LOAD_DATE;
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

    int mPage;

    //public NetworkChangeReceiver mNetworkReceiver;
    int mCase = -1;

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
            mPage = savedInstanceState.getInt(PAGE);

            if (savedInstanceState.getString(QUERY)!=null)
                mQuery = savedInstanceState.getString(QUERY);
        }

        mWebService = WebService.getInstance();

        controlloDataDeiDati();

        gestioneDellePage();

        controlloConnessioneInternet();


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

    private void controlloConnessioneInternet() {
        if (!checkConnection()&&mCase==-1)
            showAlert("if you want to see more movies, turn on the connection","No internet connection available");
    }

    private void controlloDataDeiDati() {
        if (getContentResolver().query(Provider.MOVIES_URI,null, MovieTableHelper.PAGE+"!= -1",null,null).getCount()!=0&&checkConnection()) {

            //prendo i record in ordine per page (partendo da page 1)
            Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,MovieTableHelper.PAGE+"!= -1",null, PAGE + " ASC");
            vCursor.moveToNext();
            long vDataDiCaricamento = Long.parseLong(vCursor.getString(vCursor.getColumnIndex(LOAD_DATE))); // data di caricamento in millisecondi del primo record
            long vNow = System.currentTimeMillis(); // data attuale in millisecondi

            long vDiffMillis = vNow - vDataDiCaricamento; // differnza in millisecondi tra la data attuale e la data di caricamento
            if (vDiffMillis >= (1000*60*60*24)) { // se la differenza tra i due è uguale a 24 ore trasformate in millisecondi
                getContentResolver().delete(Provider.MOVIES_URI, MovieTableHelper.FAVOURITE + "=0", null); // allora resetto il mio database

                // ora nel mio database sono rimasti solo i film preferiti
                // per evitare che incasinino la listview li salvo con una page
                // che non può essere assegnata a nessun gruppo di film, ovvero -1
                ContentValues vValues = new ContentValues();
                vValues.put(PAGE, -1);
                getContentResolver().update(Provider.MOVIES_URI,vValues,null,null);

            }
        }
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

    private void gestioneDellePage() {
        if (getContentResolver().query(Provider.MOVIES_URI,null,MovieTableHelper.PAGE+"!= -1",null,null).getCount()==0){
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

                mCase = 1;
                NetworkChangeReceiver vNetworkReceiver = new NetworkChangeReceiver(mCase);
                IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                this.registerReceiver(vNetworkReceiver,filter);

                return;
            }

            mPage=1;
            loadMovie(mPage);
            loadMovie(++mPage);

        }else{
            //prendo i record in ordine per page (partendo dall'ultima page)
            Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,MovieTableHelper.PAGE+"!= -1",null, PAGE + " DESC");
            vCursor.moveToNext();
            mPage = vCursor.getInt(vCursor.getColumnIndex(PAGE));
        }
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

    public void showAlert(String aMessage, String aTitle){
        new AlertDialog.Builder(Home.this)
                .setTitle(aTitle)
                .setMessage(aMessage)
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Open network settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                        startActivity(intent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Close", null)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void loadMovie(int aPage) {
        mWebService.getDiscoverMovie(aPage, new IWebService() {
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

                        long vDataDiCaricamento = System.currentTimeMillis();
                        vValues.put(MovieTableHelper.LOAD_DATE,vDataDiCaricamento);

                        Uri vResultUri = getContentResolver().insert(Provider.MOVIES_URI, vValues);
                        Log.d("asda", "insert" +  vResultUri);

                    }
                }else{
                    Toast.makeText(Home.this,"Caricamento dei film non riuscito: "+ errorMessage, Toast.LENGTH_SHORT).show();

                    if (!checkConnection()&&mCase==-1){
                        showAlert("Please turn on your connection if u want load the next page of movie", "No internet connection available");
                        Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,MovieTableHelper.PAGE+"!= -1",null, PAGE + " DESC");
                        vCursor.moveToNext();
                        mPage = vCursor.getInt(vCursor.getColumnIndex(PAGE));
                        mCase = 2;
                        NetworkChangeReceiver vNetworkReceiver = new NetworkChangeReceiver(mCase);
                        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                        registerReceiver(vNetworkReceiver,filter);
                    }
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
        return new CursorLoader(this, Provider.MOVIES_URI,null,MovieTableHelper.PAGE+"!= -1",null, PAGE + " ASC");
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
                    Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI,null,MovieTableHelper.PAGE+"!= -1",null, PAGE + " ASC");
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
                Cursor vCursor = getContentResolver().query(Provider.MOVIES_URI, null, MovieTableHelper.PAGE+"!= -1", null, PAGE + " ASC");
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

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        switch (mCase){
            case 1:
                mPage = 1;
                loadMovie(mPage);
                loadMovie(++mPage);
                mCase = -1;
                break;
            case 2:
                loadMovie(++mPage);
                mCase = -1;
                break;
        }

    }
}
