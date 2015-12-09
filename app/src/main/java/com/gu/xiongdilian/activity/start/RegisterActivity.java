package com.gu.xiongdilian.activity.start;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gu.baselibrary.utils.LogUtils;
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
 * Created by Nate on 2015/11/23.用户注册界面
 */
public class RegisterActivity extends XDLBaseActivity {

    @InjectView(R.id.register_account_et)
    EditText registerAccountEt;
    @InjectView(R.id.register_pwd_et)
    EditText registerPwdEt;
    @InjectView(R.id.register_pwd_comfirm_et)
    EditText registerPwdComfirmEt;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.register_activity_layout;
    }

    /**
     * 是否开启应用的全屏展示
     *
     * @return
     */
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

    /**
     * 处理Bundle传参
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    /**
     * @return true--自定义页面的切换动画   false--不自定义
     */
    @Override
    protected boolean isCustomPendingTransition() {
        return true;
    }

    /**
     * @return 返回自定义的动画切换方式
     */
    @Override
    protected TransitionMode getCustomPendingTransitionType() {
        return TransitionMode.FADE;
    }

    /**
     * 初始化所有布局和event事件
     */
    @Override
    protected void initViewsAndEvents() {
        setCustomToolbar(ToolbarType.WITHBACK, "注册");
    }

    /**
     * 网络连接连起来了
     *
     * @param type
     */
    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    /**
     * 网络连接断开
     */
    @Override
    protected void doOnNetworkDisConnected() {

    }

    @OnClick({R.id.do_login_register_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.do_login_register_btn:
                String userName = registerAccountEt.getText().toString().trim();
                final String password = registerPwdEt.getText().toString().trim();
                String passwordConfirm = registerPwdComfirmEt.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
                    showToast("请完成所有填写项");
                    return;
                }
                if (!TextUtils.equals(password, passwordConfirm)) {
                    showToast("两次密码不一致");
                    return;
                }
                showLoadingDialog();
                // 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
                final Account user = new Account();
                user.setUsername(userName);
                user.setPassword(password);
                user.setLevel(0);
                user.setFriendNum(0);
                user.setPostNum(0);
                user.setPicStoryNum(0);
                user.setAge(20);
                user.setSex(true);
                // 将user和设备id进行绑定
                user.setDeviceType("android");
                user.setInstallId(BmobInstallation.getInstallationId(this));
                showLoadingDialog();
                user.signUp(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        // 将设备与username进行绑定
                        userManager.bindInstallationForRegister(user.getUsername());
                        Bundle bundle = new Bundle();
                        bundle.putString("pwd", password);
                        goThenKill(BindPhoneActivity.class, bundle);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogUtils.e(TAG_LOG, "code==>" + code + "msg==>" + msg);
                        dismissLoadingDialog();
                        showToast(R.string.register_fail);
                    }
                });
                break;
            default:
                break;
        }
    }
}

