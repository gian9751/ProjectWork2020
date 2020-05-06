package com.example.androidproject.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.activity.Home;
import com.example.androidproject.localdata.MovieTableHelper;
import com.example.androidproject.localdata.Provider;

import static com.example.androidproject.localdata.MovieTableHelper.PAGE;

public class NetworkChangeReceiver extends BroadcastReceiver {

    int mCase;
    int mPage;

    public NetworkChangeReceiver(int aCase, int aPage) {
        super();
        mCase = aCase;
        mPage = aPage;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            switch (mCase){
                case 1:
                    ((Home) context).loadMovie(mPage);
                    ((Home) context).loadMovie(++mPage);
                    context.unregisterReceiver(this);
                    break;
                case 2:
                    ((Home) context).loadMovie(++mPage);
                    ((Home) context).loadMovie(++mPage);
                    context.unregisterReceiver(this);
                    break;
                case 3 :
                    context.getContentResolver().delete(Provider.MOVIES_URI, MovieTableHelper.FAVOURITE + "=0", null);
                    ContentValues vValues = new ContentValues();
                    vValues.put(PAGE, -1);
                    context.getContentResolver().update(Provider.MOVIES_URI, vValues, null, null);
                    ((Home) context).loadMovie(mPage);
                    ((Home) context).loadMovie(++mPage);
                    Toast.makeText(context, "I dati erano vecchi e sono stati aggiornati.", Toast.LENGTH_SHORT).show();
                    context.unregisterReceiver(this);
                    default:
                    Toast.makeText(context, "App connessa ad internet", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(context, "Lost internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
