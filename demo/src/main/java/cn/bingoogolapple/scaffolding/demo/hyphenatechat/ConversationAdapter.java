package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.scaffolding.adapter.BGABindingViewHolder;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ItemConversationBinding;
import cn.bingoogolapple.scaffolding.util.CalendarUtil;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;
import cn.bingoogolapple.titlebar.BGAOnNoDoubleClickListener;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:29
 * 描述:
 */
public class ConversationAdapter extends BGABindingRecyclerViewAdapter {
    private List<EMConversation> mData;
    private Delegate mDelegate;

    /**
     * 当前处于打开状态的item
     */
    private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();

    public ConversationAdapter(Delegate delegate) {
        mData = new ArrayList<>();
        mDelegate = delegate;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_conversation;
    }

    @Override
    public void onBindViewHolder(BGABindingViewHolder holder, int position) {
        ItemConversationBinding binding = (ItemConversationBinding) holder.getBinding();

        EMConversation conversation = mData.get(position);
        EMTextMessageBody messageBody = (EMTextMessageBody) conversation.getLastMessage().getBody();
        binding.tvItemConversationDetail.setText(messageBody.getMessage());
        binding.tvItemConversationTitle.setText(conversation.getUserName());
        binding.tvItemConversationDate.setText(CalendarUtil.formatChineseMonthDay(conversation.getLastMessage().getMsgTime()));

        if (conversation.getUnreadMsgCount() > 0) {
            binding.brlItemConversationBadge.showTextBadge(String.valueOf(conversation.getUnreadMsgCount()));
        } else {
            binding.brlItemConversationBadge.hiddenBadge();
        }

        binding.brlItemConversationBadge.setDragDismissDelegage(badgeable -> {
            badgeable.hiddenBadge();
            conversation.markAllMessagesAsRead();
        });
        binding.brlItemConversationBadge.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (mOpenedSil.size() > 0) {
                    closeOpenedSwipeItemLayoutWithAnim();
                } else {
                    mDelegate.goToChat(mData.get(position).getUserName());
                }
            }
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
        binding.tvItemConversationDelete.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mData.remove(position);
                notifyItemRemoved(position);
                closeOpenedSwipeItemLayoutWithAnim();

                EMClient.getInstance().chatManager().deleteConversation(conversation.getUserName(), true);
            }
        });
    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (BGASwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    public void setData(List<EMConversation> data) {
        if (data != null) {
            mData = data;
        } else {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public void refresh() {
        setData(EmUtil.loadConversationList());
    }

    public interface Delegate {
        void goToChat(String toChatUsername);
    }
}