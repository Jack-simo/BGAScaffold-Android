package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.BuildConfig;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ChatUserModel;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/15 下午11:35
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
        sLiteOrm = LiteOrm.newSingleInstance(AppManager.getApp(), "scaffolding_hyphenatechat.db");
        sLiteOrm.setDebugged(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"));

        addTestChatUser();
    }

    /**
     * 添加用于测试的聊天用户信息到数据库
     */
    private static void addTestChatUser() {
        List<ChatUserModel> userModelList = new ArrayList<>();
        ChatUserModel chatUserModel;
        for (int i = 1; i < 6; i++) {
            chatUserModel = new ChatUserModel();
            chatUserModel.chatUserName = "test" + i;
            chatUserModel.nickName = "用户" + i;
            chatUserModel.avatar = "http://7xk9dj.com1.z0.glb.clouddn.com/adapter/imgs/" + i + ".png";
            userModelList.add(chatUserModel);
        }
        insertOrUpdateChatUserList(userModelList);
    }

    /**
     * 插入或修改聊天数据集
     *
     * @param chatUserModelList
     */
    public static void insertOrUpdateChatUserList(List<ChatUserModel> chatUserModelList) {
        Observable.from(chatUserModelList)
                .flatMap(new Func1<ChatUserModel, Observable<ChatUserModel>>() {
                    @Override
                    public Observable<ChatUserModel> call(ChatUserModel chatUserModel) {
                        return insertOrUpdateChatUser(chatUserModel);
                    }
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<ChatUserModel>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("添加或修改聊天用户 onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i("添加或修改聊天用户 onError");
                    }

                    @Override
                    public void onNext(ChatUserModel chatUserModel) {
                        Logger.i("添加或修改聊天用户成功 " + chatUserModel.nickName);
                    }
                });
    }

    /**
     * 插入或修改聊天用户
     *
     * @param chatUserModel
     * @return
     */
    public static Observable<ChatUserModel> insertOrUpdateChatUser(ChatUserModel chatUserModel) {
        return getChatUserModelByChatUserName(chatUserModel.chatUserName)
                .map(preChatUserModel -> {
                    if (preChatUserModel == null) {
                        sLiteOrm.insert(chatUserModel);
                    } else {
                        chatUserModel.id = preChatUserModel.id;
                        sLiteOrm.update(chatUserModel);
                    }
                    return chatUserModel;
                });
    }

    /**
     * 根据聊天用户名获取聊天用户
     *
     * @param chatUserName
     * @return
     */
    public static Observable<ChatUserModel> getChatUserModelByChatUserName(final String chatUserName) {
        return Observable.create(new Observable.OnSubscribe<ChatUserModel>() {
            @Override
            public void call(Subscriber<? super ChatUserModel> subscriber) {
                List<ChatUserModel> chatUserModelList = sLiteOrm.query(new QueryBuilder<>(ChatUserModel.class).whereEquals(ChatUserModel.COL_CHAT_USER_NAME, chatUserName));
                if (chatUserModelList != null && chatUserModelList.size() > 0) {
                    subscriber.onNext(chatUserModelList.get(0));
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 获取所有聊天用户信息
     *
     * @return
     */
    public static Observable<List<ChatUserModel>> getChatUserModelList() {
        return Observable.create(new Observable.OnSubscribe<List<ChatUserModel>>() {
            @Override
            public void call(Subscriber<? super List<ChatUserModel>> subscriber) {
                subscriber.onNext(sLiteOrm.query(ChatUserModel.class));
                subscriber.onCompleted();
            }
        });
    }

}
