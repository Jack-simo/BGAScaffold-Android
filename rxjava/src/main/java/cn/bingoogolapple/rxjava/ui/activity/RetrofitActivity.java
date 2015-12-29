package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.LocalServerEngine;
import cn.bingoogolapple.rxjava.model.JsonResp;
import cn.bingoogolapple.rxjava.util.GlobalHeaderInterceptor;
import cn.bingoogolapple.rxjava.util.LoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitActivity extends TitlebarActivity {
    private LocalServerEngine mLocalServerEngine;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_retrofit);
    }

    @Override
    protected void setListener() {
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "max-age=60")
                    .build();
        }
    };

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Retrofit学习笔记");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.199.190:8080/netnote/retrofit/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        retrofit.client().interceptors().add(new LoggingInterceptor());
//        retrofit.client().interceptors().add(new GzipRequestInterceptor());
        retrofit.client().interceptors().add(new GlobalHeaderInterceptor());
        retrofit.client().interceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);


        mLocalServerEngine = retrofit.create(LocalServerEngine.class);
    }

    @Override
    public void onClick(View v) {
    }

    private Observer<JsonResp> mMsgObserver = new Observer<JsonResp>() {
        @Override
        public void onCompleted() {
            dismissLoadingDialog();
        }

        @Override
        public void onError(Throwable e) {
            dismissLoadingDialog();
            ToastUtil.show("数据加载失败");
        }

        @Override
        public void onNext(JsonResp jsonResp) {
            ToastUtil.show(jsonResp.msg);
        }
    };

    public void loginGet(View v) {
        mLocalServerEngine.loginGet("hello", "world")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void loginPost(View v) {
        mLocalServerEngine.loginPost("hello", "world")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void staticHeaders1(View v) {
        mLocalServerEngine.staticHeaders1()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void staticHeaders2(View v) {
        mLocalServerEngine.staticHeaders2()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void dynamicHeader1(View v) {
        mLocalServerEngine.dynamicHeader1("headerParam1Value")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void dynamicHeader2(View v) {
        mLocalServerEngine.dynamicHeader2("headerParam1Value", "headerParam2Value")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> showLoadingDialog(R.string.loading))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mMsgObserver);
    }

    public void test7(View v) {

    }

    public void test8(View v) {

    }

    public void test9(View v) {

    }

    public void test10(View v) {

    }

    public void test11(View v) {

    }

}