package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.trello.rxlifecycle.ActivityEvent;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.rxjava.R;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class LifecycleActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_lifecycle);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("RxLifecycle学习笔记");

        Logger.i(TAG, "onCreate");
        Observable.interval(3, TimeUnit.SECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.i(TAG, "Unsubscribing subscription from onCreate()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE)) // 手动设置在activity onPause的时候取消订阅
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long num) {
                        Logger.i(TAG, "Started in onCreate(), running until onPause(): " + num);
                    }
                });
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.i(TAG, "onStart");
        Observable.interval(3, TimeUnit.SECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.i(TAG, "Unsubscribing subscription from onStart()");
                    }
                })
                .compose(this.<Long>bindToLifecycle()) // bindToLifecycle的自动取消订阅示例，因为是在onStart的时候调用，所以在onStop的时候自动取消订阅
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long num) {
                        Logger.i(TAG, "Started in onStart(), running until onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Logger.i(TAG, "onResume");
        Observable.interval(3, TimeUnit.SECONDS)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.i(TAG, "Unsubscribing subscription from onResume()");
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY)) // 手动设置在activity onDestroy的时候取消订阅
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long num) {
                        Logger.i(TAG, "Started in onResume(), running until onDestroy(): " + num);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy()");
    }
}
