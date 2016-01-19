package cn.bingoogolapple.recorder.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import cn.bingoogolapple.recorder.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/20 上午1:01
 * 描述:
 */
public class AudioRecorderButton extends Button {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    private int mCurrentState = STATE_NORMAL;
    private boolean mIsRecording;

    private int mDistanceYCancel;

    private Delegate mDelegate;

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDistanceYCancel = context.getResources().getDimensionPixelOffset(R.dimen.size_level10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                mIsRecording = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsRecording) {
                    if (isWantCancel(x, y)) {
                        changeState(STATE_WANT_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentState == STATE_RECORDING) {

                } else if (mCurrentState == STATE_WANT_CANCEL) {

                }
                reset();
                break;
        }
        return true;
    }

    private void changeState(int status) {
        if (mCurrentState != status) {
            mCurrentState = status;
            switch (mCurrentState) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.shape_btn_recorder_normal);
                    setText(R.string.arb_status_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.shape_btn_recorder_recording);
                    setText(R.string.arb_status_recording);

                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.shape_btn_recorder_recording);
                    setText(R.string.arb_status_want_cancel);
                    break;
            }
        }
    }

    private boolean isWantCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        } else if (y < -mDistanceYCancel || y > getHeight() + mDistanceYCancel) {
            return true;
        }
        return false;
    }

    private void reset() {
        mIsRecording = false;
        changeState(STATE_NORMAL);
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public interface Delegate {

    }
}