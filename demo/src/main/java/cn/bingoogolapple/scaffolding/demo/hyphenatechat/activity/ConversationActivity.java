package cn.bingoogolapple.scaffolding.demo.hyphenatechat.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.chat.EMClient;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityConversationBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.adapter.ConversationAdapter;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.EmUtil;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.RxEmEvent;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:04
 * 描述:会话列表界面
 */
public class ConversationActivity extends MvcBindingActivity<ActivityConversationBinding> implements ConversationAdapter.Delegate {
    private ConversationAdapter mConversationAdapter;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mConversationAdapter = new ConversationAdapter(this);
        mBinding.rvConversationContent.setAdapter(mConversationAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RxBus.toObservableAndBindUntilStop(RxEmEvent.ConversationUpdateEvent.class, this).subscribe(conversationUpdateEvent -> {
            Logger.i("会话发生了改变");
            mConversationAdapter.setData(conversationUpdateEvent.mConversationModelList);
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.EMConnectedEvent.class, this).subscribe(emConnectedEvent -> {
            Logger.i("连接聊天服务器成功");
            // TODO 界面上展示出来
        });
        RxBus.toObservableAndBindUntilStop(RxEmEvent.EMDisconnectedEvent.class, this).subscribe(emDisconnectedEvent -> {
            //  TODO 界面上展示出来
            Logger.i(emDisconnectedEvent.mErrorMsg);
        });

        EmUtil.loadConversationList();
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
                    goToChat(text.toString(), text.toString());
                })
                .show();
    }

    @Override
    public void goToChat(String toChatUsername, String toChatNickname) {
        forward(ChatActivity.newIntent(this, toChatUsername, toChatNickname));
    }
}
