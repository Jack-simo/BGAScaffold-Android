package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityConversationBinding;
import cn.bingoogolapple.scaffolding.util.NetUtil;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:04
 * 描述:会话列表界面
 */
public class ConversationActivity extends MvcBindingActivity<ActivityConversationBinding> implements EMConversationListener, EMMessageListener, EMConnectionListener, ConversationAdapter.Delegate {
    private ConversationAdapter mConversationAdapter;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBinding.setEventHandler(this);

        mConversationAdapter = new ConversationAdapter(this);
        mBinding.rvConversationContent.setAdapter(mConversationAdapter);

        mConversationAdapter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().addConnectionListener(this);
        EMClient.getInstance().chatManager().addConversationListener(this);
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().removeConnectionListener(this);
        EMClient.getInstance().chatManager().removeConversationListener(this);
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
            mConversationAdapter.closeOpenedSwipeItemLayoutWithAnim();
        }
    }

    @Override
    public void onClickRightCtv() {
        List<String> usernameList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            usernameList.add("test" + i);
        }
        usernameList.remove(EMClient.getInstance().getCurrentUser());
        new MaterialDialog.Builder(this)
                .title("请选择环信账号")
                .items(usernameList)
                .itemsCallback((dialog, itemView, position, text) -> {
                    goToChat(text.toString());
                })
                .show();
    }

    @Override
    public void goToChat(String toChatUsername) {
        forward(ChatActivity.newIntent(this, toChatUsername));
    }

    @Override
    public void onCoversationUpdate() {
        // 这里是在子线程的。如果网络断开期间有新的会话产生，网络重连时也会走该方法
        Logger.i("会话发生了改变");
        RxUtil.runInUIThread().subscribe(object -> {
            mConversationAdapter.refresh();
        });
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        // 这里是在子线程的，循环遍历当前收到的消息。如果网络断开期间有新的消息，网络重连时也会走该方法
        Logger.i("收到新的消息");
        RxUtil.runInUIThread().subscribe(object -> {
            mConversationAdapter.refresh();
        });
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        // 这里是在子线程的，收到透传消息
    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> messages) {
        // 这里是在子线程的，收到已读回执
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        // 这里是在子线程的，收到已送达回执
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        // 这里是在子线程的，消息状态变动
    }

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
            if (NetUtil.isNetworkAvailable()) {
                Logger.i("连接不到聊天服务器");
            } else {
                Logger.i("当前网络不可用，请检查网络设置");
            }
        }
    }
}
