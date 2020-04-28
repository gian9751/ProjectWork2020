package com.example.androidproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
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

import java.util.ArrayList;

import static android.os.Build.ID;

public class MovieAdapter extends CursorAdapter {

    RequestOptions mRequestOptions ;

    class ViewHolder {

        ImageView mImage1, mImage2;

        public ViewHolder(View view) {
            mImage1 = view.findViewById(R.id.imageViewColumn1);
            mImage2 = view.findViewById(R.id.imageViewColumn2);
        }
    }

    public MovieAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater vInflater = LayoutInflater.from(context);
        View vView = vInflater.inflate(R.layout.item_movie,viewGroup,false);

        return  vView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

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
            }
        });

        if (position + 1 >= cursor.getCount())
            return;

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
            }
        });

    }

    @Override
    public int getCount() {
        if (getCursor()!=null)
            return (getCursor().getCount()%2==0)?getCursor().getCount()/2:getCursor().getCount()/2+1;
        else
            return 0;
    }
}
