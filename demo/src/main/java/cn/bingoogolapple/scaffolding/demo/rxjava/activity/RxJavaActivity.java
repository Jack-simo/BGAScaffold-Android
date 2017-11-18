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
        Schedulers.computation().scheduleDirect(() -> {
            Logger.d("在 computation 线程订阅"); // RxComputationThreadPool-1
            Observable.create((ObservableOnSubscribe<Integer>) emitter -> { // RxCachedThreadScheduler-1【受 A 影响】
                try {
                    if (!emitter.isDisposed()) { // Observable 并不是在创建的时候就立即开始发送事件，而是在它被订阅的时候
                        Thread.sleep(3000);
                        Logger.d("onNext");
                        emitter.onNext(1);
                    }
                    if (!emitter.isDisposed()) {
                        Thread.sleep(3000);
                        Logger.d("onComplete");
                        emitter.onComplete(); // 在一个正确运行的事件序列中, onComplete() 和 onError() 有且只有一个，并且是事件序列中的最后一个
                    }
                } catch (Exception e) {
                    if (!emitter.isDisposed()) {
                        Logger.d("onError");
                        emitter.onError(e);
                    }
                }
            }).doOnNext(data -> Logger.d("doOnNext1: " + data)) // RxCachedThreadScheduler-1【受 A 影响】
                    .observeOn(Schedulers.newThread()) // 如果后续还有 observeOn，则影响两个 observeOn 之间操作符的执行线程【B newThread】
                    .doOnNext(data -> Logger.d("doOnNext2: " + data)) // RxNewThreadScheduler-1 newThread【受 B 影响】
                    .doOnComplete(() -> Logger.d("doOnComplete1")) // RxNewThreadScheduler-1 newThread【受 B 影响】
                    .map(data -> { // RxNewThreadScheduler-1 newThread【受 B 影响】
                        Logger.d("map: " + data);
                        return data + 1;
                    })
                    .doOnSubscribe(disposable -> Logger.d("订阅成功 doOnSubscribe2")) // RxCachedThreadScheduler-1【在 subscribeOn 之前，受 A 影响】
                    .subscribeOn(Schedulers.io()) // 影响「被观察者」以及「被观察者和第一个 observeOn」之间操作符的执行线程【A io】
                    .doOnSubscribe(disposable -> Logger.d("订阅成功 doOnSubscribe1")) // RxComputationThreadPool-1【在 subscribeOn 之后，与 Observer 的 onSubscribe 方法一样】
                    .doOnComplete(() -> Logger.d("doOnComplete2")) // RxNewThreadScheduler-1 newThread【受 B 影响】
                    .doOnNext(data -> Logger.d("doOnNext3: " + data)) // RxNewThreadScheduler-1 newThread【受 B 影响】
                    .observeOn(AndroidSchedulers.mainThread()) // 如果后续没有 observeOn，则影响后续所有操作符的执行线程（包括 Observer 中的所有方法的执行线程）【C main】
                    .doOnComplete(() -> Logger.d("doOnComplete3")) // main【受 C 影响】
                    .compose(bindToLifecycle()) // 如果 Activity 销毁时被观察者还没有发射 onComplete 或 onError，会回调「compose 之后的 doOnComplete」和「Observer 的 onComplete 方法」
                    .doOnComplete(() -> Logger.d("doOnComplete4")) // main【受 C 影响】
                    .doOnNext(data -> Logger.d("doOnNext4: " + data)) // main【受 C 影响】
                    .flatMap(data -> { // main【受 C 影响】
                        Logger.d("flatMap: " + data);
                        return Observable.just("RxJava" + data);
                    })
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable disposable) { // RxComputationThreadPool-1
                            // 在 subscribe 刚开始，而事件还未发送之前被调用，可以用于做一些准备工作，如果对准备工作的线程有要求，可能该方法就不适用做准备工作
                            // 因为该方法总是在 subscribe 所发生的线程被调用，而不能指定线程（例如本例就不适合在该方法中显示对话框，因为是在 computation 中 subscribe 的）
                            Logger.d("订阅成功 onSubscribe");
                        }

                        @Override
                        public void onNext(String item) { // main【受 C 影响】
                            Logger.d("onNext: " + item);
                        }

                        @Override
                        public void onComplete() { // main【受 C 影响】
                            Logger.d("接收完所有数据");
                        }

                        @Override
                        public void onError(Throwable throwable) { // main【受 C 影响】
                            Logger.d(throwable);
                        }
                    });
        });
    }
}