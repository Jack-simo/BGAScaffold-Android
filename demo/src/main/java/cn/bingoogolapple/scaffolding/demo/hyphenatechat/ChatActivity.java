package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.Engine;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityChatBinding;
import cn.bingoogolapple.scaffolding.util.KeyboardUtil;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import cn.bingoogolapple.scaffolding.util.StringUtil;
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

        setOnClick(mBinding.ivChatSend, object -> sendTextMessage());

        mBinding.rvChatContent.setOnTouchListener((v, event) -> {
            KeyboardUtil.closeKeyboard(this);
            return false;
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mToChatUsername = getIntent().getStringExtra(EXTRA_TO_CHAT_USERNAME);
        mBinding.titleBar.setTitleText(mToChatUsername);

        mChatAdapter = new ChatAdapter(mBinding.rvChatContent, mToChatUsername);
        mBinding.rvChatContent.setAdapter(mChatAdapter);
        mChatAdapter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    protected boolean isAutoCloseKeyboard() {
        return false;
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
        Logger.i("收到消息 messages:" + Engine.toJsonString(messages));
        // 循环遍历当前收到的消息
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