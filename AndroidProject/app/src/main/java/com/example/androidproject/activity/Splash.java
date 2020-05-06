package com.example.androidproject.activity;



import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.R;
import com.scwang.wave.MultiWaveHeader;

import java.io.IOException;

public class Splash extends AppCompatActivity {

    MultiWaveHeader waveFooter;
    private static int splashTimeOut = 5000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent i = new Intent(Splash.this, Home.class);

            //se la versione delle api sono >= 23 allora mostra la spalshscreen altrimenti salta direttamente a home per evitare outofmemory
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                setContentView(R.layout.activity_splashscreen);
                waveFooter = findViewById(R.id.waveFooter);
                wave();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(i);
                        finish();
                    }
                }, splashTimeOut);
            } else {
                        startActivity(i);
            }











    }

    private void wave() {
        waveFooter.setVelocity(2);
        waveFooter.setProgress(1);
        waveFooter.isRunning();
        waveFooter.setGradientAngle(45);
        waveFooter.setWaveHeight(70);
        waveFooter.setStartColor(Color.BLUE);
        waveFooter.setCloseColor(Color.CYAN);
    }
}
