package cn.bingoogolapple.materialdrawer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import cn.bingoogolapple.basenote.activity.BaseActivity;
import cn.bingoogolapple.materialdrawer.R;
import cn.bingoogolapple.materialdrawer.fragment.Fragment11;
import cn.bingoogolapple.materialdrawer.fragment.Fragment12;
import cn.bingoogolapple.materialdrawer.fragment.Fragment13;
import cn.bingoogolapple.materialdrawer.fragment.Fragment21;
import cn.bingoogolapple.materialdrawer.fragment.Fragment22;

public class MainActivity extends BaseActivity implements Drawer.OnDrawerItemClickListener {
    private Drawer mDrawer;
    private TextView mHeaderDescTv;
    private Fragment mCurrentMenuFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initDrawer(savedInstanceState);

        mHeaderDescTv.setText("修改后的头部描述");
    }

    private void initDrawer(Bundle savedInstanceState) {
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.view_drawerheader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_11).withIcon(R.drawable.ic_launcher).withIdentifier(R.string.menu_11).withTag(Fragment11.class.getName()),
                        new PrimaryDrawerItem().withName(R.string.menu_12).withIcon(R.drawable.ic_launcher).withIdentifier(R.string.menu_12).withTag(Fragment12.class.getName()),
                        new PrimaryDrawerItem().withName(R.string.menu_13).withIcon(R.drawable.ic_launcher).withIdentifier(R.string.menu_13).withTag(Fragment13.class.getName()),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.menu_21).withIcon(R.drawable.ic_launcher).withIdentifier(R.string.menu_21).withTag(Fragment21.class.getName()),
                        new PrimaryDrawerItem().withName(R.string.menu_22).withIcon(R.drawable.ic_launcher).withIdentifier(R.string.menu_22).withTag(Fragment22.class.getName())
                )
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .build();
        mDrawer.setSelection(R.string.menu_11);
        mDrawer.keyboardSupportEnabled(this, true);
        mHeaderDescTv = (TextView) mDrawer.getHeader().findViewById(R.id.tv_drawerheader_desc);
    }

    public void changeNavIconToMenu(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        mDrawer.setToolbar(this, toolbar, true);

        mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void changeNavIconToBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        mDrawer.setToolbar(this, null);

        mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        String drawerItemTag = (String) drawerItem.getTag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment newFragment = fragmentManager.findFragmentByTag(drawerItemTag);
        if ((mCurrentMenuFragment == null && newFragment == null) || mCurrentMenuFragment != newFragment) {
            if (newFragment == null) {
                newFragment = Fragment.instantiate(this, drawerItemTag);
                fragmentTransaction.replace(R.id.fl_main_content, newFragment, drawerItemTag);
            }
            fragmentTransaction.show(newFragment);
            if (mCurrentMenuFragment != null) {
                fragmentTransaction.hide(mCurrentMenuFragment);
            }
            fragmentTransaction.commit();
            mCurrentMenuFragment = newFragment;
        }
        return false;
    }

    public void putBackFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_main_content, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            if (mDrawer.isDrawerOpen()) {
                mDrawer.closeDrawer();
            } else if (mCurrentMenuFragment != null && !(mCurrentMenuFragment instanceof Fragment11)) {
                mDrawer.setSelection(R.string.menu_11);
            } else {
                super.onBackPressed();
            }
        }
    }
}