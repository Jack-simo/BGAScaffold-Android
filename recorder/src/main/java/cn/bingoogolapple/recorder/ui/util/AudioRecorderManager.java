package cn.bingoogolapple.recorder.ui.util;

import android.media.MediaRecorder;

import java.io.File;
import java.util.UUID;

import cn.bingoogolapple.basenote.util.StorageUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/23 下午10:09
 * 描述:
 */
public class AudioRecorderManager {
    private MediaRecorder mMediaRecorder;
    private File mCurrentFile;
    private Delegate mDelegate;

    private static AudioRecorderManager sInstance;
    private boolean mIsPrepared;

    private AudioRecorderManager() {
    }

    public static AudioRecorderManager getInstance() {
        if (sInstance == null) {
            synchronized (AudioRecorderManager.class) {
                if (sInstance == null) {
                    sInstance = new AudioRecorderManager();
                }
            }
        }
        return sInstance;
    }

    public void prepareAudio() {
        try {
            mCurrentFile = new File(StorageUtil.getAudioDir(), generateFileName());
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(mCurrentFile.getAbsolutePath());
            // 设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频的格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置音频的编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            mIsPrepared = true;

            if (mDelegate != null) {
                mDelegate.wellPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (mIsPrepared) {
            try {
                //  mMediaRecorder.getMaxAmplitude()   [1,32767]
                // mMediaRecorder.getMaxAmplitude() / 32768  [0,1)
                // maxLevel * mMediaRecorder.getMaxAmplitude() / 32768  [0, maxLevel)
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;

                // 没有设置音频源之前获取声音振幅会报IllegalStateException，直接返回1
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {
        release();
        if (mCurrentFile != null) {
            mCurrentFile.delete();
            mCurrentFile = null;
        }
    }

    public String getCurrenFilePath() {
        return mCurrentFile.getAbsolutePath();
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public interface Delegate {
        void wellPrepared();
    }
}