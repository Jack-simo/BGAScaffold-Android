package cn.bingoogolapple.photopicker.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/4/10 下午5:48
 * 描述:
 */
public class BGAImageLoader {
    private static BGAImageLoader mInstance;
    private LruCache<String, Bitmap> mLruCache;

    private BGAImageLoader() {
        init();
    }

    private void init() {

        // 获取最大的可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public static BGAImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (BGAImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new BGAImageLoader();
                }
            }
        }
        return mInstance;
    }


    public void loadImage(final String path, final ImageView imageView) {
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            imageView.setImageBitmap(bm);
        }
    }

    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path) == null && bm != null) {
            mLruCache.put(path, bm);
        }
    }

    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

}
