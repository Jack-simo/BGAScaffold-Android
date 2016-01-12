package cn.bingoogolapple.materialdrawer.activity;

import android.os.Bundle;
import android.view.View;

import cn.bingoogolapple.basenote.activity.ToolbarActivity;
import cn.bingoogolapple.materialdrawer.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/12 下午10:34
 * 描述:
 */
public class TestToolbarActivity extends ToolbarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_testtoolbar);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("测试ToolbarActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }
}