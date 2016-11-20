package cn.bingoogolapple.scaffolding.demo.hyphenatechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.scaffolding.demo.MainActivity;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityChatBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.adapter.ChatAdapter;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ChatUserModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.EmUtil;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.RxEmEvent;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.KeyboardUtil;
import cn.bingoogolapple.scaffolding.util.LocalSubscriber;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;
import rx.Observable;
import rx.functions.Func1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:07
 * 描述:聊天界面
 */
public class ChatActivity extends MvcBindingActivity<ActivityChatBinding> implements BGABindingRecyclerViewAdapter.ItemEventHandler<MessageModel>, BGARefreshLayout.BGARefreshLayoutDelegate {
    private static final String EXTRA_CHAT_USER = "EXTRA_CHAT_USER";

    private EMConversation mConversation;
    private ChatUserModel mChatUserModel;
    private ChatAdapter mChatAdapter;

    public static Intent newIntent(Context context, ChatUserModel chatUserModel) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CHAT_USER, chatUserModel);
        return intent;
    }

    @Override
    protected void setListener() {
        super.setListener();

        mBinding.etChatMsg.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSendTextMessage();
            }
            return true;
        });
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        // 初始化下拉刷新控件
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, false);
        refreshViewHolder.setReleaseRefreshText("松开加载历史消息");
        refreshViewHolder.setPullDownRefreshText("下拉加载历史消息");
        mBinding.refreshChatContent.setRefreshViewHolder(refreshViewHolder);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mChatUserModel = getIntent().getParcelableExtra(EXTRA_CHAT_USER);
        mBinding.setNickname(mChatUserModel.nickName);

        mConversation = EMClient.getInstance().chatManager().getConversation(mChatUserModel.chatUserName, null, true);
        mChatAdapter = new ChatAdapter(mBinding.rvChatContent, mConversation);
        mChatAdapter.setItemEventHandler(this);
        mBinding.rvChatContent.setAdapter(mChatAdapter);
    }

    @Override
    public void onBackPressed() {
        if (AppManager.getInstance().getActivityStackSize() == 1) {
            backwardAndFinish(MainActivity.class);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageReceivedEvent.class, this)
                .flatMap(new Func1<RxEmEvent.MessageReceivedEvent, Observable<MessageModel>>() {
                    @Override
                    public Observable<MessageModel> call(RxEmEvent.MessageReceivedEvent messageReceivedEvent) {
                        return Observable.from(messageReceivedEvent.mMessageModelList);
                    }
                })
                .filter(messageModel -> StringUtil.isEqual(messageModel.from, mChatUserModel.chatUserName))
                .subscribe(messageModel -> {
                    Logger.i("收到新的消息 msg:" + messageModel.msg);
                    mChatAdapter.addLastItem(messageModel);
                });

        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageReadAckReceivedEvent.class, this)
                .flatMap(new Func1<RxEmEvent.MessageReadAckReceivedEvent, Observable<MessageModel>>() {
                    @Override
                    public Observable<MessageModel> call(RxEmEvent.MessageReadAckReceivedEvent messageReadAckReceivedEvent) {
                        return Observable.from(messageReadAckReceivedEvent.mMessageModelList);
                    }
                })
                .filter(messageModel -> StringUtil.isEqual(messageModel.from, EMClient.getInstance().getCurrentUser()) && StringUtil.isEqual(messageModel.to, mChatUserModel.chatUserName))
                .subscribe(messageModel -> {
                    Logger.i("收到已读回执");
                    mChatAdapter.loadMessageListFromMemory();
                });

        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageSendSuccessEvent.class, this).subscribe(messageSendSuccessEvent -> {
            Logger.i("消息发送成功");
            mChatAdapter.loadMessageListFromMemory();
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.MessageSendFailureEvent.class, this).subscribe(messageSendFailureEvent -> {
            Logger.i("消息发送失败");
            mChatAdapter.loadMessageListFromMemory();
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.EMConnectedEvent.class, this).subscribe(emConnectedEvent -> {
            Logger.i("连接聊天服务器成功");
            mBinding.setConnected(true);
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.EMDisconnectedEvent.class, this).subscribe(emDisconnectedEvent -> {
            Logger.i(emDisconnectedEvent.mErrorMsg);
            mBinding.setConnected(false);
        });

        mBinding.setConnected(EMClient.getInstance().isConnected());

        mChatAdapter.loadMessageListFromMemory();
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
     * 处理发送文本消息
     */
    public void handleSendTextMessage() {
        String msg = mBinding.etChatMsg.getText().toString().trim();
        if (StringUtil.isEmpty(msg)) {
            return;
        }

        mBinding.etChatMsg.setText("");

        mChatAdapter.addLastItem(EmUtil.sendMessage(EmUtil.createTextMessage(msg, mChatUserModel.chatUserName)));
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (mChatAdapter.getItemCount() > 0) {
            MessageModel firstMessageModel = mChatAdapter.getItem(0);

            EmUtil.loadHistoryMessageListFromDb(mConversation, firstMessageModel.msgId, 20)
                    .compose(RxUtil.applySchedulersBindToLifecycle(this))
                    .subscribe(new LocalSubscriber<List<MessageModel>>() {
                        @Override
                        public void onNext(List<MessageModel> messageModelList) {
                            refreshLayout.endRefreshing();
                            mChatAdapter.addNewData(messageModelList);
                        }

                        @Override
                        public void onError(String msg) {
                            refreshLayout.endRefreshing();
                        }
                    });
        } else {
            refreshLayout.endRefreshing();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void onItemClick(View v, int position, MessageModel model) {
        new MaterialDialog.Builder(this)
                .title("重发该消息？")
                .positiveText("重发")
                .onPositive((dialog, which) -> {
                    model.sendStatus = MessageModel.SEND_STATUS_INPROGRESS;
                    mChatAdapter.moveItem(position, mChatAdapter.getItemCount() - 1);

                    mChatAdapter.notifyItemRangeChanged(position, mChatAdapter.getItemCount() - position);

                    EmUtil.resendMessage(model.msgId);
                })
                .negativeText("取消")
                .show();
    }
}