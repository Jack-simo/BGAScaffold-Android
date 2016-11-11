package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.EMConversationListener;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityConversationBinding;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;
import cn.bingoogolapple.scaffolding.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:04
 * 描述:会话列表界面
 */
public class ConversationActivity extends MvcBindingActivity<ActivityConversationBinding> implements EMConversationListener, ConversationAdapter.Delegate {
    private ConversationAdapter mConversationAdapter;

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
        EMClient.getInstance().chatManager().addConversationListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeConversationListener(this);
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
        mConversationAdapter.refresh();
    }
}
