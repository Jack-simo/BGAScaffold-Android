package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityConversationBinding;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.SPUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;
import cn.bingoogolapple.scaffolding.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:04
 * 描述:会话列表界面
 */
public class ConversationActivity extends MvcBindingActivity<ActivityConversationBinding> implements EMConversationListener, ConversationAdapter.Delegate {
    private ConversationAdapter mConversationAdapter;

    /**
     * 连接状态监听器
     */
    private EMConnectionListener mEMConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {
            // 这里是在子线程的
            Logger.i("连接聊天服务器成功");
        }

        @Override
        public void onDisconnected(int error) {
            // 这里是在子线程的
            if (error == EMError.USER_REMOVED) {
                Logger.i("帐号已经被移除");
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                Logger.i("帐号在其他设备登录");
            } else {
                if (NetUtils.hasNetwork(AppManager.getApp())) {
                    Logger.i("连接不到聊天服务器");
                } else {
                    Logger.i("当前网络不可用，请检查网络设置");
                }
            }
        }
    };

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void setListener() {
        mBinding.titleBar.setDelegate(this);
        mBinding.rvConversationContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    mConversationAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBinding.rvConversationContent.addItemDecoration(Divider.newShapeDivider());

        mConversationAdapter = new ConversationAdapter(this);
        mBinding.rvConversationContent.setAdapter(mConversationAdapter);

        mConversationAdapter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().addConnectionListener(mEMConnectionListener);
        EMClient.getInstance().chatManager().addConversationListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().removeConnectionListener(mEMConnectionListener);
        EMClient.getInstance().chatManager().removeConversationListener(this);
    }

    @Override
    public void onClickRightCtv() {
        List<String> usernameList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            usernameList.add("test" + i);
        }
        usernameList.remove(SPUtil.getString("chatUsername"));
        new MaterialDialog.Builder(this)
                .title("请选择环信账号")
                .items(usernameList)
                .itemsCallback((dialog, itemView, position, text) -> {
                    goToChat(text.toString());
                })
                .cancelable(false)
                .show();
    }

    @Override
    public void goToChat(String toChatUsername) {
        forward(ChatActivity.newIntent(this, toChatUsername));
    }

    @Override
    public void onCoversationUpdate() {
        mConversationAdapter.refresh();
    }
}
