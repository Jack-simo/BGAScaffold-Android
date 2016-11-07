package cn.bingoogolapple.scaffolding.util;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/15 上午12:16
 * 描述:
 */
public class StorageUtil {

    private StorageUtil() {
    }

    /**
     * 判断外存储是否可写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private static File getAppDir() {
        File rootDir;
        if (isExternalStorageWritable()) {
            rootDir = new File(Environment.getExternalStorageDirectory(), AppManager.getInstance().getAppName());
        } else {
            rootDir = AppManager.getApp().getFilesDir();
        }
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        return rootDir;
    }

    /**
     * 获取当前app文件存储目录
     *
     * @return
     */
    public static File getFileDir() {
        File fileDir = new File(getAppDir(), "file");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    /**
     * 获取当前app图片文件存储目录
     *
     * @return
     */
    public static File getImageDir() {
        File imageDir = new File(getAppDir(), "image");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        return imageDir;
    }

    /**
     * 获取当前app缓存文件存储目录
     *
     * @return
     */
    public static File getCacheDir() {
        File cacheDir = new File(getAppDir(), "cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 获取当前app音频文件存储目录
     *
     * @return
     */
    public static File getAudioDir() {
        File audioDir = new File(getAppDir(), "audio");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        return audioDir;
    }

    /**
     * 根据输入流，保存文件
     *
     * @param file
     * @param is
     * @return
     */
    public static boolean writeFile(File file, InputStream is) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = is.read(data)) != -1) {
                os.write(data, 0, length);
            }
            os.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeStream(os);
            closeStream(is);
        }
    }

    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("关闭流失败!", e);
            }
        }
    }
}