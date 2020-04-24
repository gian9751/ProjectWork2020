package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidproject.R;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;

import static android.os.Build.ID;

public class MovieDetail extends AppCompatActivity {

    long mId = -1;

    RequestOptions mRequestOptions;

    TextView mTextViewTitle, mTextVeiwPlot;
    ImageView mImageViewPoster, mImageViewCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setTitle("Movie details");

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        mImageViewCover = findViewById(R.id.img_cover);
        mImageViewPoster = findViewById(R.id.img_poster);
        mTextVeiwPlot = findViewById(R.id.text_plot);
        mTextViewTitle = findViewById(R.id.text_title);

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
                mTextVeiwPlot.setText(vPlot + "");
            }

        }else
            Toast.makeText(MovieDetail.this,"Errore, non è stato possibile trovare i dettagli del film",Toast.LENGTH_LONG).show();
    }
}
