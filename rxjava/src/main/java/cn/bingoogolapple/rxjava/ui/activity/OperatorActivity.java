package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.trello.rxlifecycle.ActivityEvent;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.rxjava.R;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/2 下午8:13
 * 描述:
 */
public class OperatorActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_operator);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("操作符学习笔记");
    }

    @Override
    public void onClick(View v) {
    }

    public void repeat(View v) {
        Observable.just("one", "two", "three").repeat(3).subscribe(s -> Logger.i(TAG, s));
    }

    public void defer1(View v) {
        Observable<String> deferred = Observable.defer(this::defer1);
        // 不订阅就不会打印defer1
        deferred.subscribe(s -> Logger.i(TAG, s));
    }

    private Observable<String> defer1() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                try {
                    Logger.i(TAG, "defer1");
                    subscriber.onNext("defer1 value");
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public void defer2(View v) {
        Observable<String> deferred = defer2("defer2 value");
        // 不订阅就不会打印defer2
        deferred.subscribe(s -> Logger.i(TAG, s));
    }

    private Observable<String> defer2(final String s) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    Logger.i(TAG, "defer2");
                    return Observable.just(s);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public void range(View v) {
        // 从10开始发射3个数字，重复发送三次
        Observable.range(10, 3).repeat(3).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void interval1(View v) {
        // 延时3秒开始发送，之后每隔3秒发送一次
        Observable.interval(3, TimeUnit.SECONDS).compose(bindUntilEvent(ActivityEvent.PAUSE)).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void interval2(View v) {
        /**
         * 延时0秒开始发送，之后每隔3秒发送一次
         */
        Observable.interval(0, 3, TimeUnit.SECONDS).compose(bindUntilEvent(ActivityEvent.PAUSE)).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void timer(View v) {
        // 演示3秒发送
        Observable.timer(3, TimeUnit.SECONDS).compose(bindUntilEvent(ActivityEvent.PAUSE)).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }
}