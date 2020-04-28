package com.example.androidproject.adapter;

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
import com.example.androidproject.localdata.FavouritesTableHelper;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;

import static android.os.Build.ID;

public class DialogAdapter extends CursorAdapter {

    RequestOptions mRequestOptions;

    class ViewHolder {

        ConstraintLayout mLayoutMovie;
        ImageView mImmagine;
        TextView mTitle;
        ImageButton mHeartPreferito;

        public ViewHolder(View view) {
            mLayoutMovie = view.findViewById(R.id.layoutMovie);
            mImmagine = view.findViewById(R.id.poster_favorite_img);
            mTitle = view.findViewById(R.id.favorite_title_text);
            mHeartPreferito = view.findViewById(R.id.favorite_button);
        }
    }

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
// QUERY CON TABELLA PREFERITI
//        long vId = cursor.getLong(cursor.getColumnIndex(FavouritesTableHelper.MOVIE_ID));
//        Cursor vCursorMovie = context.getContentResolver().query(Uri.parse(Provider.MOVIES_URI+"/"+vId), null, null,null, null);

        ViewHolder vViewHolder = (ViewHolder) view.getTag();
        if (vViewHolder == null) {
            Log.d("viewHolderDialog", "View Holder null");
            vViewHolder = new ViewHolder(view);
            view.setTag(vViewHolder);
        }else{
            Log.d("viewHolderDialog", "view holder diverso da null");
        }

        final int position = cursor.getPosition();

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        Glide
                .with(context)
                .setDefaultRequestOptions(mRequestOptions)
                .load(cursor.getString(cursor.getColumnIndex(MovieTableHelper.POSTER_PATH)))
                .into(vViewHolder.mImmagine);

        vViewHolder.mTitle.setText(cursor.getString(cursor.getColumnIndex(MovieTableHelper.TITLE)));

        vViewHolder.mLayoutMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                Intent vI = new Intent(context,MovieDetail.class);
                Bundle vBundle = new Bundle();
                vBundle.putLong(ID, cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID)));
                vI.putExtras(vBundle);
                context.startActivity(vI);
            }
        });

        final ViewHolder finalVViewHolder = vViewHolder;
        vViewHolder.mHeartPreferito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                ContentValues vValues = new ContentValues();
                vValues.put(MovieTableHelper.FAVOURITE, 0);
                finalVViewHolder.mHeartPreferito.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_24dp)); //UNCHECK
                int vResult = context.getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + cursor.getLong(cursor.getColumnIndex(MovieTableHelper._ID))), vValues, null, null);
                Toast.makeText(context, "Movie eliminato dai preferiti :)", Toast.LENGTH_LONG).show();
            }
        });
    }
}
