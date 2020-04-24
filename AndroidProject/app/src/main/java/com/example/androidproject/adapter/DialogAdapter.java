package com.example.androidproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cursoradapter.widget.CursorAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidproject.R;
import com.example.androidproject.activity.MovieDetail;
import com.example.androidproject.localdata.MovieTableHelper;

import static android.os.Build.ID;

public class DialogAdapter extends CursorAdapter {

    RequestOptions mRequestOptions;

    public DialogAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater vInflater = LayoutInflater.from(context);
        View vView = vInflater.inflate(R.layout.dialog_item_favorite,viewGroup,false);

        return  vView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ConstraintLayout vLayoutMovie = view.findViewById(R.id.layoutMovie);
        ImageView vImmagine = view.findViewById(R.id.poster_favorite_img);
        TextView vTitle = view.findViewById(R.id.favorite_title_text);

        ImageButton vHeartPreferito = view.findViewById(R.id.favorite_button);

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vImmagine);

        vTitle.setText(cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)));

        vLayoutMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Hai premuto su un movie",Toast.LENGTH_SHORT).show();
            }
        });

        vHeartPreferito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Hai premuto sul cuore",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
