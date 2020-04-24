package com.example.androidproject.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import androidx.cursoradapter.widget.CursorAdapter;

public class DialogAdapter extends CursorAdapter {

    public DialogAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
