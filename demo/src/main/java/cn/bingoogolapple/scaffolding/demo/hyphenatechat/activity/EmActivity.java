package cn.bingoogolapple.scaffolding.demo.hyphenatechat.activity;

import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.chat.EMClient;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityEmBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.EmUtil;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.util.RxEmEvent;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/15 上午5:05
 * 描述:环信案例主界面
 */
public class EmActivity extends MvcBindingActivity<ActivityEmBinding> {
    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_em;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        RxBus.toObservableAndBindToLifecycle(RxEmEvent.LoginEvent.class, this).subscribe(loginEvent -> {
            if (loginEvent.mIsSuccess) {
                ToastUtil.show("登录聊天服务器成功 " + EMClient.getInstance().getCurrentUser());

                forward(ConversationActivity.class);
            } else {
                ToastUtil.showSafe("登录聊天服务器失败 code:" + loginEvent.mCode + " message:" + loginEvent.mMessage);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        RxBus.toObservableAndBindUntilStop(RxEmEvent.UnreadMsgCountChangedEvent.class, this).subscribe(unreadMsgCountChangedEvent -> {
            if (unreadMsgCountChangedEvent.mUnreadMsgCount > 0) {
                String badgeText = unreadMsgCountChangedEvent.mUnreadMsgCount > 99 ? "99+" : String.valueOf(unreadMsgCountChangedEvent.mUnreadMsgCount);
                mBinding.btvEmUnread.showTextBadge(badgeText);
            } else {
                mBinding.btvEmUnread.hiddenBadge();
            }
        });

        EmUtil.loadConversationList();
    }

    /**
     * 显示选择环信账号对话框
     */
    public void showChooseEmAccountDialog() {
        new MaterialDialog.Builder(this)
                .title("请选择环信账号")
                .items("test1", "test2", "test3", "test4", "test5")
                .itemsCallback((dialog, itemView, position, text) -> {
                    EmUtil.login(text.toString(), "111111");
                })
                .show();
    }
}
