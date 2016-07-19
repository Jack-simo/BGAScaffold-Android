package cn.bingoogolapple.basenote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.inject.Inject;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.basenote.component.Poetry;
import cn.bingoogolapple.basenote.component.PoetryQualifier;


/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午5:06
 * 描述:
 */
public class Dagger2ThreeActivity extends TitlebarActivity {
    private TextView mResultTv;

    @PoetryQualifier("A")
    @Inject
    Poetry mPoetry;

    @PoetryQualifier("B")
    @Inject
    Poetry mPoetryB;

    @Inject
    Gson mGson;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dagger2_three);
        mResultTv = getViewById(R.id.tv_dagger2_three_result);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mApp.getAComponent().inject(this);
    }

    public void perform(View v) {
        String text = mPoetry.getPoem() + ",mPoetryA:" + mPoetry +
                mPoetryB.getPoem() + ",mPoetryB:" + mPoetryB +
                (mGson == null ? "Gson没被注入" : "Gson已经被注入");
        mResultTv.setText(mGson.toJson(text));
    }

}