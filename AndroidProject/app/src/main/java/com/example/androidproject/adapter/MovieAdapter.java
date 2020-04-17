package com.example.androidproject.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidproject.R;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public ArrayList<String> mDataSet;
    public Context mContext;

    public MovieAdapter(ArrayList<String> mDataSet, Context mContext) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        MovieViewHolder vHolder = new MovieViewHolder(vView);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieAdapter.MovieViewHolder holder, int position) {

        Glide
                .with(mContext)
                .load(mDataSet.get(position))
                .into(holder.mImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);

        }
    }
}
