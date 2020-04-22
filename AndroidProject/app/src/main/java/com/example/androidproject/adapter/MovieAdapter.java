package com.example.androidproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidproject.R;
import com.example.androidproject.activity.MovieDetail;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.localdata.MovieTableHelper;

import java.util.ArrayList;

public class MovieAdapter extends CursorAdapter {

    private static final String ID = "ID" ;

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

        ImageView vImmagine1 = view.findViewById(R.id.imageViewColumn1);
        ImageView vImmagine2 = view.findViewById(R.id.imageViewColumn2);

        int position = cursor.getPosition() * 2;

        if (position >= cursor.getCount())
            return;

        cursor.moveToPosition(position);

        Glide
                .with(context)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vImmagine1);

        vImmagine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"hai premuto immagine 1",Toast.LENGTH_SHORT).show();
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtra("BUNDLE_ID", vBundle);
                context.startActivity(vI);
            }
        });

        if (position + 1 >= cursor.getCount())
            return;

        cursor.moveToPosition(position + 1);

        Glide
                .with(context)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vImmagine2);

        vImmagine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"hai premuto immagine 2",Toast.LENGTH_SHORT).show();
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtra("BUNDLE_ID", vBundle);
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
