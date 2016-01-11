package cn.bingoogolapple.materialdrawer.fragment;

import android.os.Bundle;

import cn.bingoogolapple.materialdrawer.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午1:39
 * 描述:
 */
public class Fragment22 extends BaseMainFragment {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_22);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mMainActivity.changeNavIconToMenu(mToolbar);
        setTitle(R.string.menu_22);
    }
}
