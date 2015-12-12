package cn.bingoogolapple.basenote.util;

import android.os.AsyncTask;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/12 下午11:44
 * 描述:
 */
public abstract class MinTimeRequestTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public static final long DELAY_TIME = 700L;
    protected boolean mIsNeedSleep = true;

    @Override
    protected Result doInBackground(Params... paramses) {
        long beginTime = System.currentTimeMillis();
        Result result = request(paramses);
        long endTime = System.currentTimeMillis();
        long time = endTime - beginTime;
        if (mIsNeedSleep && time < DELAY_TIME) {
            try {
                Thread.sleep(DELAY_TIME - time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected abstract Result request(Params... paramses);
}