package cn.bingoogolapple.basenote.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/23 下午9:28
 * 描述:
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private boolean mIsFirstReceiveBroadcast = true;
    private Callback mCallback;

    public NetworkChangeReceiver(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (!mIsFirstReceiveBroadcast) {
                if (NetUtil.isNetworkAvailable()) {
                    if (mCallback != null) {
                        mCallback.onNetworkConnected();
                    }
                } else {
                    if (mCallback != null) {
                        mCallback.onNetworkDisconnected();
                    }
                }
            } else {
                mIsFirstReceiveBroadcast = false;
            }
        }
    }

    public void register(Context context) {
        context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public interface Callback {
        void onNetworkConnected();

        void onNetworkDisconnected();
    }
}