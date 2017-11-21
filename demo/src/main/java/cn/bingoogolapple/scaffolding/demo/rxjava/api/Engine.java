package cn.bingoogolapple.scaffolding.demo.rxjava.api;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.scaffolding.net.NetConverterFactory;
import cn.bingoogolapple.scaffolding.util.AppManager;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:09
 * 描述:
 */
public class Engine {
    public static final String BASE_URL = "http://10.0.9.161:8080/";
    private RxJavaApi mRxJavaApi;

    private Engine() {
        boolean isBuildDebug = AppManager.getInstance().isBuildDebug();
        HttpLoggingInterceptor.Level logLevel = isBuildDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(new HeaderInterceptor())
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS)
                .writeTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())) // 指定在 io 线程进行网络请求
                .addConverterFactory(NetConverterFactory.create())
                .client(client)
                .build();
        mRxJavaApi = retrofit.create(RxJavaApi.class);
    }

    private static class SingletonHolder {
        private static final Engine INSTANCE = new Engine();
    }

    public static Engine getInstance() {
        return Engine.SingletonHolder.INSTANCE;
    }

    public static RxJavaApi getRxJavaApi() {
        return getInstance().mRxJavaApi;
    }
}
