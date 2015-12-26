package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.Engine;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends TitlebarActivity {
    private Engine mEngine;

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

        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                com.squareup.okhttp.Response response = chain.proceed(chain.request());
                response.header("", "");
                return response;
            }
        });
        mEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(Engine.class);
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只有一个，并且是事件序列中的最后一个。
     * 需要注意的是，onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
     */
    private Observer mObserver = new Observer<String>() {
        @Override
        public void onNext(String s) {
            Logger.i(TAG, "onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        }

        /**
         * 事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。
         * RxJava 规定，当不会再有新的 onNext() 发出时，需要触发 onCompleted() 方法作为标志
         */
        @Override
        public void onCompleted() {
            Logger.i(TAG, "onCompleted ThreadName:" + Thread.currentThread().getName());
        }

        /**
         * 事件队列异常。在事件处理过程中出异常时，onError() 会被触发，同时队列自动终止，不允许再有事件发出。
         * @param e
         */
        @Override
        public void onError(Throwable e) {
            Logger.i(TAG, "onError ThreadName:" + Thread.currentThread().getName());
        }
    };

    private Subscriber mSubscriber = new Subscriber<String>() {
        /**
         * 在 subscribe 刚开始，而事件还未发送之前被调用，可以用于做一些准备工作，例如数据的清零或重置。
         * 这是一个可选方法，默认情况下它的实现为空。
         * 需要注意的是，如果对准备工作的线程有要求（例如弹出一个显示进度的对话框，这必须在主线程执行），
         * onStart() 就不适用了，因为它总是在 subscribe 所发生的线程被调用，而不能指定线程。
         */
        @Override
        public void onStart() {
            Logger.i(TAG, "onStart ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onNext(String s) {
            Logger.i(TAG, "onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onCompleted() {
            Logger.i(TAG, "onCompleted ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onError(Throwable e) {
            Logger.i(TAG, "onError ThreadName:" + Thread.currentThread().getName());
        }
    };

    public void test1(View v) {
        // 当 Observable 被订阅的时候(包括subscribe方法不传任何参数时)，OnSubscribe 的 call() 方法会自动被调用

        Logger.i(TAG, "method ThreadName:" + Thread.currentThread().getName());
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i(TAG, "call ThreadName:" + Thread.currentThread().getName());
                subscriber.onNext("Hello");
                subscriber.onNext("RxJava");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
    }

    public void test2(View v) {
//        executeTest2();
        new Thread() {
            @Override
            public void run() {
                executeTest2();
            }
        }.start();
    }

    private void executeTest2() {
        Logger.i(TAG, "method ThreadName:" + Thread.currentThread().getName());
        final Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i(TAG, "call ThreadName:" + Thread.currentThread().getName());
                subscriber.onNext("Hello");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

//        subscribeTest2(observable);
        new Thread() {
            @Override
            public void run() {
                subscribeTest2(observable);
            }
        }.start();
    }

    private void subscribeTest2(Observable observable) {
        Logger.i(TAG, "subscribe ThreadName:" + Thread.currentThread().getName());
        observable.subscribe(mSubscriber);
    }

    public void test3(View v) {
        Observable.just("Hello", "RxJava", "RxAndroid").observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    public void test4(View v) {
        String[] words = {"Hello", "RxJava", "RxAndroid"};
        Observable.from(words).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(mSubscriber);
    }

    public void test10(View v) {
        showLoadingDialog(R.string.loading);
        mEngine.loadMoreData(1).enqueue(new Callback<List<RefreshModel>>() {
            @Override
            public void onResponse(Response<List<RefreshModel>> response, Retrofit retrofit) {
                dismissLoadingDialog();
                Logger.i(TAG, response.body().toString());
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

}