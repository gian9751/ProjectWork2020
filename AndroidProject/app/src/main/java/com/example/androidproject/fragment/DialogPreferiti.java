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
import com.example.androidproject.localdata.Provider;

public class DialogPreferiti extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MY_LOADER_ID = 1;
    DialogAdapter mAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View vView = getActivity().getLayoutInflater().inflate(R.layout.dialog_favorites_list, null);

        ListView vListView = vView.findViewById(R.id.favorite_list);

        mAdapter = new DialogAdapter(getActivity(),null);
        vListView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(MY_LOADER_ID,null,this);

        AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());

        vBuilder
                .setTitle("Lista preferiti")
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setView(vView);

        return vBuilder.create();

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), Provider.FAVOURITES_URI,null,null,null,null);

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