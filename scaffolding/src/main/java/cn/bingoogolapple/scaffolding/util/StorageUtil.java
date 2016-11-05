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
    public static final String DIR_ROOT = "BGANote";
    public static final String DIR_FILE = DIR_ROOT + File.separator + "file";
    public static final String DIR_IMAGE = DIR_ROOT + File.separator + "image";
    public static final String DIR_CACHE = DIR_ROOT + File.separator + "cache";
    public static final String DIR_AUDIO = DIR_ROOT + File.separator + "audio";

    /**
     * 判断外存储是否可写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取当前app文件存储目录
     *
     * @return
     */
    public static File getFileDir() {
        File fileDir = null;
        if (isExternalStorageWritable()) {
            fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_FILE);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
        }
        return fileDir;
    }

    /**
     * 获取当前app外部存储根目录
     *
     * @return
     */
    public static File getAppDir() {
        File appDir = null;
        if (isExternalStorageWritable()) {
            appDir = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_ROOT);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
        }
        return appDir;
    }

    /**
     * 获取当前app图片文件存储目录
     *
     * @return
     */
    public static File getImageDir() {
        File imageDir = null;
        if (isExternalStorageWritable()) {
            imageDir = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_IMAGE);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
        }
        return imageDir;
    }

    /**
     * 获取当前app缓存文件存储目录
     *
     * @return
     */
    public static File getCacheDir() {
        File cacheDir = null;
        if (isExternalStorageWritable()) {
            cacheDir = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_CACHE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
        }
        return cacheDir;
    }

    /**
     * 获取当前app音频文件存储目录
     *
     * @return
     */
    public static File getAudioDir() {
        File audioDir = null;
        if (isExternalStorageWritable()) {
            audioDir = new File(Environment.getExternalStorageDirectory() + File.separator + DIR_AUDIO);
            if (!audioDir.exists()) {
                audioDir.mkdirs();
            }
        } else {
            throw new RuntimeException("外部存储不可写");
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