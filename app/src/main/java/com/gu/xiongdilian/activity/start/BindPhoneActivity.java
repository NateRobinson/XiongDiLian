package com.gu.xiongdilian.activity.start;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.home.HomeActivity;
import com.gu.xiongdilian.base.XDLBaseActivity;
import com.gu.xiongdilian.pojo.Account;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by Nate on 2015/11/24.绑定手机还哦啊页面
 */
public class BindPhoneActivity extends XDLBaseActivity {

    @InjectView(R.id.bind_phone_et)
    EditText bindPhoneEt;
    @InjectView(R.id.bind_vercode_et)
    EditText bindVercodeEt;
    @InjectView(R.id.bind_get_vercode_btn)
    Button bindGetVercodeBtn;
    @InjectView(R.id.bind_login_btn)
    Button bindLoginBtn;
    private MyCountTimer timer;
    private String pwd;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.bind_phone_activity_layout;
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
        pwd = extras.getString("pwd");
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
        setCustomToolbar(ToolbarType.WITHBACK, "绑定手机");
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

    @OnClick({R.id.bind_get_vercode_btn, R.id.bind_login_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_get_vercode_btn:
                if (timer != null) {
                    timer.cancel();
                }
                requestSMSCode();
                break;
            case R.id.bind_login_btn:
                verifyOrBind();
                break;
            default:
                break;
        }
    }

    private void requestSMSCode() {
        String phone = bindPhoneEt.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(this, phone, "手机号码登陆模板", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {// 验证码发送成功
                        showToast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {//如果验证码发送错误，可停止计时
                        timer.cancel();
                    }
                }
            });
        } else {
            showToast("请输入手机号码");
        }
    }

    private void verifyOrBind() {
        final String phone = bindPhoneEt.getText().toString();
        String code = bindVercodeEt.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        showLoadingDialog();
        // V3.3.9提供的一键注册或登录方式，可传手机号码和验证码
        BmobSMS.verifySmsCode(this, phone, code, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                if (ex == null) {
                    showToast("手机号码已验证");
                    bindMobilePhone(phone);
                } else {
                    dismissLoadingDialog();
                    showToast("验证失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }

    private void bindMobilePhone(String phone) {
        //开发者在给用户绑定手机号码的时候需要提交两个字段的值：mobilePhoneNumber、mobilePhoneNumberVerified
        final Account cur = BmobUser.getCurrentUser(BindPhoneActivity.this, Account.class);
        cur.setMobilePhoneNumber(phone);
        cur.setMobilePhoneNumberVerified(true);
        LogUtils.e(TAG_LOG, "username=>" + cur.getUsername() + "***pwd==>");
        cur.update(BindPhoneActivity.this, cur.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                cur.setPassword(pwd);
                userManager.login(cur, new SaveListener() {
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
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                dismissLoadingDialog();
                showToast("手机号码绑定失败：" + arg0 + "-" + arg1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (bindGetVercodeBtn != null) {
                bindGetVercodeBtn.setText((millisUntilFinished / 1000) + "秒后重发");
            }
        }

        @Override
        public void onFinish() {
            bindGetVercodeBtn.setText("重新发送");
        }
    }
}
