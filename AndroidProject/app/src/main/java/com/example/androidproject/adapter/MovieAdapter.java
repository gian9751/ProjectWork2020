package com.example.androidproject.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidproject.R;
import com.example.androidproject.activity.MovieDetail;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;

import java.util.ArrayList;

import static android.os.Build.ID;

public class MovieAdapter extends CursorAdapter {

    RequestOptions mRequestOptions ;
    boolean mOrientation;
    Context mContext;

    IMovieAdapter mListener;

    public MovieAdapter(Context context, Cursor c) {
        super(context, c);

        mContext = context;

        if (context instanceof IMovieAdapter) {
            mListener = (IMovieAdapter) context;
        } else {
            mListener = null;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater vInflater = LayoutInflater.from(context);
        View vView = vInflater.inflate(R.layout.item_movie,viewGroup,false);

        return  vView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        if (context.getResources().getConfiguration().orientation==1)
            setBindViewVertical(view,context,cursor);
        else
            setBindViewHorizontal(view,context,cursor);
    }

    @Override
    public int getCount() {
        if (getCursor()!=null)
            if (mContext.getResources().getConfiguration().orientation==1)
                return (getCursor().getCount()%2==0)?getCursor().getCount()/2:getCursor().getCount()/2+1;
            else
                if (getCursor().getCount()%4==0)
                    // nel caso sia divisibile per 4 ritorno la grandezza diviso 4
                    return getCursor().getCount()/4;
                else {
                    // altrimenti divido la grandezza per quattro e prendo solo il valore decimale
                    // se il valore decimale è minore di 0.30 significa che ho un poster in più
                    // se il valore decimale è maggiore di 0.60 significa che ho tre poster in più
                    // se il valore decimale non è minore di 0.30 e non è maggiore di 0.60 significa che ho due poster in più

                    double vCursorSize = getCursor().getCount()/4;
                    double vCursorSizeFractional = vCursorSize - (long)vCursorSize;

                    if (vCursorSizeFractional < 0.30)
                        return getCursor().getCount() / 4 + 1;
                    else if (vCursorSizeFractional > 0.60)
                        return getCursor().getCount() / 4 + 3;
                    else
                        return getCursor().getCount() / 4 + 2;
                }

        else
            return 0;
    }

    public void setBindViewVertical(View view, final Context context, final Cursor cursor){

        ViewHolder vViewHolder = (ViewHolder) view.getTag();
        if (vViewHolder == null) {
            Log.d("viewHolder", "View Holder null");
            vViewHolder = new ViewHolder(view);
            view.setTag(vViewHolder);
        }else{
            Log.d("view holder", "view holder diverso da null");
        }

        final int position = cursor.getPosition() * 2;

        if (position >= cursor.getCount())
            return;

        cursor.moveToPosition(position);

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImage1);

        vViewHolder.mImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, getCursor().getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
                mListener.onListViewItemSelected(true);
            }
        });

        vViewHolder.mImage1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(position);
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.FAVOURITE))==0) {
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                    Toast.makeText(context, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Il movie è già presente tra i preferiti :)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        if(position + 1 >= cursor.getCount()) {
            vViewHolder.mImage2.setImageDrawable(null);
            vViewHolder.mImage2.setVisibility(View.INVISIBLE);
            return;
        } else {
            vViewHolder.mImage2.setVisibility(View.VISIBLE);
        }

        cursor.moveToPosition(position + 1);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImage2);

        vViewHolder.mImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position + 1);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
                mListener.onListViewItemSelected(true);
            }
        });

        vViewHolder.mImage2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(position + 1);
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.FAVOURITE))==0) {
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                    Toast.makeText(context, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Il movie è già presente tra i preferiti :)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

    }

    private void setBindViewHorizontal(View view, final Context context, final Cursor cursor) {

        ViewHolder vViewHolder = (ViewHolder) view.getTag();
        if (vViewHolder == null) {
            Log.d("viewHolder", "View Holder null");
            vViewHolder = new ViewHolder(view);
            view.setTag(vViewHolder);
        }else{
            Log.d("view holder", "view holder diverso da null");
        }

        final int position = cursor.getPosition() * 4;

        if (position >= cursor.getCount())
            return;


        cursor.moveToPosition(position);

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImage1);

        vViewHolder.mImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, getCursor().getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
                mListener.onListViewItemSelected(true);
            }
        });

        vViewHolder.mImage1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(position);
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.FAVOURITE))==0) {
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                    Toast.makeText(context, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Il movie è già presente tra i preferiti :)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        if (position + 1 >= cursor.getCount()){
            vViewHolder.mImage2.setImageDrawable(null);
            vViewHolder.mImage2.setVisibility(View.INVISIBLE);
            return;
        } else
            vViewHolder.mImage2.setVisibility(View.VISIBLE);

        cursor.moveToPosition(position + 1);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImage2);

        vViewHolder.mImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position + 1);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
                mListener.onListViewItemSelected(true);
            }
        });

        vViewHolder.mImage2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(position + 1);
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.FAVOURITE))==0) {
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                    Toast.makeText(context, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Il movie è già presente tra i preferiti :)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        if (position + 2 >= cursor.getCount()){
            vViewHolder.mImage3.setImageDrawable(null);
            vViewHolder.mImage3.setVisibility(View.INVISIBLE);
            return;
        } else
            vViewHolder.mImage3.setVisibility(View.VISIBLE);

        cursor.moveToPosition(position + 2);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImage3);

        vViewHolder.mImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position + 2);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
                mListener.onListViewItemSelected(true);
            }
        });

        vViewHolder.mImage3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(position + 2);
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.FAVOURITE))==0) {
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                    Toast.makeText(context, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Il movie è già presente tra i preferiti :)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        if (position + 3 >= cursor.getCount())
        {
            vViewHolder.mImage4.setImageDrawable(null);
            vViewHolder.mImage4.setVisibility(View.INVISIBLE);
            return;
        } else
            vViewHolder.mImage4.setVisibility(View.VISIBLE);

        cursor.moveToPosition(position + 3);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImage4);

        vViewHolder.mImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position + 3);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
                mListener.onListViewItemSelected(true);
            }
        });

        vViewHolder.mImage4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(position + 3);
                if (cursor.getInt(cursor.getColumnIndex(MovieTableHelper.FAVOURITE))==0) {
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                    Toast.makeText(context, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Il movie è già presente tra i preferiti :)", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    private class ViewHolder {

        ImageView mImage1, mImage2, mImage3, mImage4;

        public ViewHolder(View view) {
            mImage1 = view.findViewById(R.id.imageViewColumn1);
            mImage2 = view.findViewById(R.id.imageViewColumn2);
            mImage3 = view.findViewById(R.id.imageViewColumn3);
            mImage4 = view.findViewById(R.id.imageViewColumn4);
        }
    }


    public interface IMovieAdapter{
        void onListViewItemSelected(boolean aResponse);
    }


}
