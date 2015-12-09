package com.gu.xiongdilian.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gu.baselibrary.baseui.BaseActivity;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.activity.start.LoginActivity;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.utils.CollectionUtils;

import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * @author nate
 * @ClassName: BaseActivity
 * @Description: 整个应用的父类activity
 * @date 2015-5-21 下午3:42:58
 */
public abstract class XDLBaseActivity extends BaseActivity {
    protected BmobUserManager userManager = null;

    protected BmobChatManager manager = null;

    protected XiongDiLianApplication mApplication = null;

    protected Toolbar mToolbar = null;

    protected TextView mToolbarTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
        mApplication = XiongDiLianApplication.getXiongDiLianInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        mToolbarTitle = ButterKnife.findById(this, R.id.common_toolbar_title);
    }

    /**
     * 根据每个页面的需求设置toolbar
     *
     * @param type  toolbar 类型
     * @param title 标题
     */
    protected void setCustomToolbar(ToolbarType type, int title) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
            mToolbarTitle.setText(title);
            switch (type) {
                case WITHBACK:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;
                case NOBACK:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;
                default:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;
            }
        }
    }

    /**
     * 根据每个页面的需求设置toolbar
     *
     * @param type  toolbar 类型
     * @param title 标题
     */
    protected void setCustomToolbar(ToolbarType type, String title) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
            mToolbarTitle.setText(title);
            switch (type) {
                case WITHBACK:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;
                case NOBACK:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;
                default:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;
            }
        }
    }


    /**
     * 屏蔽系统的menu按钮功能
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Toolbar类型 WITHBACK--带返回；NOBACK--没有返回
     */
    public enum ToolbarType {
        WITHBACK, NOBACK;
    }

    /**
     * 显示下线的对话框 showOfflineDialog
     */
    public void showOfflineDialog() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("提示");
        mMaterialDialog.setMessage("您的账号已在其他设备上登录!\", \"重新登录");
        mMaterialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                XiongDiLianApplication.getXiongDiLianInstance().logout();
                go(LoginActivity.class);
                finish();
            }
        });
        mMaterialDialog.show();
    }

    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     */
    public void updateUserInfos() {
        // 更新地理位置信息
        updateUserLocation();
        // 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        // 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onError(int arg0, String arg1) {
                if (arg0 == BmobConfig.CODE_COMMON_NONE) {
                    LogUtils.d(TAG_LOG, arg1);
                } else {
                    LogUtils.d(TAG_LOG, "查询好友列表失败：" + arg1);
                }
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // 保存到application中方便比较
                XiongDiLianApplication.getXiongDiLianInstance().setContactList(CollectionUtils.list2map(arg0));
            }
        });
    }

    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {
        if (XiongDiLianApplication.lastPoint != null) {
            String saveLatitude = mApplication.getLatitude();
            String saveLongtitude = mApplication.getLongtitude();
            String newLat = String.valueOf(XiongDiLianApplication.lastPoint.getLatitude());
            String newLong = String.valueOf(XiongDiLianApplication.lastPoint.getLongitude());
            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// 只有位置有变化就更新当前位置，达到实时更新的目的
                final Account Account = userManager.getCurrentUser(Account.class);
                Account.setLocation(XiongDiLianApplication.lastPoint);
                Account.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        XiongDiLianApplication.getXiongDiLianInstance().setLatitude(String.valueOf(Account.getLocation()
                                .getLatitude()));
                        XiongDiLianApplication.getXiongDiLianInstance().setLongtitude(String.valueOf(Account.getLocation()
                                .getLongitude()));
                        LogUtils.d(TAG_LOG, "经纬度更新成功");
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogUtils.d(TAG_LOG, "经纬度更新 失败:" + msg);
                    }
                });
            } else {
                LogUtils.d(TAG_LOG, "用户位置未发生过变化");
            }
        }
    }
}
