package cn.bingoogolapple.basenote.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 下午4:36
 * 描述:
 */
public class NetManager {
    private static final String TAG = NetManager.class.getSimpleName();
    private static NetManager sInstance;
    private Context mContext;

    private NetManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static final NetManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NetManager.class) {
                if (sInstance == null) {
                    sInstance = new NetManager(context);
                }
            }
        }
        return sInstance;
    }

    public boolean isWifiConnected() {
        NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public boolean isNetworkAvailable() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isAvailable();
    }

    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
