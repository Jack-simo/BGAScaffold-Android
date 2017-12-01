package cn.bingoogolapple.scaffolding.demo.rxjava.util;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.bingoogolapple.scaffolding.demo.rxjava.api.Engine;
import cn.bingoogolapple.scaffolding.util.SPUtil;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/11/20
 * 描述:
 */
public class UploadManager {
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private static final String SP_KEY_TOKEN = "SP_KEY_TOKEN";
    private static final String SP_KEY_EXPIRE_TIME = "SP_KEY_EXPIRE_TIME";

    private UploadManager() {
    }

    private static class SingletonHolder {
        private static final UploadManager INSTANCE = new UploadManager();
    }

    public static UploadManager getInstance() {
        return UploadManager.SingletonHolder.INSTANCE;
    }

    public static String getToken() {
        return SPUtil.getString(SP_KEY_TOKEN);
    }

    public static long getExpireTime() {
        return SPUtil.getLong(SP_KEY_EXPIRE_TIME);
    }

    // 获取文件上传 Token 的 Observable
    public Observable getImageTokenObservable() {
        if (mRefreshing.compareAndSet(false, true)) {
            Logger.d("没有请求，发起一次新的 Token 请求");
            return Engine.getRxJavaApi()
                    .getUploadToken()
                    .doOnNext(uploadToken -> {
                        SPUtil.putString(SP_KEY_TOKEN, uploadToken.getToken());
                        SPUtil.putLong(SP_KEY_EXPIRE_TIME, uploadToken.getExpireTime());
                        mRefreshing.set(false);
                    })
                    .doOnError(throwable -> mRefreshing.set(false));
        } else {
            Logger.d("已经有 Token 请求，延迟 5 秒重试");
            return Observable.timer(5000, TimeUnit.MILLISECONDS);
        }
    }

    // 获取文件上传的 Observable
    public Observable<String> getUploadObservable(String filePath) {
        if (filePath != null && !filePath.startsWith("http://") && !filePath.startsWith("https://")) {
            File file = new File(filePath);
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                    .build();

            return Engine.getRxJavaApi().upload(body)
                    .map(fileName -> Engine.BASE_URL + "api/file/browse/" + fileName);
        } else {
            return Observable.just(filePath == null ? "" : filePath);
        }

    }
}
