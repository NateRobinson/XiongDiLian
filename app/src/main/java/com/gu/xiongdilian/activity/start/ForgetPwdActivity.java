package com.gu.xiongdilian.activity.start;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.home.HomeActivity;
import com.gu.xiongdilian.base.XDLBaseActivity;
import com.gu.xiongdilian.pojo.Account;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * Created by Nate on 2015/11/23.忘记密码界面
 */
public class ForgetPwdActivity extends XDLBaseActivity {
    @InjectView(R.id.forget_pwd_phone_et)
    EditText forgetPwdPhoneEt;
    @InjectView(R.id.forget_pwd_vercode_et)
    EditText forgetPwdVercodeEt;
    @InjectView(R.id.register_pwd_et)
    EditText registerPwdEt;
    @InjectView(R.id.register_pwd_comfirm_et)
    EditText registerPwdComfirmEt;
    @InjectView(R.id.forget_pwd_vercode_send_btn)
    Button forgetPwdVercodeSendBtn;
    private String phone;
    private MyCountTimer timer;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.forget_pwd_activity_layout;
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
        setCustomToolbar(ToolbarType.WITHBACK, "忘记密码");
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

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    @OnClick({R.id.forget_pwd_vercode_send_btn, R.id.forget_pwd_do_next_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_pwd_do_next_btn:
                final String newPassword = registerPwdEt.getText().toString().trim();
                phone = forgetPwdPhoneEt.getText().toString().trim();
                String passwordConfirm = registerPwdComfirmEt.getText().toString().trim();
                String vercode = forgetPwdVercodeEt.getText().toString().trim();
                if (TextUtils.isEmpty(vercode) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(passwordConfirm) || TextUtils.isEmpty(vercode)) {
                    showToast("请完成所有填写项");
                    return;
                }
                if (!TextUtils.equals(newPassword, passwordConfirm)) {
                    showToast("两次密码不一致");
                    return;
                }
                showLoadingDialog();
                BmobUser.resetPasswordBySMSCode(this, vercode, newPassword, new ResetPasswordByCodeListener() {
                    @Override
                    public void done(BmobException ex) {
                        if (ex == null) {
                            LogUtils.e("smile", "密码重置成功");
                            BmobUser.loginByAccount(ForgetPwdActivity.this, phone, newPassword, new LogInListener<Account>() {
                                @Override
                                public void done(Account user, BmobException e) {
                                    if (user != null) {
                                        LogUtils.e("smile", "用户登陆成功");
                                        // 更新用户的地理位置以及好友的资料
                                        updateUserInfos();
                                        dismissLoadingDialog();
                                        goThenKill(HomeActivity.class);
                                    }
                                }
                            });
                        } else {
                            LogUtils.e("smile", "重置失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                        }
                    }
                });
                break;
            case R.id.forget_pwd_vercode_send_btn:
                if (timer != null) {
                    timer.cancel();
                }
                requestSMSCode();
                break;
            default:
                break;
        }
    }

    private void requestSMSCode() {
        String phone = forgetPwdPhoneEt.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(this, phone, "手机号码登陆模板", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {// 验证码发送成功
                        showToast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {//如果验证码发送错误，可停止计时
                        LogUtils.e(TAG_LOG,ex.getMessage());
                        timer.cancel();
                    }
                }
            });
        } else {
            showToast("请输入手机号码");
        }
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (forgetPwdVercodeSendBtn != null) {
                forgetPwdVercodeSendBtn.setText((millisUntilFinished / 1000) + "秒后重发");
            }
        }

        @Override
        public void onFinish() {
            forgetPwdVercodeSendBtn.setText("重新发送");
        }
    }
}
