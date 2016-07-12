package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.NetworkChangeReceiver;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;

public class MainActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle("RxJava学习笔记");

        mNetworkChangeReceiver.register(this);
    }

    @Override
    public void onClick(View v) {
    }

    public void helloworld(View v) {
        forward(HelloworldActivity.class);
    }

    public void testRxLifecycle(View v) {
        forward(LifecycleActivity.class);
    }

    public void testRxBinding(View v) {
        forward(BindingActivity.class);
    }

    public void testRxPreferences(View v) {
        forward(PreferencesActivity.class);
    }

    public void testRetrofit(View v) {
        forward(RetrofitActivity.class);
    }

    public void testRxBus(View v) {
        forward(RxBusActivity.class);
    }

    public void testOperator(View v) {
        forward(OperatorActivity.class);
    }

    public void testLeakCanary(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(20000);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChangeReceiver();
        super.onDestroy();
    }

    private void unregisterNetworkChangeReceiver() {
        unregisterReceiver(mNetworkChangeReceiver);
        mNetworkChangeReceiver = null;
    }

    private NetworkChangeReceiver mNetworkChangeReceiver = new NetworkChangeReceiver(new NetworkChangeReceiver.Callback() {
        @Override
        public void onNetworkConnected() {
            ToastUtil.show("成功接入网络");
        }

        @Override
        public void onNetworkDisconnected() {
            ToastUtil.show("网络连接断开");
        }
    });
}