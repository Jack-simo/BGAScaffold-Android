package cn.bingoogolapple.photopicker.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.StorageUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.model.FolderModel;
import cn.bingoogolapple.photopicker.ui.pw.DirPW;
import cn.bingoogolapple.photopicker.util.ImageLoader;
import cn.bingoogolapple.photopicker.util.ImgFilenameFilter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends TitlebarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    public static final int DATA_LOADED = 1;
    private GridView mContentGv;
    private TextView mDirnameTv;
    private TextView mDircountTv;

    private RelativeLayout mDirRl;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderModel> mFolderModels = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    private ImgAdapter mImgAdapter;

    private DirPW mDirPW;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOADED) {
                mProgressDialog.dismiss();
                dataToView();
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mContentGv = getViewById(R.id.gv_main_content);
        mDirnameTv = getViewById(R.id.tv_main_dirname);
        mDircountTv = getViewById(R.id.tv_main_dircount);

        mDirRl = getViewById(R.id.rl_main_dir);
    }

    @Override
    protected void setListener() {
        mImgAdapter = new ImgAdapter(this);
        mImgAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup viewGroup, View view, int position) {
                mImgAdapter.handleSelect(position);
            }
        });

        mDirRl.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle("图片选择器");
        mContentGv.setAdapter(mImgAdapter);


        initDatasWrapper();
    }

    /**
     * 利用ContentProvider扫描手机中的所有图片
     */
    private void initDatas() {
        if (StorageUtil.isExternalStorageWritable()) {
            mProgressDialog = ProgressDialog.show(this, null, "正在加载");
            new Thread() {
                @Override
                public void run() {
                    Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver cr = MainActivity.this.getContentResolver();
                    Cursor cursor = cr.query(imgUri, null, MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?", new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                    Set<String> mDirPaths = new HashSet<>();
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null) {
                            continue;
                        }

                        String dirPath = parentFile.getAbsolutePath();
                        FolderModel folderModel = null;

                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            folderModel = new FolderModel();
                            folderModel.setDirPath(dirPath);
                            folderModel.setFirstImgPath(path);
                        }

                        if (parentFile.list() == null) {
                            continue;
                        } else {
                            int picSize = parentFile.list(new ImgFilenameFilter()).length;
                            folderModel.setCount(picSize);
                            mFolderModels.add(folderModel);

                            if (picSize > mMaxCount) {
                                mMaxCount = picSize;
                                mCurrentDir = parentFile;
                            }
                        }
                    }

                    cursor.close();

                    mHandler.sendEmptyMessage(DATA_LOADED);
                }
            }.start();
        } else {
            ToastUtil.show("当前存储卡不可用");
        }
    }

    private void dataToView() {
        if (mCurrentDir == null) {
            ToastUtil.show("未扫描到任何图片");
        } else {
            mImgAdapter.setDirPath(mCurrentDir.getAbsolutePath());
            mImgAdapter.setData(Arrays.asList(mCurrentDir.list(new ImgFilenameFilter())));

            mDirnameTv.setText(mCurrentDir.getName());
            mDircountTv.setText(String.valueOf(mMaxCount));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        finish();
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    private void initDatasWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            initDatas();
        } else {
            EasyPermissions.requestPermissions(this, "需要访问SD卡的权限!", REQUEST_CODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_main_dir) {
            showDirPw();
        }
    }

    private void showDirPw() {
        if (mDirPW == null) {
            mDirPW = new DirPW(this, mFolderModels);
            mDirPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lightOn();
                }
            });
            mDirPW.setDelegate(new DirPW.Delegate() {
                @Override
                public void onSelected(FolderModel folderModel) {
                    mCurrentDir = new File(folderModel.getDirPath());
                    mMaxCount = folderModel.getCount();
                    dataToView();
                }
            });
        }
        mDirPW.showAsDropDown(mDirRl);
        lightOff();
    }

    /**
     * 内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    /**
     * 内容区域变量
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    private static final class ImgAdapter extends BGAAdapterViewAdapter<String> {
        private String mDirPath;
        private Set<String> mSelectedImg = new HashSet<>();
        private int mItemMaxWidth;

        public ImgAdapter(Context context) {
            super(context, R.layout.item_img);
            mItemMaxWidth = context.getResources().getDisplayMetrics().widthPixels / 3;
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper helper) {
            helper.setItemChildClickListener(R.id.iv_item_img_icon);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String path) {
            ImageView iconIv = helper.getImageView(R.id.iv_item_img_icon);
            ImageButton selectIb = helper.getView(R.id.ib_item_img_select);

            // 第一次进来时ImageLoader获取不到图片宽高，设置最大宽度进行缩放，节省内存
            iconIv.setMaxWidth(mItemMaxWidth);

            iconIv.setImageResource(R.mipmap.pictures_no);
            ImageLoader.getInstance(3, ImageLoader.ScheduleType.LIFO).loadImage(mDirPath + path, iconIv);

            if (mSelectedImg.contains(mDirPath + path)) {
                selectIb.setImageResource(R.mipmap.pictures_selected);
                iconIv.setColorFilter(Color.parseColor("#77000000"));
            } else {
                selectIb.setImageResource(R.mipmap.picture_unselected);
                iconIv.setColorFilter(null);
            }
        }

        public void setDirPath(String dirPath) {
            mDirPath = dirPath + "/";
        }

        public void handleSelect(int position) {
            String filePath = mDirPath + mData.get(position);
            if (mSelectedImg.contains(filePath)) {
                mSelectedImg.remove(filePath);
            } else {
                mSelectedImg.add(filePath);
            }
            notifyDataSetChanged();
        }
    }

}