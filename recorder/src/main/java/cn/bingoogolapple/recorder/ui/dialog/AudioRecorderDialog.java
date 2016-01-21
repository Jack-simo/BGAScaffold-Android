package cn.bingoogolapple.recorder.ui.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bingoogolapple.recorder.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/21 下午11:09
 * 描述:
 */
public class AudioRecorderDialog extends AppCompatDialog {
    private ImageView mIconIv;
    private ImageView mVoiceIv;
    private TextView mTipTv;

    public AudioRecorderDialog(Context context) {
        super(context, R.style.ThemeAudioRecorderDialog);
        setContentView(R.layout.dialog_audio_recorder);
        mIconIv = getViewById(R.id.iv_audio_recorder_icon);
        mVoiceIv = getViewById(R.id.iv_audio_recorder_voice);
        mTipTv = getViewById(R.id.tv_audio_recorder_tip);
    }

    public void changeToRecording() {
        mVoiceIv.setVisibility(View.VISIBLE);

        mIconIv.setImageResource(R.mipmap.recorder);
        mVoiceIv.setImageResource(R.mipmap.v1);
        mTipTv.setText(R.string.dialog_status_up_cancel);
        show();
    }

    public void changeToWantCancel() {
        mVoiceIv.setVisibility(View.VISIBLE);

        mIconIv.setImageResource(R.mipmap.recorder);
        mVoiceIv.setImageResource(R.mipmap.v1);
        mTipTv.setText(R.string.dialog_status_release_cancel);
    }

    public void changeToShort() {
        mVoiceIv.setVisibility(View.GONE);

        mIconIv.setImageResource(R.mipmap.voice_to_short);
        mTipTv.setText(R.string.dialog_status_to_short);
    }

    public void updateVoiceLevel(int level) {
        int resId = getContext().getResources().getIdentifier("v" + level, "mipmap", getContext().getPackageName());
        mVoiceIv.setImageResource(resId);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

}