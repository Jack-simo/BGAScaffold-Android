package cn.bingoogolapple.rxjava.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class PreferencesActivity extends TitlebarActivity {
    private CompositeSubscription mSubscriptions;

    private CheckBox mFool1Cb;
    private CheckBox mFool2Cb;
    private CheckBox mFool3Cb;
    private CheckBox mFool4Cb;
    private Preference<Boolean> mFooPreference;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_preference);
        mFool1Cb = getViewById(R.id.cb_preference_fool1);
        mFool2Cb = getViewById(R.id.cb_preference_fool2);
        mFool3Cb = getViewById(R.id.cb_preference_fool3);
        mFool4Cb = getViewById(R.id.cb_preference_fool4);
    }

    @Override
    protected void setListener() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApp);
        RxSharedPreferences rxPreferences = RxSharedPreferences.create(preferences);

        mFooPreference = rxPreferences.getBoolean("foo");

        Preference<Boolean> checked = rxPreferences.getBoolean("checked", true);
        RxCompoundButton
                .checkedChanges((CheckBox) getViewById(R.id.cb_preference_checked))
                .subscribe(checked.asAction());
        checked.asObservable().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isChecked) {
                ToastUtil.show(isChecked ? "选中" : "取消选中");
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Preferences学习笔记");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubscriptions = new CompositeSubscription();
        bindPreference(mFool1Cb, mFooPreference);
        bindPreference(mFool2Cb, mFooPreference);
        bindPreference(mFool3Cb, mFooPreference);
        bindPreference(mFool4Cb, mFooPreference);
    }

    private void bindPreference(CheckBox checkBox, Preference<Boolean> preference) {
        // Bind the preference to the checkbox.
        mSubscriptions.add(preference.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxCompoundButton.checked(checkBox)));

        // Bind the checkbox to the preference.
        mSubscriptions.add(RxCompoundButton.checkedChanges(checkBox)
                .skip(1)
                .subscribe(preference.asAction()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onClick(View v) {
    }
}