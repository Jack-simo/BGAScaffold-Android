package cn.bingoogolapple.recorder.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.bingoogolapple.recorder.R;
import cn.bingoogolapple.recorder.ui.dialog.AudioRecorderDialog;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/20 上午1:01
 * 描述:
 */
public class AudioRecorderButton extends AppCompatButton {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    private int mCurrentState = STATE_NORMAL;
    private boolean mIsRecording;

    private int mDistanceYCancel;

    private AudioRecorderDialog mRecorderDialog;

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDistanceYCancel = context.getResources().getDimensionPixelOffset(R.dimen.size_level10);
        mRecorderDialog = new AudioRecorderDialog(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsRecording = true;
                changeState(STATE_RECORDING);
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
                    mRecorderDialog.dismiss();
                } else if (mCurrentState == STATE_WANT_CANCEL) {
                    mRecorderDialog.dismiss();
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
                    if (mIsRecording) {
                        mRecorderDialog.changeToRecording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.shape_btn_recorder_recording);
                    setText(R.string.arb_status_want_cancel);
                    mRecorderDialog.changeToWantCancel();
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
}