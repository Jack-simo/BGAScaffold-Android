package cn.bingoogolapple.rxjava.util;

import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/29 下午5:28
 * 描述:
 */
public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long startTime = System.nanoTime();
        Logger.i(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long endTime = System.nanoTime();
        Logger.i(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (endTime - startTime) / 1e6d, response.headers()));

        return response;
    }
}
