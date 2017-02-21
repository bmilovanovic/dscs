package com.example.dscs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Class that is triggered on connectivity change.
 */
public class WiFiStateReceiver extends BroadcastReceiver {

    private static final String TAG = WiFiStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            Log.d(TAG, "Device connected to the internet, start the service. ");
            context.startService(new Intent(context, CrawlingService.class));
        }
    }
}
