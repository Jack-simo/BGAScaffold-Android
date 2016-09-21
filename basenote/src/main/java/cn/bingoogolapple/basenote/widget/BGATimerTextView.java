package cn.bingoogolapple.basenote.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.bingoogolapple.basenote.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/9/21 下午10:20
 * 描述:
 */
public class BGATimerTextView extends TextView implements Runnable {
    private static final int MAX_TIME = 60;
    private int mTime = MAX_TIME;
    private boolean mIsStart = false;

    public BGATimerTextView(Context context) {
        super(context);
    }

    public BGATimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BGATimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BGATimerTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void run() {
        if (mTime == 0) {
            setText("重新获取");
            mTime = MAX_TIME;
            mIsStart = false;
            setEnabled(true);
        } else {
            postDelayed(this, 1000);
            Logger.i(BGATimerTextView.class.getSimpleName(), String.valueOf(mTime));
            setText(mTime-- + "s");
            setEnabled(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this);
        mTime = MAX_TIME;
    }

    public synchronized void start() {
        if (!mIsStart) {
            mIsStart = true;
            mTime = MAX_TIME;
            post(this);
        }
    }
}
