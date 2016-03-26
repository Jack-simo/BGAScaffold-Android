package cn.bingoogolapple.basenote.util;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 上午12:42
 * 描述:
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();
    private static CrashHandler sInstance;
    private Context mContext;
    private UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private CrashHandler() {
    }

    public static final CrashHandler getInstance() {
        if (sInstance == null) {
            synchronized (CrashHandler.class) {
                if (sInstance == null) {
                    sInstance = new CrashHandler();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (isNeedHandle(ex)) {
            handleException(thread, ex);
        } else {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }

    private boolean isNeedHandle(Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            return true;
        }
    }

    private void handleException(Thread thread, Throwable ex) {
        new Thread() {
            @Override
            public void run() {
                // 除了主线程，其他线程默认情况下是没有开启looper消息处理的
                Looper.prepare();
                Toast.makeText(mContext, "系统出现未知异常，即将退出...", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        collectionException(ex);

        try {
            thread.sleep(2000);
            AppManager.getInstance().exit();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collectionException(Throwable ex) {
        String deviceInfo = Build.DEVICE + Build.VERSION.SDK_INT + Build.MODEL + Build.PRODUCT;
        String errorInfo = ex.getMessage();

        Logger.e(TAG, "deviceInfo -- " + deviceInfo + "  errorInfo -- " + errorInfo);
    }
}
