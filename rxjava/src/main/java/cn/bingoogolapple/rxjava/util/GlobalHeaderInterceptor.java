package cn.bingoogolapple.rxjava.util;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

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
                .header("globalHeader1", "globalHeader1Value")
                .header("globalHeader2", "globalHeader2Value")
                .addHeader("globalHeader3", "globalHeader3Value")
                .addHeader("globalHeader4", "globalHeader4Value")
                .method(originalRequest.method(), originalRequest.body())
                .build();
        return chain.proceed(compressedRequest);
    }

}