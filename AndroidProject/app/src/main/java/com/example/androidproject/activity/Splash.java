package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidproject.R;
import com.example.androidproject.waves.WaveView;

public class Splash extends AppCompatActivity {

    private static final String TAG = Splash.class.getSimpleName();

    private WaveView waveViewFooter, waveViewHeader;
    private static int splashTimeOut = 5000;

    ImageView mImageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        final Intent i = new Intent(Splash.this, Home.class);

        waveViewFooter = findViewById(R.id.waveFooter);
        waveViewHeader = findViewById(R.id.waveHeader);

        waveViewFooter.addDefaultWaves(2, 1);
        waveViewHeader.addDefaultWaves(2, 1);
        mImageLogo = findViewById(R.id.logo);
        

        waveViewFooter.startAnimation();
        waveViewHeader.startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(i);
                finish();
            }
        }, splashTimeOut);

    }
}
