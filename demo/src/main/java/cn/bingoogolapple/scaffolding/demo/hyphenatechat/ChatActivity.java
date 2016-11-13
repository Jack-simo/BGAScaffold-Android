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
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityChatBinding;
import cn.bingoogolapple.scaffolding.util.KeyboardUtil;
import cn.bingoogolapple.scaffolding.util.NetUtil;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:07
 * 描述:聊天界面
 */
public class ChatActivity extends MvcBindingActivity<ActivityChatBinding> implements EMMessageListener, EMConnectionListener {
    private static final String EXTRA_TO_CHAT_USERNAME = "EXTRA_TO_CHAT_USERNAME";

    private String mToChatUsername;
    private ChatAdapter mChatAdapter;

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
    protected void processLogic(Bundle savedInstanceState) {
        mToChatUsername = getIntent().getStringExtra(EXTRA_TO_CHAT_USERNAME);

        mBinding.setEventHandler(this);
        mBinding.setToChatUsername(mToChatUsername);

        mChatAdapter = new ChatAdapter(mBinding.rvChatContent, mToChatUsername);
        mBinding.rvChatContent.setAdapter(mChatAdapter);
        mChatAdapter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().addConnectionListener(this);
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().removeConnectionListener(this);
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    protected boolean isAutoCloseKeyboard() {
        return false;
    }

    /**
     * 关闭键盘
     */
    public boolean closeKeyboard() {
        KeyboardUtil.closeKeyboard(this);
        return false;
    }

    /**
     * 滚动到列表底部
     */
    public void smoothScrollToBottom() {
        RxUtil.runInUIThreadDelay(300).subscribe(aVoid -> mChatAdapter.smoothScrollToBottom());
    }

    /**
     * 发送文本消息
     */
    public void sendTextMessage() {
        String msg = mBinding.etChatMsg.getText().toString().trim();
        if (StringUtil.isEmpty(msg)) {
            return;
        }

        mBinding.etChatMsg.setText("");

        EMMessage message = EMMessage.createTxtSendMessage(msg, mToChatUsername);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                RxUtil.runInUIThread(message).subscribe(emMessage -> {
                    Logger.i("消息发送成功 " + msg);

                    mChatAdapter.refresh();
                });
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
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        // 这里是在子线程的，循环遍历当前收到的消息。如果网络断开期间有新的消息，网络重连时也会走该方法
        for (EMMessage message : messages) {
            if (StringUtil.isEqual(message.getFrom(), mToChatUsername)) {
                RxUtil.runInUIThread(message).subscribe(emMessage -> {
                    mChatAdapter.addMoreItem(emMessage);
                });
            } else {
                Logger.i("收到其他人发来的消息");
            }
        }
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