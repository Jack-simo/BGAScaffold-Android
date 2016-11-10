package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.Engine;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityChatBinding;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.KeyboardUtil;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:07
 * 描述:聊天界面
 */
public class ChatActivity extends MvcBindingActivity<ActivityChatBinding> implements EMMessageListener {
    private static final String EXTRA_TO_CHAT_USERNAME = "EXTRA_TO_CHAT_USERNAME";

    private String mToChatUsername;
    private ChatAdapter mChatAdapter;

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

    public static Intent newIntent(Context context, String toChatUsername) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_TO_CHAT_USERNAME, toChatUsername);
        return intent;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void setListener() {
        mBinding.titleBar.setDelegate(this);

        setOnClick(mBinding.ivChatSend, object -> {
            String msg = mBinding.etChatMsg.getText().toString().trim();
            if (StringUtil.isNotEmpty(msg)) {
                mBinding.etChatMsg.setText("");
                sendTextMessage(msg);
            }
        });

        mBinding.rvChatContent.setOnTouchListener((v, event) -> {
            KeyboardUtil.closeKeyboard(this);
            return false;
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mToChatUsername = getIntent().getStringExtra(EXTRA_TO_CHAT_USERNAME);
        mBinding.titleBar.setTitleText(mToChatUsername);

        mChatAdapter = new ChatAdapter(mToChatUsername);
        mBinding.rvChatContent.setAdapter(mChatAdapter);
        mChatAdapter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().addConnectionListener(mEMConnectionListener);
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().removeConnectionListener(mEMConnectionListener);
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    protected boolean isAutoCloseKeyboard() {
        return false;
    }

    @Override
    public void onClickRightCtv() {
        ToastUtil.show("打电话");
    }

    public void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.i("消息发送成功 " + content);
                mChatAdapter.refresh();
            }

            @Override
            public void onError(int code, String message) {
                Logger.e("消息发送失败 code:" + code + " message:" + message);
            }

            @Override
            public void onProgress(int progress, String status) {
                // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt 不回调
                Logger.i("消息发送中 progress:" + progress + " status:" + status);
            }
        });
        mChatAdapter.addMoreItem(message);
        mBinding.rvChatContent.smoothScrollToPosition(mChatAdapter.getItemCount());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        mChatAdapter.refresh();
        Logger.i("收到消息 messages:" + Engine.toJsonString(messages));
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        Logger.i("收到透传消息 messages:" + Engine.toJsonString(messages));
    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> messages) {
        Logger.i("收到已读回执 messages:" + Engine.toJsonString(messages));
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        Logger.i("收到已送达回执 message:" + Engine.toJsonString(message));
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        Logger.i("消息状态变动 message:" + Engine.toJsonString(message) + " change:" + Engine.toJsonString(change));
    }
}