package com.gu.xiongdilian.base;


import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.start.LoginActivity;

/**
 * @author nate
 * @ClassName: XDLBaseWithCheckLoginActivity
 * @Description: 除登陆注册和欢迎页面外继承的基类-用于检测是否有其他设备登录了同一账号
 * @date 2015年6月4日17:03:41
 */
public abstract class XDLBaseWithCheckLoginActivity extends XDLBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 自动登陆状态下检测是否在其他设备登陆
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 锁屏状态下的检测
        checkLogin();
    }

    private void checkLogin() {
        if (userManager.getCurrentUser() == null) {
            showToast(R.string.other_place_login_notice);
            goThenKill(LoginActivity.class);
        }
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     */
    protected void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
