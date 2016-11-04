package cn.bingoogolapple.basenote.presenter;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.view.BaseView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午8:41
 * 描述:
 */
public class BasePresenterImpl<T extends BaseView> implements BasePresenter {
    protected T mView;
    protected App mApp;

    public BasePresenterImpl(T view) {
        mView = view;
        mApp = App.getInstance();
    }
}
