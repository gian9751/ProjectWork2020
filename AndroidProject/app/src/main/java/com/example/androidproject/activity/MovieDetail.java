package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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


import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

import static android.os.Build.ID;

public class MovieDetail extends AppCompatActivity {

    long mId = -1;

    RequestOptions mRequestOptions;

    TextView mTextViewTitle, mTextViewPlot;
    ImageView mImageViewPoster, mImageViewCover;
    FloatingActionButton mFabAddFavourite;

    //TEST
    ViewFlipper mViewFlipper;
    BlurView mBlurView;
    //TEST

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

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    landscape();
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

        //imageflipper per landscape

    }

    private void landscape(){

            mViewFlipper = findViewById(R.id.imageFlipper);


            Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
            mViewFlipper.setInAnimation(in);
            mViewFlipper.setOutAnimation(out);
            mViewFlipper.setAutoStart(true);
            mViewFlipper.setFlipInterval(3000);
    }
}
