package cn.bingoogolapple.materialdrawer.activity;

import android.os.Bundle;

import cn.bingoogolapple.basenote.activity.ToolbarActivity;
import cn.bingoogolapple.basenote.util.SkinUtil;
import cn.bingoogolapple.materialdrawer.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/12 下午10:34
 * 描述:
 */
public class TestToolbarActivity extends ToolbarActivity {

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_testtoolbar);
        setNoLinearContentView(R.layout.activity_testtoolbar);
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.toggleSkin, object -> SkinUtil.toggleSkin(this));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("测试ToolbarActivity");
    }
}