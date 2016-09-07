package cn.bingoogolapple.bottomnavigation.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/9/6 下午6:07
 * 描述:
 */
public class TestCountDownActivity extends TitlebarActivity {
    private TextView mSendVcodeTv;
    private CountDownTimer mCountDownTimer;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_count_down);
        mSendVcodeTv = getViewById(R.id.send_vcode);
    }

    @Override
    protected void setListener() {
        mSendVcodeTv.setOnClickListener(this);
        setOnClickListener(R.id.stop_send);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_vcode) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            if (mCountDownTimer == null) {
                mCountDownTimer = new CountDownTimer(60000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        mSendVcodeTv.setEnabled(true);
                        mSendVcodeTv.setText(millisUntilFinished / 1000 + "秒后可重新发送");
                        Logger.i(TAG, String.valueOf(millisUntilFinished));
                        Logger.i(TAG, String.valueOf(millisUntilFinished / 1000));
                        Logger.i(TAG, String.valueOf((int) Math.ceil(millisUntilFinished / 1000.0)));
                    }

                    @Override
                    public void onFinish() {
                        mSendVcodeTv.setEnabled(true);
                        mSendVcodeTv.setText("重新发送");
                    }
                };
            }

            mCountDownTimer.start();
        } else if (v.getId() == R.id.stop_send) {
            if (mCountDownTimer != null) {
                mSendVcodeTv.setEnabled(true);
                mCountDownTimer.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }
}
