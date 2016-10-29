package cn.bingoogolapple.bottomnavigation.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.math.BigDecimal;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.widget.BGATimerTextView;
import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/9/6 下午6:07
 * 描述:
 */
public class CountDownActivity extends TitlebarActivity {
    private TextView mSendVcodeTv;
    private BGATimerTextView mTimerTextView;
    private CountDownTimer mCountDownTimer;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_count_down);
        mSendVcodeTv = getViewById(R.id.send_vcode);
        mTimerTextView = getViewById(R.id.send_vcode_custom);
    }

    @Override
    protected void setListener() {
        setOnClick(mSendVcodeTv, object -> {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            if (mCountDownTimer == null) {
                mCountDownTimer = new CountDownTimer(60000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        mSendVcodeTv.setEnabled(true);
                        mSendVcodeTv.setText(millisUntilFinished / 1000 + "秒后可重新发送");
                        Logger.i(String.valueOf(millisUntilFinished));
                        Logger.i(String.valueOf(millisUntilFinished / 1000));
                        Logger.i(String.valueOf(BigDecimal.valueOf(millisUntilFinished).divide(BigDecimal.valueOf(1000)).intValue()));
                        Logger.i(String.valueOf((int) Math.ceil(millisUntilFinished / 1000.0)));
                    }

                    @Override
                    public void onFinish() {
                        mSendVcodeTv.setEnabled(true);
                        mSendVcodeTv.setText("重新发送");
                    }
                };
            }

            mCountDownTimer.start();
        });
        setOnClick(mTimerTextView, object -> mTimerTextView.start());
        setOnClick(R.id.stop_send, object -> {
            if (mCountDownTimer != null) {
                mSendVcodeTv.setEnabled(true);
                mCountDownTimer.cancel();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }
}
