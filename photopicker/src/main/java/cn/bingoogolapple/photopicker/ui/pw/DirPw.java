package cn.bingoogolapple.photopicker.ui.pw;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.model.FolderModel;
import cn.bingoogolapple.photopicker.util.ImageLoader;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/2/14 下午11:47
 * 描述:
 */
public class DirPW extends PopupWindow implements AdapterView.OnItemClickListener {
    private View mContentView;
    private ListView mContentLv;
    private DirAdapter mDirAdapter;
    private Delegate mDelegate;

    public DirPW(Context context, List<FolderModel> datas) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.pw_dir, null);
        setContentView(mContentView);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        setWidth(displayMetrics.widthPixels);
        setHeight((int) (displayMetrics.heightPixels * 0.7));

        setAnimationStyle(R.style.dir_pw_anim);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        mContentLv = (ListView) mContentView.findViewById(R.id.lv_dir_content);
        mContentLv.setOnItemClickListener(this);

        mDirAdapter = new DirAdapter(context, datas);
        mContentLv.setAdapter(mDirAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDelegate != null) {
            mDelegate.onSelected(mDirAdapter.getItem(position));
        }
        dismiss();
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    private static final class DirAdapter extends BGAAdapterViewAdapter<FolderModel> {

        public DirAdapter(Context context, List<FolderModel> datas) {
            super(context, R.layout.item_dir);
            mDatas = datas;
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, FolderModel folderModel) {
            ImageView iconIv = helper.getImageView(R.id.iv_item_dir_icon);
            iconIv.setImageResource(R.mipmap.pictures_no);
            ImageLoader.getInstance(3, ImageLoader.ScheduleType.LIFO).loadImage(folderModel.getFirstImgPath(), iconIv);

            helper.setText(R.id.tv_item_dir_name, folderModel.getName());
            helper.setText(R.id.tv_item_dir_count, String.valueOf(folderModel.getCount()));
        }
    }

    public interface Delegate {
        void onSelected(FolderModel folderModel);
    }
}
