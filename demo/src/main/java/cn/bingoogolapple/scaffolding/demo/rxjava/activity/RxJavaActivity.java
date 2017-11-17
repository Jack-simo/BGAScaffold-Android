package cn.bingoogolapple.scaffolding.demo.rxjava.activity;

import android.os.Bundle;

import com.orhanobut.logger.Logger;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.view.MvcActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class RxJavaActivity extends MvcActivity {

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_rxjava;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.btn_rxjava_test, o -> helloWorld());
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    private void helloWorld() {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> { // RxCachedThreadScheduler-1【受 A 影响】
            /**
             * 1.Observable 并不是在创建的时候就立即开始发送事件，而是在它被订阅的时候
             * 2.在一个正确运行的事件序列中, onComplete() 和 onError() 有且只有一个，并且是事件序列中的最后一个
             */
            try {
                if (!emitter.isDisposed()) {
                    Thread.sleep(3000);
                    Logger.d("onNext");
                    emitter.onNext(1);
                }
                if (!emitter.isDisposed()) {
                    Thread.sleep(3000);
                    Logger.d("onComplete");
                    emitter.onComplete();
                }
            } catch (Exception e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        }).doOnNext(data -> Logger.d("doOnNext1: " + data)) // RxCachedThreadScheduler-1【受 A 影响】
                .observeOn(Schedulers.newThread()) // 如果后续还有 observeOn，则影响两个 observeOn 之间操作符的执行线程【B newThread】
                .doOnNext(data -> Logger.d("doOnNext2: " + data)) // RxNewThreadScheduler-1 newThread【受 B 影响】
                .map(data -> { // RxNewThreadScheduler-1 newThread【受 B 影响】
                    Logger.d("map: " + data);
                    return data + 1;
                })
                .subscribeOn(Schedulers.io()) // 影响「被观察者」以及「被观察者和第一个 observeOn」之间操作符的执行线程【A io】
                .doOnNext(data -> Logger.d("doOnNext3: " + data)) // RxNewThreadScheduler-1 newThread【受 B 影响】
                .observeOn(AndroidSchedulers.mainThread()) // 如果后续没有 observeOn，则影响后续所有操作符的执行线程（包括 Observer 中的所有方法的执行线程）【C main】
                .doOnNext(data -> Logger.d("doOnNext4: " + data)) // main【受 C 影响】
                .flatMap(data -> { // main【受 C 影响】
                    Logger.d("flatMap: " + data);
                    return Observable.just("RxJava" + data);
                })
                .compose(bindToLifecycle()) // 如果 Activity 销毁时被观察者还没有发射 onComplete 或 onError，观察者会收到 onComplete
                .subscribe(new Observer<String>() { // main【受 C 影响】
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        Logger.d("订阅成功");
                    }

                    @Override
                    public void onNext(String item) {
                        Logger.d("onNext: " + item);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.d(throwable);
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("接收完所有数据");
                    }
                });
    }
}