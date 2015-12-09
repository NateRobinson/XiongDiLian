package com.gu.xiongdilian.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.Account;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author nate
 * @ClassName: SetNickAndSexActivity
 * @Description: 设置昵称和性别
 * @date 2015年6月4日17:20:15
 */
public class UpdateInfoActivity extends XDLBaseWithCheckLoginActivity {
    @InjectView(R.id.edit_nick)
    EditText edit_nick;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_set_updateinfo;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.modify_nick_name);
        String nick=userManager.getCurrentUser().getNick();
        if(!TextUtils.isEmpty(nick)){
            edit_nick.setText(nick);
            edit_nick.setSelection(nick.length());
        }
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

    /**
     * 修改资料
     */
    private void updateInfo(String nick) {
        final Account account = userManager.getCurrentUser(Account.class);
        account.setNick(nick);
        account.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @OnClick({R.id.sure_change_nick_name_btn})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.sure_change_nick_name_btn:
                String nick = edit_nick.getText().toString();
                if (TextUtils.isEmpty(nick)) {
                    showToast(R.string.please_enter_nick_name);
                    return;
                }
                updateInfo(nick);
                break;
            default:
                break;
        }
    }
}