package cn.bingoogolapple.scaffolding.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cn.bingoogolapple.scaffolding.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 下午4:36
 * 描述:
 */
public class NetUtil {

    private NetUtil() {
    }

    public static boolean isWifiConnected() {
        NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public static boolean isNetworkAvailable() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isAvailable();
    }

    private static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
