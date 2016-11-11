package cn.bingoogolapple.scaffolding.demo.hyphenatechat;

import android.util.Pair;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMOptions;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.scaffolding.demo.BuildConfig;
import cn.bingoogolapple.scaffolding.util.AppManager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:06
 * 描述:
 */
public class EmUtil {
    private EmUtil() {
    }

    /**
     * 初始化环信 SDK，在 Application 的 onCreate 方法里调用
     */
    public static void initSdk() {
        EMOptions options = new EMOptions();
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执，
        options.setRequireDeliveryAck(true);
        // 设置是否需要服务器收到消息确认
        options.setRequireServerAck(true);
        // 设置是否根据服务器时间排序，默认是true
        options.setSortMessageByServerTime(false);
        options.allowChatroomOwnerLeave(true);
        // 设置google GCM推送id，国内可以不用设置
        // options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
        // 设置集成小米推送的appid和appkey
        // options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

        EMClient.getInstance().init(AppManager.getApp(), options);
        EMClient.getInstance().setDebugMode(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"));

        Logger.i("初始化了环信 SDK");
    }

    /**
     * load conversation list
     *
     * @return +
     */
    public static List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private static void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
}
