package com.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SAM-PC2 on 2017/7/18.
 */
public class NetUtil {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
