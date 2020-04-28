package com.example.androidproject.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.androidproject.R;
import com.example.androidproject.activity.Home;
import com.example.androidproject.adapter.DialogAdapter;
import com.example.androidproject.adapter.MovieAdapter;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;

public class DialogPreferiti extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MY_LOADER_ID = 1;
    DialogAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new DialogAdapter(getActivity(),null);

        getLoaderManager().initLoader(MY_LOADER_ID,null,this);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder vBuilder = new AlertDialog.Builder(getContext());

        if(getActivity().getContentResolver().query(Provider.MOVIES_URI,null,MovieTableHelper.FAVOURITE+"=1",null,null).getCount()==0)
            vBuilder
                    .setTitle("Favorites List")
                    .setMessage("Non sono presenti film preferiti.\nPuoi aggiungere un film ai preferiti tenendolo premuto " +
                            "a lungo, oppure puoi aggiungerlo premendo sul cuoricino nella schermata del dettaglio film")
                    .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    });
        else
            vBuilder
                    .setTitle("Favorites List")
                    .setAdapter(mAdapter,null)
                    .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    });


        return vBuilder.create();

    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), Provider.MOVIES_URI,null, MovieTableHelper.FAVOURITE+" = 1" ,null,null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

}