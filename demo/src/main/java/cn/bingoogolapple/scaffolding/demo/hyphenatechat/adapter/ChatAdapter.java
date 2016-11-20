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
import cn.bingoogolapple.scaffolding.util.StringUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:57
 * 描述:
 */
public class ChatAdapter extends BGABindingRecyclerViewAdapter<MessageModel, ViewDataBinding> {
    private RecyclerView mRecyclerView;
    private EMConversation mConversation;

    public ChatAdapter(RecyclerView recyclerView, EMConversation conversation) {
        mRecyclerView = recyclerView;
        mConversation = conversation;
        mConversation.markAllMessagesAsRead();
    }

    @Override
    protected void bindSpecialModel(ViewDataBinding binding, int position, MessageModel model) {
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel = getItem(position);
        if (messageModel.contentType == MessageModel.TYPE_CONTENT_TIME) {
            return R.layout.item_chat_time;
        } if (StringUtil.isEqual(EMClient.getInstance().getCurrentUser(), messageModel.from)) {
            // TODO 处理非文本消息
            return R.layout.item_chat_me_text;
        } else {
            // TODO 处理非文本消息
            return R.layout.item_chat_other_text;
        }
    }

    @Override
    public void addNewData(List<MessageModel> data) {
        EmUtil.markMessageListAsRead(mConversation, data);
        super.addNewData(data);
    }

    @Override
    public void setData(List<MessageModel> data) {
        EmUtil.markMessageListAsRead(mConversation, data);
        super.setData(data);
        smoothScrollToBottom();
    }

    @Override
    public void addLastItem(MessageModel messageModel) {
        // 如果不是第一条消息，处理是否添加时间类型的消息
        if (getItemCount() > 0) {
            MessageModel timeMessageModel = EmUtil.getTimeMessageModel(messageModel, getItem(getItemCount() - 1));
            if (timeMessageModel != null) {
                super.addLastItem(timeMessageModel);
            }
        }

        EmUtil.markMessageAsRead(mConversation, messageModel);
        super.addLastItem(messageModel);
        smoothScrollToBottom();
    }

    /**
     * 从内存中加载消息集合并刷新列表
     */
    public void loadMessageListFromMemory() {
        setData(EmUtil.loadMessageListFromMemory(mConversation));
    }

    public void smoothScrollToBottom() {
        mRecyclerView.smoothScrollToPosition(getItemCount());
    }
}