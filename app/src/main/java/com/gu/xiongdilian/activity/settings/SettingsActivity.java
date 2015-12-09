package com.gu.xiongdilian.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.utils.SPUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.activity.start.LoginActivity;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author nate
 * @ClassName: SettingsActivity
 * @Description: 个人设置界面
 * @date 2015-6-5 下午3:23:42
 */
public class SettingsActivity extends XDLBaseWithCheckLoginActivity {
    @InjectView(R.id.sb_notification)
    SwitchButton sb_notification;
    @InjectView(R.id.sb_voice)
    SwitchButton sb_voice;
    @InjectView(R.id.sb_vibrate)
    SwitchButton sb_vibrate;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_set;
    }

    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }

    /**
     * 是否绑定了EventBus
     *
     * @return
     */
    @Override
    protected boolean isBindEventBus() {
        return false;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected boolean isCustomPendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getCustomPendingTransitionType() {
        return TransitionMode.FADE;
    }

    @Override
    protected void initViewsAndEvents() {
        setCustomToolbar(ToolbarType.WITHBACK, R.string.mian_tab_set);
        setSwitchButtons();
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @OnClick({R.id.layout_blacklist, R.id.layout_info, R.id.btn_logout})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.layout_blacklist:// 启动到黑名单页面
                go(BlackListActivity.class);
                break;
            case R.id.layout_info:// 启动到个人资料页面
                Bundle bundle = new Bundle();
                bundle.putString("from", "me");
                go(SetMyInfoActivity.class, bundle);
                break;
            case R.id.btn_logout:
                XiongDiLianApplication.getXiongDiLianInstance().bmobLogout();
                goThenKill(LoginActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 设置提醒，声音，震动的开关
     */
    private void setSwitchButtons() {
        boolean isAllowNotify = (boolean) SPUtils.get(this, MyConfig.SHARED_KEY_NOTIFY, true);
        sb_notification.setChecked(isAllowNotify);
        sb_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SettingsActivity.this, MyConfig.SHARED_KEY_NOTIFY, isChecked);
                //设置其他两个不可点击
                sb_voice.setEnabled(isChecked);
                sb_vibrate.setEnabled(isChecked);
            }
        });

        boolean isAllowVoice = (boolean) SPUtils.get(this, MyConfig.SHARED_KEY_VOICE, true);
        sb_voice.setChecked(isAllowVoice);
        sb_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SettingsActivity.this, MyConfig.SHARED_KEY_VOICE, isChecked);
            }
        });

        boolean isAllowVibrate = (boolean) SPUtils.get(this, MyConfig.SHARED_KEY_VIBRATE, true);
        sb_vibrate.setChecked(isAllowVibrate);
        sb_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SettingsActivity.this, MyConfig.SHARED_KEY_VIBRATE, isChecked);
            }
        });
    }
}
