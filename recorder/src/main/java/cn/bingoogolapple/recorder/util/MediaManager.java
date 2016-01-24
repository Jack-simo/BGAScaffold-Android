package cn.bingoogolapple.recorder.util;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/25 上午12:31
 * 描述:
 */
public class MediaManager {
    private static MediaPlayer sMediaPlayer;
    private static boolean sIsPause;

    public static void playSound(String path, MediaPlayer.OnCompletionListener onCompletionListener) {
        try {
            if (sMediaPlayer == null) {
                sMediaPlayer = new MediaPlayer();
                sMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        sMediaPlayer.reset();
                        return false;
                    }
                });
            } else {
                sMediaPlayer.reset();
            }
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            sMediaPlayer.setOnCompletionListener(onCompletionListener);
            sMediaPlayer.setDataSource(path);
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resume() {
        if (sMediaPlayer != null && sIsPause) {
            sMediaPlayer.start();
            sIsPause = false;
        }
    }

    public static void pause() {
        if (sMediaPlayer != null && sMediaPlayer.isPlaying()) {
            sMediaPlayer.pause();
            sIsPause = true;
        }
    }

    public static void release() {
        if (sMediaPlayer != null) {
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    }
}