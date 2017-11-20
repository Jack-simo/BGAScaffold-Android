package cn.bingoogolapple.scaffolding.demo.rxjava.api;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import cn.bingoogolapple.scaffolding.demo.rxjava.util.UploadManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Request ultimateRequest = chain.request().newBuilder()
                    .addHeader("uploadToken", UploadManager.getInstance().getToken())
                    .addHeader("expireTime", String.valueOf(UploadManager.getInstance().getExpireTime()))
                    .build();

            return chain.proceed(ultimateRequest);
        } catch (Exception e) {
            Logger.e(e, HeaderInterceptor.class.getSimpleName());
            throw e;
        }
    }
}
