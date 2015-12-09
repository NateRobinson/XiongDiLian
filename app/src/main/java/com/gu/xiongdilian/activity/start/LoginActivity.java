package com.gu.xiongdilian.activity.start;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.home.HomeActivity;
import com.gu.xiongdilian.base.XDLBaseActivity;
import com.gu.xiongdilian.pojo.Account;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author nate
 * @ClassName: RegistActivity
 * @Description: 注册页面
 * @date 2015-5-21 下午10:56:41
 */
public class LoginActivity extends XDLBaseActivity {
    @InjectView(R.id.login_account_et)
    EditText loginAccountEt;
    @InjectView(R.id.login_account_pwd_et)
    EditText loginAccountPwdEt;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.register_login_layout;
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
        setCustomToolbar(ToolbarType.NOBACK, R.string.login);
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @OnClick({R.id.do_login_register_btn, R.id.register_login_forget_tv, R.id.register_login_new_register_tv})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.do_login_register_btn:
                doLoginOrRegister();
                break;
            case R.id.register_login_forget_tv:
                go(ForgetPwdActivity.class);//跳转忘记密码界面
                break;
            case R.id.register_login_new_register_tv:
                go(RegisterActivity.class);//跳转用户注册界面
                break;
            default:
                break;
        }
    }

    /**
     * 登入or注册
     */
    private void doLoginOrRegister() {
        boolean isNetConnected = NetUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            showToast(R.string.network_tips);
            return;
        }
        if (true) {
            showLoadingDialog();
            String nameLogin = loginAccountEt.getText().toString();
            String passwordLogin = loginAccountPwdEt.getText().toString();
            // 登入前判断
            if (TextUtils.isEmpty(nameLogin)) {
                showToast(R.string.toast_error_username_null);
                return;
            }
            if (TextUtils.isEmpty(passwordLogin)) {
                showToast(R.string.toast_error_password_null);
                return;
            }
            final Account account = new Account();
            account.setUsername(nameLogin);
            account.setPassword(passwordLogin);
            userManager.login(account, new SaveListener() {
                @Override
                public void onSuccess() {
                    // 更新用户的地理位置以及好友的资料
                    updateUserInfos();
                    dismissLoadingDialog();
                    goThenKill(HomeActivity.class);
                }

                @Override
                public void onFailure(int errorcode, String arg0) {
                    dismissLoadingDialog();
                    showToast(arg0);
                }
            });
        } else {
//            String nameRegister = registerAccountEt.getText().toString();
//            String pwdRegister = registerAccountPwdEt.getText().toString();
//            String pwdAgainRegister = registerAccountPwdComfirmEt.getText().toString();
//            if (TextUtils.isEmpty(nameRegister)) {
//                showToast(R.string.toast_error_username_null);
//                return;
//            }
//            if (TextUtils.isEmpty(pwdRegister)) {
//                showToast(R.string.toast_error_password_null);
//                return;
//            }
//            if (!pwdRegister.equals(pwdAgainRegister)) {
//                showToast(R.string.toast_error_comfirm_password);
//                return;
//            }
//            showLoadingDialog();
//            // 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
//            final Account account = new Account();
//            account.setUsername(nameRegister);
//            account.setPassword(pwdRegister);
//            account.setLevel(0);
//            account.setFriendNum(0);
//            account.setPostNum(0);
//            account.setPicStoryNum(0);
//            account.setAge(20);
//            account.setSex(true);
//            // 将user和设备id进行绑定
//            account.setDeviceType("android");
//            account.setInstallId(BmobInstallation.getInstallationId(this));
//            account.signUp(this, new SaveListener() {
//                @Override
//                public void onSuccess() {
//                    // 将设备与username进行绑定
//                    userManager.bindInstallationForRegister(account.getUsername());
//                    // 更新地理位置信息
//                    updateUserLocation();
//                    goThenKill(HomeActivity.class);
//                }
//
//                @Override
//                public void onFailure(int code, String msg) {
//                    dismissLoadingDialog();
//                    showToast(R.string.register_fail);
//                }
//            });
        }
    }

}
