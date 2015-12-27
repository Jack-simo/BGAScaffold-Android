package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
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

    public void testLeakCanary(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(20000);
            }
        }).start();
    }
}