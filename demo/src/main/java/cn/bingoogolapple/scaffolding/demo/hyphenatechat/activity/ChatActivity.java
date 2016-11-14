package cn.bingoogolapple.scaffolding.demo.hyphenatechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityChatBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.adapter.ChatAdapter;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.EmUtil;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.RxEmEvent;
import cn.bingoogolapple.scaffolding.util.KeyboardUtil;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:07
 * 描述:聊天界面
 */
public class ChatActivity extends MvcBindingActivity<ActivityChatBinding> {
    private static final String EXTRA_TO_CHAT_USERNAME = "EXTRA_TO_CHAT_USERNAME";
    private static final String EXTRA_TO_CHAT_NICKNAME = "EXTRA_TO_CHAT_NICKNAME";

    private String mToChatUsername;
    private ChatAdapter mChatAdapter;

    public static Intent newIntent(Context context, String toChatUsername, String toChatNickname) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_TO_CHAT_USERNAME, toChatUsername);
        intent.putExtra(EXTRA_TO_CHAT_NICKNAME, toChatNickname);
        return intent;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBinding.setToChatNickname(getIntent().getStringExtra(EXTRA_TO_CHAT_NICKNAME));

        mToChatUsername = getIntent().getStringExtra(EXTRA_TO_CHAT_USERNAME);
        mChatAdapter = new ChatAdapter(mBinding.rvChatContent, mToChatUsername);
        mBinding.rvChatContent.setAdapter(mChatAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mChatAdapter.refresh();

        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageReceivedEvent.class, this).subscribe(messageReceivedEvent -> {
            for (MessageModel messageModel : messageReceivedEvent.mMessageModelList) {
                if (StringUtil.isEqual(messageModel.from, mToChatUsername)) {
                    Logger.i("收到新的消息 msg:" + messageModel.msg);
                    mChatAdapter.addMoreItem(messageModel);
                } else {
                    Logger.i("收到其他人发来的消息 from:" + messageModel.from);
                }
            }
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageSendSuccessEvent.class, this).subscribe(messageSendSuccessEvent -> {
            // TODO 修改数据模型，增加消息发送状态的属性
            Logger.i("消息发送成功");
            mChatAdapter.refresh();
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageSendFailureEvent.class, this).subscribe(messageSendFailureEvent -> {
            Logger.i("消息发送失败");
            mChatAdapter.refresh();
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.EMConnectedEvent.class, this).subscribe(emConnectedEvent -> {
            Logger.i("连接聊天服务器成功");
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.EMDisconnectedEvent.class, this).subscribe(emDisconnectedEvent -> {
            Logger.i(emDisconnectedEvent.mErrorMsg);
        });
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
        EmUtil.sendMessage(message);
        mChatAdapter.addMoreItem(EmUtil.convertToMessageModel(message));
    }
}