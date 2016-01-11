package cn.bingoogolapple.materialdrawer.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.bingoogolapple.materialdrawer.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午1:39
 * 描述:
 */
public class FragmentBack1 extends BaseMainFragment {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_back1);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_changetoback2, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.changetoback2) {
            mMainActivity.putBackFragment(new FragmentBack2());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMainActivity.changeNavIconToBack(mToolbar);
        setTitle("Back1");
    }
}
