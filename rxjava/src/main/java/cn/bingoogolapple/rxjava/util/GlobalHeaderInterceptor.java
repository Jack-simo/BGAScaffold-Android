package cn.bingoogolapple.rxjava.util;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/29 下午5:31
 * 描述:
 */
public class GlobalHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request compressedRequest = originalRequest.newBuilder()
                .header("globalHeader1", "globalHeader1Value")  // overrides the respective header key with value if there is already an existing header identified by key
                .header("globalHeader2", "globalHeader2Value")
                .addHeader("globalHeader3", "globalHeader3Value") // adds the respective header key and value even if there is an existing header field with the same key
                .addHeader("globalHeader4", "globalHeader4Value")
                .method(originalRequest.method(), originalRequest.body())
                .build();
        return chain.proceed(compressedRequest);
    }

}