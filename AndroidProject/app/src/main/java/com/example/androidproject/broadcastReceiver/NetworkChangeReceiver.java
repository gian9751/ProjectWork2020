package com.example.androidproject.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.androidproject.activity.Home;

public class NetworkChangeReceiver extends BroadcastReceiver {

    int mCase;

    public NetworkChangeReceiver(int aCase) {
        super();
        mCase = aCase;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            switch (mCase){
                case 1:
                    context.unregisterReceiver(this);
                    break;
                case 2:
                    context.unregisterReceiver(this);
                    break;
                default:
                    Toast.makeText(context, "App connessa ad internet", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(context, "Connessione ad internet persa! ", Toast.LENGTH_SHORT).show();
        }
    }
}
