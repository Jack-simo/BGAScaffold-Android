package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.BuildConfig;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ConversationModel;
import cn.bingoogolapple.scaffolding.util.AppManager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午7:11
 * 描述:
 */
public class LiteOrmUtil {
    private static LiteOrm sLiteOrm;

    private LiteOrmUtil() {
    }

    public static void init() {
        if (sLiteOrm != null) {
            sLiteOrm.close();
        }
        sLiteOrm = LiteOrm.newSingleInstance(AppManager.getApp(), "scaffolding.db");
        sLiteOrm.setDebugged(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"));
    }

    public static ConversationModel saveOrUpdateConversation(ConversationModel conversationModel) {
        ConversationModel existConversationModel = getConversationByConversationId(conversationModel.conversationId);
        if (existConversationModel == null) {
            sLiteOrm.save(conversationModel);
        } else {
            conversationModel.id = existConversationModel.id;
            sLiteOrm.update(conversationModel);
        }
        return conversationModel;
    }

    public static ConversationModel getConversationByConversationId(String conversationId) {
        List<ConversationModel> conversationModelList = sLiteOrm.query(
                new QueryBuilder<>(ConversationModel.class)
                        .whereEquals(ConversationModel.COL_CONVERSATION_ID, conversationId)
                        .limit(0, 1)
        );
        if (conversationModelList.size() > 0) {
            return conversationModelList.get(0);
        } else {
            return null;
        }
    }
}
