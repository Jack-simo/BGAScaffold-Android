package cn.bingoogolapple.rxjava.util;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        Logger.i(String.format("Sending request %s on %s%n%s%n%s", request.url(), chain.connection(), request.headers(), getRequestBody(request)));

        Response response = chain.proceed(request);

        Logger.i(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (System.nanoTime() - startTime) / 1e6d, response.headers()));

        return response;
    }

    @NonNull
    private String getRequestBody(Request request) {
        if (request.method() == "POST") {
            RequestBody requestBody = request.body();
            if (requestBody instanceof FormBody) {
                FormBody formBody = (FormBody) requestBody;
                StringBuilder bodyStr = new StringBuilder("POST参数内容:");
                for (int i = 0; i < formBody.size(); i++) {
                    if (i > 0) {
                        bodyStr.append("&");
                    }
                    bodyStr.append(formBody.encodedName(i));
                    bodyStr.append("=");
                    bodyStr.append(formBody.encodedValue(i));
                }
                return bodyStr.toString();
            }

        }
        return "";
    }
}
