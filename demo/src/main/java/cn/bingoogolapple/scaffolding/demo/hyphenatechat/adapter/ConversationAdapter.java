package cn.bingoogolapple.scaffolding.demo.hyphenatechat.adapter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ItemConversationBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ConversationModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.EmUtil;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:29
 * 描述:
 */
public class ConversationAdapter extends BGABindingRecyclerViewAdapter<ConversationModel, ItemConversationBinding> implements BGABindingRecyclerViewAdapter.ItemEventHandler<ConversationModel> {
    private Delegate mDelegate;

    /**
     * 当前处于打开状态的item
     */
    private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();

    public ConversationAdapter(Delegate delegate) {
        mDelegate = delegate;
        setItemEventHandler(this);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_conversation;
    }

    @Override
    protected void bindSpecialModel(ItemConversationBinding binding, int position, ConversationModel model) {
        if (model.unreadMsgCount > 0) {
            binding.brlItemConversationBadge.showTextBadge(String.valueOf(model.unreadMsgCount));
        } else {
            binding.brlItemConversationBadge.hiddenBadge();
        }
        binding.brlItemConversationBadge.setDragDismissDelegage(badgeable -> {
            badgeable.hiddenBadge();
            model.unreadMsgCount = 0;
            EmUtil.markConversationAllMessagesAsRead(model.username);
        });
        binding.silItemConversationRoot.setDelegate(new BGASwipeItemLayout.BGASwipeItemLayoutDelegate() {
            @Override
            public void onBGASwipeItemLayoutOpened(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
                mOpenedSil.add(swipeItemLayout);
            }

            @Override
            public void onBGASwipeItemLayoutClosed(BGASwipeItemLayout swipeItemLayout) {
                mOpenedSil.remove(swipeItemLayout);
            }

            @Override
            public void onBGASwipeItemLayoutStartOpen(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
            }
        });
    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (BGASwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    @Override
    public void setData(List<ConversationModel> data) {
        if (getItemCount() != data.size()) {
            closeOpenedSwipeItemLayoutWithAnim();
        }
        super.setData(data);
    }

    @Override
    public void onItemClick(View view, int position, ConversationModel model) {
        if (view.getId() == R.id.tv_item_conversation_delete) {
            removeItem(position);
            closeOpenedSwipeItemLayoutWithAnim();

            EmUtil.deleteConversation(model.username);
        } else if (view.getId() == R.id.brl_item_conversation_badge) {
            if (mOpenedSil.size() > 0) {
                closeOpenedSwipeItemLayoutWithAnim();
            } else {
                mDelegate.goToChat(model.username, model.nickname);
            }
        }
    }

    public interface Delegate {
        void goToChat(String toChatUsername, String toChatNickname);
    }
}