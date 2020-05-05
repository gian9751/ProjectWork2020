package com.example.androidproject.activity;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.R;
import com.scwang.wave.MultiWaveHeader;

public class Splash extends AppCompatActivity {

    MultiWaveHeader waveFooter;
    private static int splashTimeOut = 5000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        waveFooter = findViewById(R.id.waveFooter);
        wave();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(Splash.this, Home.class));
                finish();
            }
        }, splashTimeOut);



    }

    private void wave() {
        waveFooter.setVelocity(1);
        waveFooter.setProgress(1);
        waveFooter.isRunning();
        waveFooter.setGradientAngle(45);
        waveFooter.setWaveHeight(40);
        waveFooter.setStartColor(Color.BLUE);
        waveFooter.setCloseColor(Color.CYAN);
    }
}
