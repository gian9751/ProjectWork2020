package com.example.androidproject.internetManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.activity.Home;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            Home home = (Home) context;
            home.loadMovie(1);
            home.loadMovie(2);
            home.unregisterReceiver(home.mNetworkReceiver);
            Log.d("Network Available ", "Flag No 1");
        }else{
            Toast.makeText(context, "Connessione ad internet persa! ", Toast.LENGTH_SHORT).show();
        }
    }
}
