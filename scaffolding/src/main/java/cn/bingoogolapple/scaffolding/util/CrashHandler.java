package cn.bingoogolapple.scaffolding.util;

import android.os.Build;
import android.os.Looper;

import com.orhanobut.logger.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 上午12:42
 * 描述:
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static CrashHandler sInstance;
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

    public void init() {
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
                ToastUtil.show("系统出现未知异常，即将退出...");
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
        Logger.e("【deviceInfo】\n" + getDeviceInfo() + "【errorInfo】\n" + getErrorInfo(ex));
    }

    private String getErrorInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
        return writer.toString();
    }

    private String getDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        // 通过反射获取系统的硬件信息
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射 ,获取私有的信息
                field.setAccessible(true);
                sb.append(field.getName() + "=" + field.get(null).toString());
                sb.append("\n");
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return sb.toString();
    }
}
