package cn.bingoogolapple.scaffolding.demo.rxjava.activity;

import android.os.Bundle;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.rxjava.api.Engine;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Blog;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.UploadToken;
import cn.bingoogolapple.scaffolding.demo.rxjava.util.RxUtil;
import cn.bingoogolapple.scaffolding.demo.rxjava.util.UploadManager;
import cn.bingoogolapple.scaffolding.net.NetResult;
import cn.bingoogolapple.scaffolding.util.GsonUtil;
import cn.bingoogolapple.scaffolding.view.MvcActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableOperator;
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
        setOnClick(R.id.btn_rxjava_helloworld, o -> helloWorld());
        setOnClick(R.id.btn_rxjava_search, o -> forward(SearchActivity.class));
        setOnClick(R.id.btn_rxjava_sticky_search, o -> forward(StickySearchActivity.class));
        setOnClick(R.id.btn_rxjava_add_blog, o -> addBlog());
        setOnClick(R.id.btn_rxjava_custom_perator, o -> customOperator());
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
                        emitter.onNext(0);
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
            }).doOnNext(data -> Logger.d("doOnNext1")) // RxCachedThreadScheduler-1【受 A 影响】
                    .doOnComplete(() -> Logger.d("doOnComplete1")) // RxCachedThreadScheduler-1【受 A 影响】
                    .doOnError(throwable -> Logger.d("doOnError1")) // RxCachedThreadScheduler-1【受 A 影响】
                    .map(data -> { // RxCachedThreadScheduler-1【受 A 影响】
                        Logger.d("map1");
                        return data + 1;
                    })
                    .flatMap(data -> {  // RxCachedThreadScheduler-1【受 A 影响】
                        Logger.d("flatMap1");
                        return Observable.just(data);
                        // return Observable.just(data)
                        //         .observeOn(Schedulers.newThread()); // 【B newThread】如果后续还有 observeOn，则影响两个 observeOn 之间操作符的执行线程「flatMap 内部执行 observeOn 来切换线程也会外部的操作符执行线程，但不会影响外部的 doOnComplete」
                    })

                    .observeOn(Schedulers.newThread()) // 【B newThread】如果后续还有 observeOn，则影响两个 observeOn 之间操作符的执行线程

                    .doOnNext(data -> Logger.d("doOnNext2")) // RxNewThreadScheduler-2【受 B 影响】
                    .doOnComplete(() -> Logger.d("doOnComplete2")) // RxNewThreadScheduler-2【受 B 影响】
                    .doOnError(throwable -> Logger.d("doOnError2")) // RxNewThreadScheduler-2【受 B 影响】
                    .flatMap(integer -> {  // RxNewThreadScheduler-2【受 B 影响】
                        Logger.d("flatMap2");
                        return Observable.just(Long.valueOf(integer));
                    })
                    .map(data -> { // RxNewThreadScheduler-2【受 B 影响】
                        Logger.d("map2");
                        return data + 1;
                    })

                    .doOnSubscribe(disposable -> Logger.d("订阅成功 doOnSubscribe4")) // RxCachedThreadScheduler-1【在 A 处的 subscribeOn 之前，受 A 影响】
                    .subscribeOn(Schedulers.io()) // 【A io】第一次执行，影响「被观察者」以及「被观察者和第一个 observeOn」之间操作符的执行线程
                    .doOnSubscribe(disposable -> Logger.d("订阅成功 doOnSubscribe3")) // RxNewThreadScheduler-1【在 D 处的 subscribeOn 之前，受 D 影响】

                    .doOnNext(data -> Logger.d("doOnNext3")) // RxNewThreadScheduler-2【受 B 影响】
                    .doOnComplete(() -> Logger.d("doOnComplete3")) // RxNewThreadScheduler-2【受 B 影响】
                    .doOnError(throwable -> Logger.d("doOnError3")) // RxNewThreadScheduler-2【受 B 影响】
                    .flatMap(integer -> {  // RxNewThreadScheduler-2【受 B 影响】
                        Logger.d("flatMap3");
                        return Observable.just(Double.valueOf(integer));
                    })
                    .map(data -> { // RxNewThreadScheduler-2【受 B 影响】
                        Logger.d("map3");
                        return data + 1;
                    })

                    .observeOn(AndroidSchedulers.mainThread()) // 【C main】如果后续没有 observeOn，则影响后续所有操作符的执行线程（包括 Observer 中的所有方法的执行线程）

                    .doOnNext(data -> Logger.d("doOnNext4")) // main【受 C 影响】
                    .doOnComplete(() -> Logger.d("doOnComplete4")) // main【受 C 影响】
                    .doOnError(throwable -> Logger.d("doOnError4")) // main【受 C 影响】
                    .map(data -> { // main【受 C 影响】
                        Logger.d("map4");
                        return data + 1;
                    })

                    .compose(bindToLifecycle()) // 如果 Activity 销毁时被观察者还没有发射 onComplete 或 onError，会回调「compose 之后的 doOnComplete」和「Observer 的 onComplete 方法」

                    .doOnNext(data -> Logger.d("doOnNext5")) // main【受 C 影响】
                    .doOnComplete(() -> Logger.d("doOnComplete5")) // main【受 C 影响】
                    .doOnError(throwable -> Logger.d("doOnError5")) // main【受 C 影响】
                    .map(data -> { // main【受 C 影响】
                        Logger.d("map5");
                        return data + 1;
                    })
                    .flatMap(data -> { // main【受 C 影响】
                        Logger.d("flatMap4");
                        return Observable.just("转换了 " + data + " 次 map 操作");
                    })

                    .doOnSubscribe(disposable -> Logger.d("订阅成功 doOnSubscribe2")) // RxNewThreadScheduler-1【在 subscribeOn 之前，受 D 影响】
                    .subscribeOn(Schedulers.newThread()) // 【D newThread】第二次执行，只影响两个 subscribeOn 之间的 doOnSubscribe
                    .doOnSubscribe(disposable -> Logger.d("订阅成功 doOnSubscribe1")) // RxComputationThreadPool-1【在 subscribeOn 之后，与 Observer 的 onSubscribe 方法一样】

                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable disposable) { // RxComputationThreadPool-1
                            // 在 subscribe 刚开始，但事件还未发送之前被调用，可以用于做一些准备工作，如果对准备工作的线程有要求，可能该方法就不适用做准备工作
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

    private String mCoverFilePath = "/sdcard/avatar.png";

    private void addBlog() {
        final Blog blog = new Blog();
        blog.setCategoryId(1L);
        blog.setTitle("Token + 图片上传 + 错误重试");
        blog.setContent("我是内容");

        Observable.defer(() -> UploadManager.getInstance().getUploadObservable(mCoverFilePath))
                .switchMap(filePath -> {
                    mCoverFilePath = filePath;
                    blog.setCover(mCoverFilePath);
                    return Engine.getRxJavaApi().addBlog(blog);
                })
                .compose(RxUtil.handleResultThreadLifecycleRetry(this))
                .subscribe(result -> Logger.d("添加博客成功"), throwable -> {
                    Logger.d("添加博客失败");
                    throwable.printStackTrace();
                });
    }

    private void customOperator() {
        Engine.getRxJavaApi().getUploadToken()
                .delay(2000, TimeUnit.MILLISECONDS)
                .lift(new NetResultOperator())
                .doOnSubscribe(disposable -> showLoadingDialog("正在获取Token..."))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadToken -> {
                    dismissLoadingDialog();
                    Logger.d("获取成功：" + GsonUtil.toJson(uploadToken));
                }, throwable -> {
                    dismissLoadingDialog();
                    Logger.d("获取文件上传 Token 失败" + throwable.getMessage());
                });
    }

    private class NetResultOperator implements ObservableOperator<UploadToken, NetResult<UploadToken>> {
        @Override
        public Observer<? super NetResult<UploadToken>> apply(Observer<? super UploadToken> observer) throws Exception {
            return new Observer<NetResult<UploadToken>>() {
                private Disposable mDisposable;

                @Override
                public void onSubscribe(Disposable disposable) {
                    mDisposable = disposable;
                    observer.onSubscribe(mDisposable);
                }

                @Override
                public void onNext(NetResult<UploadToken> netResult) {
                    if (!mDisposable.isDisposed()) {
                        observer.onNext(netResult.data);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (!mDisposable.isDisposed()) {
                        observer.onError(e);
                    }
                }

                @Override
                public void onComplete() {
                    if (!mDisposable.isDisposed()) {
                        observer.onComplete();
                    }
                }
            };
        }
    }
}