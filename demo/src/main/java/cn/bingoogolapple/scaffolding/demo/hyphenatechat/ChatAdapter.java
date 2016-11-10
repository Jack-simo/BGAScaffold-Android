package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.support.annotation.LayoutRes;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.scaffolding.adapter.BGABindingViewHolder;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ItemChatMeTextBinding;
import cn.bingoogolapple.scaffolding.demo.databinding.ItemChatOtherTextBinding;
import cn.bingoogolapple.scaffolding.util.StringUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:57
 * 描述:
 */
public class ChatAdapter extends BGABindingRecyclerViewAdapter {
    private static final int ITEM_TYPE_ME = 1;
    private static final int ITEM_TYPE_OTHER = 2;

    private List<EMMessage> mData;
    private String mToChatUsername;
    private EMConversation mEMConversation;

    public ChatAdapter(String toChatUsername) {
        mData = new ArrayList<>();
        mToChatUsername = toChatUsername;
        mEMConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, null, true);
        mEMConversation.markAllMessagesAsRead();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    protected
    @LayoutRes
    int getItemRootLayoutResID(int viewType) {
        if (viewType == ITEM_TYPE_OTHER) {
            return R.layout.item_chat_other_text;
        } else {
            return R.layout.item_chat_me_text;
        }
    }

    @Override
    public void onBindViewHolder(BGABindingViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_OTHER) {
            refreshOther((ItemChatOtherTextBinding) holder.getBinding(), mData.get(position));
        } else {
            refreshMe((ItemChatMeTextBinding) holder.getBinding(), mData.get(position));
        }
    }

    private void refreshOther(ItemChatOtherTextBinding binding, EMMessage emMessage) {
        EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
        binding.tvItemChatTextMsg.setText(messageBody.getMessage());
    }

    private void refreshMe(ItemChatMeTextBinding binding, EMMessage emMessage) {
        EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
        binding.tvItemChatTextMsg.setText(messageBody.getMessage());
    }

    @Override
    public int getItemViewType(int position) {
        if (StringUtil.isEqual(mData.get(position).getTo(), mToChatUsername)) {
            return ITEM_TYPE_ME;
        } else {
            return ITEM_TYPE_OTHER;
        }
    }

    public void setData(List<EMMessage> data) {
        if (data != null) {
            mData = data;
        } else {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public void refresh() {
        setData(mEMConversation.getAllMessages());
    }

    public void addMoreItem(EMMessage message) {
        mData.add(mData.size(), message);
        notifyItemInserted(mData.size() - 1);
    }
}