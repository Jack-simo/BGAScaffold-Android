package cn.bingoogolapple.recorder.ui.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.UIUtil;
import cn.bingoogolapple.recorder.R;
import cn.bingoogolapple.recorder.model.Recorder;
import cn.bingoogolapple.recorder.ui.widget.AudioRecorderButton;
import cn.bingoogolapple.recorder.util.MediaManager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/20 上午12:37
 * 描述:
 */
public class MainActivity extends TitlebarActivity implements EasyPermissions.PermissionCallbacks, AudioRecorderButton.Delegate, AdapterView.OnItemClickListener {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private ListView mContentLv;
    private AudioRecorderButton mRecorderBtn;

    private RecorderAdapter mRecorderAdapter;

    private View mCurrentAnimView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mContentLv = getViewById(R.id.lv_main_content);
        mRecorderBtn = getViewById(R.id.arb_main_recorder);
    }

    @Override
    protected void setListener() {
        mContentLv.setOnItemClickListener(this);
        mRecorderBtn.setDelegate(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle(R.string.app_name);

        mRecorderAdapter = new RecorderAdapter(this);
        mContentLv.setAdapter(mRecorderAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finish();
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_tip), REQUEST_CODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onAudioRecorderFinish(float time, String filePath) {
        mRecorderAdapter.addLastItem(new Recorder(time, filePath));
        mContentLv.setSelection(mRecorderAdapter.getCount() - 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCurrentAnimView != null) {
            mCurrentAnimView.setBackgroundResource(R.mipmap.adj);
            mCurrentAnimView = null;
        }

        mCurrentAnimView = view.findViewById(R.id.v_recorder_anim);
        mCurrentAnimView.setBackgroundResource(R.drawable.anim_play);
        ((AnimationDrawable) mCurrentAnimView.getBackground()).start();

        MediaManager.playSound(mRecorderAdapter.getItem(position).filePath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCurrentAnimView.setBackgroundResource(R.mipmap.adj);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        MediaManager.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MediaManager.release();
        super.onDestroy();
    }

    private static final class RecorderAdapter extends BGAAdapterViewAdapter<Recorder> {
        private int mMinItemWidth;
        private int mMaxItemWidth;

        public RecorderAdapter(Context context) {
            super(context, R.layout.item_recorder);
            int screenWidth = UIUtil.getScreenWidth();
            mMaxItemWidth = (int) (screenWidth * 0.7f);
            mMinItemWidth = (int) (screenWidth * 0.15f);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Recorder recorder) {
            helper.setText(R.id.tv_recorder_time, Math.round(recorder.time) + "\"");
            ViewGroup.LayoutParams lp = helper.getView(R.id.fl_recorder_anim).getLayoutParams();
            lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * recorder.time));
        }
    }
}
