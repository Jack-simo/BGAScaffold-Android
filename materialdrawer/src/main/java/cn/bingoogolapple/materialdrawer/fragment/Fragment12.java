package cn.bingoogolapple.materialdrawer.fragment;

import android.os.Bundle;

import cn.bingoogolapple.basenote.util.SkinUtil;
import cn.bingoogolapple.materialdrawer.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午1:39
 * 描述:
 */
public class Fragment12 extends BaseMainFragment {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_12);
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.changetoback2, object -> mMainActivity.putBackFragment(new FragmentBack2()));
        setOnClick(R.id.changeToGreen, object -> SkinUtil.changeToGreen(mActivity));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mMainActivity.changeNavIconToMenu(mToolbar);
        setTitle(R.string.menu_12);
    }
}
