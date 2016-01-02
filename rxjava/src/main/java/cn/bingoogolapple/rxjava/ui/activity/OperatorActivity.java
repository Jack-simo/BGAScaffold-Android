package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.rxjava.R;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

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

    public void take(View v) {
        // 选取前3个，不足3个时有多少个就输出多少个
        Observable.just(1, 2, 3, 4, 5).take(3).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void takeFirst(View v) {
        // 选取第一个符合条件的输出
        Observable.just(1, 2, 3, 4, 5).takeFirst(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer % 2 == 0;
            }
        }).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void takeLast(View v) {
        // 选取最后3个，不足3个时有多少个就输出多少个
        Observable.just(1, 2, 3, 4, 5).takeLast(7).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void takeLastBuffer(View v) {
        // 去最后2个，并转换成集合，takeLastBuffer <==> takeLast(count).toList()
        Observable.just(1, 2, 3, 4, 5).takeLastBuffer(2).subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                Logger.i(TAG, integers.toString());
            }
        });
    }

    public void takeUntil(View v) {
        // 一直取，知道integer为4
        Observable.just(1, 2, 3, 4, 5).takeUntil(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer == 4;
            }
        }).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void takeWhile(View v) {
        // 当integer小于4的时候一直取
        Observable.just(1, 2, 3, 4, 5).takeWhile(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 4;
            }
        }).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void distinct(View v) {
        Observable.just(1, 2, 3, 2, 3, 5).distinct().subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void first(View v) {
        Observable.just(1, 2, 3, 2, 3, 5).first().subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void last(View v) {
        Observable.just(1, 2, 3, 2, 3, 5).last().subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void skip(View v) {
        Observable.just(1, 2, 3, 4, 5, 6).skip(2).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void skipLast(View v) {
        Observable.just(1, 2, 3, 2, 3, 5).skipLast(2).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void skipWhile(View v) {
        // 有一次不满足条件时开始打印，即使后面再有满足条件的，还是会照常打印
        Observable.just(1, 2, 3, 2, 3, 5).skipWhile(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 3;
            }
        }).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void elementAt(View v) {
        Observable.just(1, 2, 3, 4, 5, 6).elementAt(7).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Logger.e(TAG, "错误" + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Logger.i(TAG, String.valueOf(integer));
            }
        });
    }

    public void elementAtOrDefault(View v) {
        Observable.just(1, 2, 3, 4, 5, 6).elementAtOrDefault(7, 10).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void switchMap(View v) {
        // concatMap()函数解决了flatMap()的交叉问题，提供了一种能够把发射的值连在一起的铺平函数，而不是合并它们
        Observable.just(1, 2, 3, 4, 5, 6).switchMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(Integer integer) {
                return Observable.just(String.valueOf(integer));
            }
        }).subscribe(s -> Logger.i(TAG, s));
    }

    public void scan(View v) {
        Observable.just(1, 2, 3, 4, 5, 6).scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).subscribe(integer -> Logger.i(TAG, String.valueOf(integer)));
    }

    public void buffer(View v) {
        Observable.just(1, 2, 3, 4, 5, 6).buffer(3).subscribe(list -> Logger.i(TAG, String.valueOf(list)));
    }

}