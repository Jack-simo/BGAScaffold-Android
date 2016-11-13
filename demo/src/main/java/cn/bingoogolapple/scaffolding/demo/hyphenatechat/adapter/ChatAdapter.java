package cn.bingoogolapple.scaffolding.demo.hyphenatechat.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ItemChatMeTextBinding;
import cn.bingoogolapple.scaffolding.demo.databinding.ItemChatOtherTextBinding;
import cn.bingoogolapple.scaffolding.util.StringUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:57
 * 描述:
 */
public class ChatAdapter extends BGABindingRecyclerViewAdapter<EMMessage, ViewDataBinding> {
    private RecyclerView mRecyclerView;
    private String mToChatUsername;
    private EMConversation mEMConversation;

    public ChatAdapter(RecyclerView recyclerView, String toChatUsername) {
        mRecyclerView = recyclerView;
        mToChatUsername = toChatUsername;
        mEMConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, null, true);
        mEMConversation.markAllMessagesAsRead();
    }

    @Override
    protected void bindSpecialModel(ViewDataBinding binding, int position, EMMessage model) {
        if (getItemViewType(position) == R.layout.item_chat_other_text) {
            refreshOther((ItemChatOtherTextBinding) binding, model);
        } else {
            refreshMe((ItemChatMeTextBinding) binding, model);
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
            return R.layout.item_chat_me_text;
        } else {
            return R.layout.item_chat_other_text;
        }
    }

    @Override
    public void setData(List<EMMessage> data) {
        super.setData(data);
        smoothScrollToBottom();
    }

    public void refresh() {
        setData(mEMConversation.getAllMessages());
    }

    public void addMoreItem(EMMessage message) {
        if (StringUtil.isEqual(message.getFrom(), mToChatUsername)) {
            mEMConversation.markMessageAsRead(message.getMsgId());
        }
        super.addLastItem(message);
        smoothScrollToBottom();
    }

    public void smoothScrollToBottom() {
        mRecyclerView.smoothScrollToPosition(getItemCount());
    }
}