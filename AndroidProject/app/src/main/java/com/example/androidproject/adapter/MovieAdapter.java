package com.example.androidproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidproject.R;
import com.example.androidproject.activity.MovieDetail;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<String> {

    ArrayList<String> mMovie;
    Context mContext;
    LayoutInflater mInflater;

    public MovieAdapter(Context context, ArrayList<String> movie) {
        super(context, R.layout.item_movie, movie);

        mMovie = movie;
        mContext = context;

        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_movie,parent,false);
        }

        ImageView vImageView = convertView.findViewById(R.id.imageView);

        Glide
                .with(mContext)
                .load(mMovie.get(position))
                .into(vImageView);

        return convertView;
    }
}
