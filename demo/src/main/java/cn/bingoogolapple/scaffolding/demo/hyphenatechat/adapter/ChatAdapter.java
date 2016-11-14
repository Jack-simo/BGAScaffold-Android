package cn.bingoogolapple.scaffolding.demo.hyphenatechat.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGABindingRecyclerViewAdapter;
import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.EmUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:57
 * 描述:
 */
public class ChatAdapter extends BGABindingRecyclerViewAdapter<MessageModel, ViewDataBinding> {
    private RecyclerView mRecyclerView;
    private String mToChatUsername;
    private EMConversation mConversation;

    public ChatAdapter(RecyclerView recyclerView, String toChatUsername) {
        mRecyclerView = recyclerView;
        mToChatUsername = toChatUsername;
        mConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, null, true);
        mConversation.markAllMessagesAsRead();
    }

    @Override
    protected void bindSpecialModel(ViewDataBinding binding, int position, MessageModel model) {
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isSendByMe) {
            // TODO 处理非文本消息
            return R.layout.item_chat_me_text;
        } else {
            // TODO 处理非文本消息
            return R.layout.item_chat_other_text;
        }
    }

    @Override
    public void setData(List<MessageModel> data) {
        super.setData(data);
        smoothScrollToBottom();
    }

    public void refresh() {
        setData(EmUtil.loadMessageList(mConversation));
    }

    public void addMoreItem(MessageModel message) {
        if (!message.isSendByMe) {
            mConversation.markMessageAsRead(message.msgId);
        }
        super.addLastItem(message);
        smoothScrollToBottom();
    }

    public void smoothScrollToBottom() {
        mRecyclerView.smoothScrollToPosition(getItemCount());
    }
}