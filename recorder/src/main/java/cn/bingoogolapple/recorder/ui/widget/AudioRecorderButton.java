package cn.bingoogolapple.recorder.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.bingoogolapple.recorder.R;
import cn.bingoogolapple.recorder.ui.dialog.AudioRecorderDialog;
import cn.bingoogolapple.recorder.util.AudioRecorderManager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/20 上午1:01
 * 描述:
 */
public class AudioRecorderButton extends AppCompatButton implements AudioRecorderManager.Delegate {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    private static final int WHAT_AUDIO_PREPARED = 1;
    private static final int WHAT_VOICE_CHANGED = 2;
    private static final int WHAT_DIALOG_DISMISS = 3;


    private int mCurrentState = STATE_NORMAL;
    private boolean mIsRecording;

    private int mDistanceYCancel;

    private AudioRecorderDialog mRecorderDialog;
    private AudioRecorderManager mAudioRecorderManager;
    private float mTime;
    private Delegate mDelegate;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_AUDIO_PREPARED:
                    mIsRecording = true;
                    mRecorderDialog.changeToRecording();
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case WHAT_VOICE_CHANGED:
                    mRecorderDialog.updateVoiceLevel(mAudioRecorderManager.getVoiceLevel(7));
                    break;
                case WHAT_DIALOG_DISMISS:
                    mRecorderDialog.dismiss();
                    break;
            }
        }
    };

    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(WHAT_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDistanceYCancel = context.getResources().getDimensionPixelOffset(R.dimen.size_level10);
        mRecorderDialog = new AudioRecorderDialog(context);
        mAudioRecorderManager = AudioRecorderManager.getInstance();
        mAudioRecorderManager.setDelegate(this);
    }

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(WHAT_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAudioRecorderManager.prepareAudio();
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
                if (!mIsRecording || mTime < 0.5f) {
                    // prepare未完成
                    mRecorderDialog.changeToShort();
                    mAudioRecorderManager.cancel();
                    mHandler.sendEmptyMessageDelayed(WHAT_DIALOG_DISMISS, 1300);
                } else if (mCurrentState == STATE_RECORDING) {
                    mRecorderDialog.dismiss();
                    mAudioRecorderManager.release();
                    if (mDelegate != null) {
                        mDelegate.onAudioRecorderFinish(mTime, mAudioRecorderManager.getCurrenFilePath());
                    }
                } else if (mCurrentState == STATE_WANT_CANCEL) {
                    mRecorderDialog.dismiss();
                    mAudioRecorderManager.cancel();
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
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public interface Delegate {
        void onAudioRecorderFinish(float time, String filePath);
    }
}