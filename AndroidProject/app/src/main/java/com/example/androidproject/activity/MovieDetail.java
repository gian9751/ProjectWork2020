package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidproject.R;
import com.example.androidproject.data.models.Movie;
import com.example.androidproject.localdata.FavouritesTableHelper;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.os.Build.ID;

public class MovieDetail extends AppCompatActivity {

    long mId = -1;

    RequestOptions mRequestOptions;

    TextView mTextViewTitle, mTextViewPlot, mReleaseDate, mUserScore;
    ImageView mImageViewPoster, mImageViewCover, mImageViewReleaseDate, mImageViewUserScore, mImageBlurredBack;
    FloatingActionButton mFabAddFavourite;


    ViewFlipper mViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setTitle("Movie details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mImageViewCover = findViewById(R.id.img_cover);
        mImageViewPoster = findViewById(R.id.img_poster);
        mTextViewPlot = findViewById(R.id.text_plot);
        mTextViewTitle = findViewById(R.id.text_title);
        mFabAddFavourite = findViewById(R.id.fab_add_favorite);
        mReleaseDate = findViewById(R.id.releaseDate_text);
        mUserScore = findViewById(R.id.userScoreText);
        mImageViewReleaseDate = findViewById(R.id.calendar_icon);
        mImageViewUserScore = findViewById(R.id.userScore_icon);

        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.drawable.ic_movie_placeholder);

        controlloConnessione();

        if (getIntent().getExtras() != null)
            mId = getIntent().getExtras().getLong(ID);

        if (mId!=-1) {
            Cursor vCursor = getContentResolver().query(Uri.parse(Provider.MOVIES_URI+"/"+mId), null, null,null, null);
            if (vCursor.moveToNext()) {
                String vTitolo = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE));
                String vPlot = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.PLOT));
                String vImageCover = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.BACKDROP_PATH));
                String vImagePoster = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.POSTER_PATH));
                String vReleaseDate = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.RELEASE_DATE));
                Double vUserScore = vCursor.getDouble(vCursor.getColumnIndex(MovieTableHelper.USER_SCORE));
                int vFavorite = vCursor.getInt(vCursor.getColumnIndex(MovieTableHelper.FAVOURITE));
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
                mReleaseDate.setText(vReleaseDate + "");
                mUserScore.setText(vUserScore + "/10");
                getSupportActionBar().setSubtitle(vTitolo);

                toastMessage();

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    landscape(vImagePoster);

                }

                if (vFavorite==1){
                    mFabAddFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_24dp));
                }
            }

        }else
            Toast.makeText(MovieDetail.this,"Errore, non è stato possibile trovare i dettagli del film",Toast.LENGTH_LONG).show();


        mFabAddFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mId!=-1) {
                    if (getContentResolver().query(Uri.parse(Provider.MOVIES_URI+"/"+mId),null,MovieTableHelper.FAVOURITE+" = 0",null,null).getCount()==1) {
                        ContentValues vValues = new ContentValues();
                        vValues.put(MovieTableHelper.FAVOURITE, 1);
                        int vResult = getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + mId), vValues, null, null);
                        mFabAddFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_24dp)); //CHECK
                        Toast.makeText(MovieDetail.this, "Movie aggiunto ai preferiti :)", Toast.LENGTH_LONG).show();
                    }
                    else{
                        ContentValues vValues = new ContentValues();
                        vValues.put(MovieTableHelper.FAVOURITE, 0);
                        int vResult = getContentResolver().update(Uri.parse(Provider.MOVIES_URI + "/" + mId), vValues, null, null);
                        mFabAddFavourite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_black_24dp)); //UNCHECK
                        Toast.makeText(MovieDetail.this, "Movie eliminato dai preferiti :)", Toast.LENGTH_LONG).show();
                    }
                }else
                    Toast.makeText(MovieDetail.this,"Errore, non è stato possibile visualizzare il film",Toast.LENGTH_LONG).show();
            }
        });



    }

    private void controlloConnessione() {
        if (!checkConnection())
            Toast.makeText(this,"La tua connessione dati è disattivata, l'immagine backdrop potrebbe non visualizzarsi!",Toast.LENGTH_LONG).show();
    }

    private void toastMessage() {
        mReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieDetail.this, "Release date", Toast.LENGTH_SHORT).show();
            }
        });

        mImageViewReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieDetail.this, "Release date", Toast.LENGTH_SHORT).show();
            }
        });

        mUserScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieDetail.this, "User score", Toast.LENGTH_SHORT).show();
            }
        });

        mImageViewUserScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieDetail.this, "User score", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void landscape(String vImagePoster){

            mViewFlipper = findViewById(R.id.imageFlipper);
            mImageBlurredBack = findViewById(R.id.img_blurredBackground);


            Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
            mViewFlipper.setInAnimation(in);
            mViewFlipper.setOutAnimation(out);
            mViewFlipper.setAutoStart(true);
            mViewFlipper.setFlipInterval(3000);

        Glide
                .with(MovieDetail.this)
                .setDefaultRequestOptions(mRequestOptions)
                .load(vImagePoster)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                .into(mImageBlurredBack);



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }
}
