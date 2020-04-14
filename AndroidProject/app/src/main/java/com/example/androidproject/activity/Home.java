package com.example.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.androidproject.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Movies");
    }
}
