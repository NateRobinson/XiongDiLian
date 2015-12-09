package com.gu.xiongdilian.activity.start;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.activity.home.HomeActivity;
import com.gu.xiongdilian.base.XDLBaseActivity;

/**
 * @author nate
 * @ClassName: StartActivity
 * @Description: 启动页面
 * @date 2015-5-26 下午10:33:29
 */
public class StartActivity extends XDLBaseActivity {

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;
    // 定位获取当前用户的地理位置
    private LocationClient mLocationClient = null;
    private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    go(HomeActivity.class);
                    finish();
                    break;
                case GO_LOGIN:
                    go(LoginActivity.class);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.start_app_layout;
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
        // 开启定位
        initLocClient();
        // 注册地图 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiduReceiver();
        registerReceiver(mReceiver, iFilter);
    }


    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (userManager.getCurrentUser() != null) {
            // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
            updateUserInfos();
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * 开启定位，更新当前用户的经纬度坐标
     *
     * @param
     * @return void
     * @throws
     * @Title: initLocClient
     * @Description: TODO
     */
    private void initLocClient() {
        mLocationClient = XiongDiLianApplication.getXiongDiLianInstance().mLocationClient;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
        option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
        option.setIsNeedAddress(false);// 不需要包含地址信息
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                showToast(R.string.baidumap_key_error);
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                showToast(R.string.network_tips);
            }
        }
    }
}
