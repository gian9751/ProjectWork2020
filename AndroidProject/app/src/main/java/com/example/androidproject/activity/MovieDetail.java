package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidproject.R;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.localdata.FavouritesTableHelper;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.os.Build.ID;

public class MovieDetail extends AppCompatActivity {

    long mId = -1;

    RequestOptions mRequestOptions;

    TextView mTextViewTitle, mTextViewPlot;
    ImageView mImageViewPoster, mImageViewCover;
    FloatingActionButton mFabAddFavourite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setTitle("Movie details");

        mImageViewCover = findViewById(R.id.img_cover);
        mImageViewPoster = findViewById(R.id.img_poster);
        mTextViewPlot = findViewById(R.id.text_plot);
        mTextViewTitle = findViewById(R.id.text_title);
        mFabAddFavourite = findViewById(R.id.fab_add_favorite);

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        if (getIntent().getExtras() != null)
            mId = getIntent().getExtras().getLong(ID);

        if (mId!=-1) {
            Cursor vCursor = getContentResolver().query(Uri.parse(Provider.MOVIES_URI+"/"+mId), null, null,null, null);
            if (vCursor.moveToNext()) {
                String vTitolo = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE));
                String vPlot = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.PLOT));
                String vImageCover = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.BACKDROP_PATH));
                String vImagePoster = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.POSTER_PATH));

                Log.d("Cover", vImageCover);

                Glide
                        .with(MovieDetail.this)
                        .setDefaultRequestOptions(mRequestOptions)
                        .load(vImageCover)
                        .into(mImageViewCover);


                Glide
                        .with(MovieDetail.this)
                        .setDefaultRequestOptions(mRequestOptions)
                        .load(vImagePoster)
                        .into(mImageViewPoster);

                mTextViewTitle.setText(vTitolo + "");
                mTextViewPlot.setText(vPlot + "");
            }

        }else
            Toast.makeText(MovieDetail.this,"Errore, non è stato possibile trovare i dettagli del film",Toast.LENGTH_LONG).show();


        mFabAddFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mId!=-1) {
// AGGIUNTA AI PREFERITI CON TABELLA FAVOURITE
//                    ContentValues vValues = new ContentValues();
//                    vValues.put(FavouritesTableHelper.MOVIE_ID, mId);
//                    Uri vResult = getContentResolver().insert(Provider.FAVOURITES_URI, vValues);
//                    Toast.makeText(MovieDetail.this,"Movie aggiunto ai preferiti :)",Toast.LENGTH_LONG).show();
                    ContentValues vValues = new ContentValues();
                    vValues.put(MovieTableHelper.FAVOURITE, 1);
                    int vResult = getContentResolver().update(Uri.parse(Provider.MOVIES_URI+"/"+mId), vValues, null, null);
                    Toast.makeText(MovieDetail.this,"Movie aggiunto ai preferiti :)",Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(MovieDetail.this,"Errore, non è stato possibile visualizzare il film",Toast.LENGTH_LONG).show();
            }
        });
    }
}
