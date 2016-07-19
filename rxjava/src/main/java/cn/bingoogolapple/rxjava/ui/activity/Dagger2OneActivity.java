package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.inject.Inject;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.component.MainComponent;
import cn.bingoogolapple.basenote.component.Poetry;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午5:06
 * 描述:
 */
public class Dagger2OneActivity extends TitlebarActivity {
    private TextView mResultTv;

    //添加@Inject注解，表示这个mPoetry是需要注入的
    @Inject
    Poetry mPoetry;
    @Inject
    Gson mGson;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dagger2_one);
        mResultTv = getViewById(R.id.tv_dagger2_one_result);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        MainComponent.getInstance().inject(this);
    }

    public void perform(View v) {
        String json = mGson.toJson(mPoetry);
        String text = json + ",mPoetry:" + mPoetry;
        mResultTv.setText(mGson.toJson(text));
    }

    public void dagger2Two(View v) {
        forward(Dagger2TwoActivity.class);
    }
}