package cn.bingoogolapple.materialdrawer.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.materialdrawer.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午1:39
 * 描述:
 */
public class FragmentBack2 extends BaseMainFragment {
    private TextView mTitleTv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_back2);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_back2, menu);
        MenuItem menuItem = menu.findItem(R.id.back2);
        View actionView = menuItem.getActionView();
        mTitleTv = (TextView) actionView.findViewById(R.id.tv_custom_toolbar_menu_title);
        mTitleTv.setText("改变前的标题");
        setOnClick(mTitleTv, object -> mTitleTv.setText("改变后的标题"));
        setOnClick(actionView.findViewById(R.id.iv_custom_toolbar_menu_more), object -> ToastUtil.show("点击了更多按钮"));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMainActivity.changeNavIconToBack(mToolbar);
    }
}
