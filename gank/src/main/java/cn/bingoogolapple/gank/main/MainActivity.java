package cn.bingoogolapple.gank.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.view.ToolbarActivity;
import cn.bingoogolapple.gank.R;
import cn.bingoogolapple.gank.web.WebActivity;

public class MainActivity extends ToolbarActivity<MainPresenter> implements MainPresenter.View {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenBackArrow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_girls) {
            ToastUtil.show("妹子");
        } else if (item.getItemId() == R.id.action_github_hot) {
            forward(WebActivity.newIntent(this, "https://github.com/trending", "今日Github热点"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}