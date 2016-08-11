package cn.bingoogolapple.photopicker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/2/13 下午4:30
 * 描述:
 */
public class ImageLoader {
    private static final int DEFAULT_THREAD_COUNT = 1;
    private static ImageLoader mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 队列的调度方式
     */
    private ScheduleType mScheduleType;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    /**
     * 避免还未初始化mPoolThreadHandler就开始使用
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphorePoolThread;

    public enum ScheduleType {
        FIFO, LIFO
    }

    private ImageLoader(int threadCount, ScheduleType scheduleType) {
        init(threadCount, scheduleType);
    }

    private void init(int threadCount, ScheduleType scheduleType) {
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // 线程池去取出一个任务进行执行
                        mThreadPool.execute(getTask());

                        try {
                            mSemaphorePoolThread.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // mPoolThreadHandler初始化完成后释放信号量，避免还未初始化就开始使用导致空指针
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取最大的可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<>();
        mScheduleType = scheduleType;
        mSemaphorePoolThread = new Semaphore(threadCount);
    }

    /**
     * 从任务队列取出一个任务
     *
     * @return
     */
    private Runnable getTask() {
        if (mScheduleType == ScheduleType.FIFO) {
            return mTaskQueue.removeFirst();
        } else {
            return mTaskQueue.removeLast();
        }
    }

    public static ImageLoader getInstance() {
        return getInstance(DEFAULT_THREAD_COUNT, ScheduleType.LIFO);
    }

    public static ImageLoader getInstance(int threadCount, ScheduleType scheduleType) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    threadCount = threadCount < 1 ? DEFAULT_THREAD_COUNT : threadCount;
                    scheduleType = scheduleType == null ? ScheduleType.LIFO : scheduleType;
                    mInstance = new ImageLoader(threadCount, scheduleType);
                }
            }
        }
        return mInstance;
    }

    public void loadImage(final String path, final ImageView imageView) {
        // 防止列表中item复用造成混乱，图片加载完后根据tag对比path判断
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 获取得到图片，为imageview回调设置图片
                    ImageBean holder = (ImageBean) msg.obj;
                    if (holder.mImageView.getTag().toString().equals(holder.mPath)) {
                        holder.mImageView.setImageBitmap(holder.mBitmap);
                    }
                }
            };
        }
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            refreshBitmap(path, imageView, bm);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    // 1.获得图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    // 2.压缩图片
                    Bitmap bm = decodeSampleBitmap(path, imageSize.mWidth, imageSize.mHeight);
                    // 3.把图片加入到缓存
                    addBitmapToLruCache(path, bm);

                    refreshBitmap(path, imageView, bm);

                    mSemaphorePoolThread.release();
                }
            });
        }
    }

    private void refreshBitmap(String path, ImageView imageView, Bitmap bitmap) {
        Message message = Message.obtain();
        message.obj = new ImageBean(bitmap, imageView, path);
        mUIHandler.sendMessage(message);
    }

    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path) == null && bm != null) {
            mLruCache.put(path, bm);
        }
    }

    /**
     * 根据图片显示的宽和高对图片进行压缩
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampleBitmap(String path, int width, int height) {
        // 获取图片的宽和高，但不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, width, height);

        // 使用获取到的inSampleSize值在此解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算inSampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
//    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        int inSampleSize = 1;
//        //图片的原始宽高
//        final int originWidth = options.outWidth;
//        final int originHeight = options.outHeight;
//
//        if (originWidth > reqWidth || originHeight > reqHeight) {
//            int widthRadio = Math.round(originWidth * 1.0f / reqWidth);
//            int heightRadio = Math.round(originHeight * 1.0f / reqHeight);
//            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高一定都会大于等于目标的宽和高
//            inSampleSize = Math.min(widthRadio, heightRadio);
//        }
//        return inSampleSize;
//    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算inSampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 默认值为1，不进行采样，原图加载
        int inSampleSize = 1;

        //图片的原始宽高
        final int originWidth = options.outWidth;
        final int originHeight = options.outHeight;

        // 当原始宽度或高度大于期望的宽度或高度时，计算采样率
        if (originWidth > reqWidth || originHeight > reqHeight) {
            final int halfOriginWidth = originWidth / 2;
            final int halfOriginHeight = originHeight / 2;

            // 缩放后的图片宽高不能小于期望的宽高，否则图片就会被拉伸，从而导致模糊。
            // 这个公式的理解方式：当 originWidth / (inSampleSize * 2) >= reqWidth 时，inSampleSize才乘以2
            while ((halfOriginWidth / inSampleSize) >= reqWidth && (halfOriginHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 根据ImageView获取适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        // 获取ImageView的实际高度
        int width = imageView.getWidth();
        if (width <= 0) {
            // 获取ImageView在Layout中声明的宽度
            width = imageView.getLayoutParams().width;
        }
        if (width <= 0) {
            // 检测最大值
//            width = imageView.getMaxWidth();
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            // 压缩到屏幕宽度
            width = imageView.getContext().getResources().getDisplayMetrics().widthPixels;
        }


        int height = imageView.getHeight();
        if (height <= 0) {
            // 获取ImageView在Layout中声明的高度
            height = imageView.getLayoutParams().height;
        }
        if (height <= 0) {
            // 检测最大值
//            height = imageView.getMaxHeight();
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }
        if (height <= 0) {
            // 压缩到屏幕高度
            height = imageView.getContext().getResources().getDisplayMetrics().heightPixels;
        }

        return new ImageSize(width, height);
    }

    /**
     * 运用反射的方式得到字段的值
     */
    private static int getImageViewFieldValue(ImageView imageView, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(imageView);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);

        try {
            if (mPoolThreadHandler == null) {
                // 避免还未初始化mPoolThreadHandler就开始使用
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    private class ImageBean {
        private Bitmap mBitmap;
        private ImageView mImageView;
        private String mPath;

        public ImageBean(Bitmap bitmap, ImageView imageView, String path) {
            mBitmap = bitmap;
            mImageView = imageView;
            mPath = path;
        }
    }

    private class ImageSize {
        private int mWidth;
        private int mHeight;

        private ImageSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }
    }
}