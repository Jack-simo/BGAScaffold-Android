package cn.bingoogolapple.scaffolding.demo.rxjava.api;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Blog;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Category;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.NetResult;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:09
 * 描述:
 */
public class Engine {
    private Engine() {
    }

    public static BlogApi getRxJavaApi() {
        boolean isBuildDebug = AppManager.getInstance().isBuildDebug();
        HttpLoggingInterceptor.Level logLevel = isBuildDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(logLevel))
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .writeTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl("http://192.168.31.152:8080/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(BlogApi.class);
    }

    public interface BlogApi {
        /**
         * 查询博客列表
         *
         * @param keyword
         * @return
         */
        @GET("api/blogs")
        Observable<NetResult<List<Blog>>> findBlogList(@Query("keyword") String keyword);

        /**
         * 查询分类列表
         *
         * @return
         */
        @GET("api/categorys")
        Observable<NetResult<List<Category>>> getCategoryList();
    }
}
