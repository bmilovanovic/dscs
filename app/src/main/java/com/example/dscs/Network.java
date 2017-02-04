package com.example.dscs;

import android.content.Context;
import android.util.Log;

import com.example.dscs.utility.PreferenceUtility;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper class for connecting to the azure database.
 */
public class Network {

    private static final String TAG = Network.class.getSimpleName();

    public static MobileServiceClient getClient(Context context) {
        MobileServiceClient client = null;
        try {
            client = new MobileServiceClient(PreferenceUtility.getCurrentJob().getAzureUrl(), context);

            // Extend timeout from default of 10s to 20s
            client.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
        } catch (MalformedURLException mue) {
            Log.e(TAG, "There was an error creating the Mobile Service. Verify the URL", mue);
        }
        return client;
    }

    public static <E> MobileServiceTable<E> getTable(Context context, Class<E> clazz) {
        return getClient(context).getTable(clazz);
    }
}
