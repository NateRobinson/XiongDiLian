package com.gu.xiongdilian.activity.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.utils.SPUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.activity.friends.FriendMainActivity;
import com.gu.xiongdilian.activity.friends.NewFriendActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.receiver.MyMessageReceiver;
import com.gu.xiongdilian.activity.xiongdilian.MyXiongDiLianHomeActivity;
import com.gu.xiongdilian.activity.settings.SettingsActivity;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.tencent.bugly.crashreport.CrashReport;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

/**
 * @author nate
 * @ClassName: HomeActivity
 * @Description: 应用首页
 * @date 2015-5-21 下午5:28:49
 */
public class HomeActivity extends XDLBaseWithCheckLoginActivity implements EventListener {
    private static final int MY_XIONGDILIAN_LINEARLAYOUT = 1;
    private static final int FRIEND_LAYOUT = 2;
    private static final int PIC_STROY_LAYOUT = 3;
    private static final int SETTING_CENTER_LILINEARLAYOUT = 4;
    @InjectView(R.id.my_xiongdilian_ll)
    LinearLayout myXiongdilianLinearLayout;
    @InjectView(R.id.friend_ll)
    RelativeLayout friendLayout;
    @InjectView(R.id.pic_story_ll)
    LinearLayout picStroyLayout;
    @InjectView(R.id.setting_center_ll)
    LinearLayout settingCenterliLinearLayout;
    @InjectView(R.id.tips_tv)
    TextView tips_tv;
    private Animation mAnimation = null;
    private int flag = -1;
    private static long firstTime = 0;
    private NewBroadcastReceiver newReceiver = null;
    private TagBroadcastReceiver userReceiver = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.home_layout;
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
        //开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
        // 如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
        BmobChat.getInstance(this).startPollService(10);
        CrashReport.setUserId(userManager.getCurrentUserName()); // 设置用户的唯一标识
        initNewMessageBroadCast();// 新消息的广播
        initTagMessageBroadCast();// 添加好友的广播


        mAnimation = AnimationUtils.loadAnimation(this, R.anim.home_ll_ani);
        mAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                switch (flag) {
                    case MY_XIONGDILIAN_LINEARLAYOUT:
                        go(MyXiongDiLianHomeActivity.class);
                        break;
                    case FRIEND_LAYOUT:
                        go(FriendMainActivity.class);
                        break;
                    case PIC_STROY_LAYOUT:
                        break;
                    case SETTING_CENTER_LILINEARLAYOUT:
                        go(SettingsActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 小圆点提示
        if (BmobDB.create(this).hasUnReadMsg() || BmobDB.create(this).hasNewInvite()) {
            tips_tv.setVisibility(View.VISIBLE);
        } else {
            tips_tv.setVisibility(View.GONE);
        }
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        // 清空
        MyMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(newReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(userReceiver);
        } catch (Exception e) {
        }
        // 取消定时检测服务
        BmobChat.getInstance(this).stopPollService();
    }

    // 连续按两次返回键就退出
    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            mApplication.logout();
        } else {
            showToast("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }

    @OnClick({R.id.my_xiongdilian_ll, R.id.friend_ll, R.id.pic_story_ll, R.id.setting_center_ll})
    public void bindClick(View v) {

        switch (v.getId()) {
            case R.id.my_xiongdilian_ll:
                flag = MY_XIONGDILIAN_LINEARLAYOUT;
                myXiongdilianLinearLayout.startAnimation(mAnimation);
                break;
            case R.id.friend_ll:
                flag = FRIEND_LAYOUT;
                friendLayout.startAnimation(mAnimation);
                break;
            case R.id.pic_story_ll:
                flag = PIC_STROY_LAYOUT;
                picStroyLayout.startAnimation(mAnimation);
                break;
            case R.id.setting_center_ll:
                flag = SETTING_CENTER_LILINEARLAYOUT;
                settingCenterliLinearLayout.startAnimation(mAnimation);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        tips_tv.setVisibility(View.VISIBLE);
        refreshInvite(message);
    }

    @Override
    public void onMessage(BmobMsg message) {
        tips_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (isNetConnected) {
            showToast(R.string.network_tips);
        }
    }

    @Override
    public void onOffline() {
        showOfflineDialog();
    }

    @Override
    public void onReaded(String arg0, String arg1) {

    }

    private void initNewMessageBroadCast() {
        // 注册接收消息广播
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // 优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }


    private void initTagMessageBroadCast() {
        // 注册接收消息广播
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        // 优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }


    /**
     * 刷新好友请求
     */
    private void refreshInvite(BmobInvitation message) {
        boolean isAllow = (boolean) SPUtils.get(this, MyConfig.SHARED_KEY_VOICE, true);
        if (isAllow) {
            XiongDiLianApplication.getXiongDiLianInstance().getMediaPlayer().start();
        }
        // 同时提醒通知
        String tickerText = message.getFromname() + "请求添加好友";
        boolean isAllowVibrate = (boolean) SPUtils.get(this, MyConfig.SHARED_KEY_VIBRATE, true);
        BmobNotifyManager.getInstance(this).showNotify(isAllow,
                isAllowVibrate,
                R.mipmap.app_icon,
                tickerText,
                message.getFromname(),
                tickerText.toString(),
                NewFriendActivity.class);
    }

    /**
     * 新消息广播接收者
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tips_tv.setVisibility(View.VISIBLE);
            // 声音提示
            boolean isAllow = (boolean) SPUtils.get(HomeActivity.this, MyConfig.SHARED_KEY_VOICE, true);
            if (isAllow) {
                XiongDiLianApplication.getXiongDiLianInstance().getMediaPlayer().start();
            }
        }
    }

    /**
     * 标签消息广播接收者
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tips_tv.setVisibility(View.VISIBLE);
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
        }
    }

}
