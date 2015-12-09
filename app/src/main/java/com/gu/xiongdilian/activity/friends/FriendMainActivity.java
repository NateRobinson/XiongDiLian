package com.gu.xiongdilian.activity.friends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.fragment.friends.ContactFragment;
import com.gu.xiongdilian.fragment.friends.RecentFragment;
import com.gu.xiongdilian.receiver.MyMessageReceiver;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.EventListener;

/**
 * @author nate
 * @ClassName: MainActivity
 * @Description:聊天功能主页面
 * @date 2015年6月4日14:46:52
 */
public class FriendMainActivity extends XDLBaseWithCheckLoginActivity implements EventListener {

    @InjectView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @InjectView(R.id.viewpager)
    ViewPager viewPager;
    private List<Fragment> fragmentList;
    private List<String> tabNames;
    private NewBroadcastReceiver newReceiver = null;
    private SimpleFragmentPagerAdapter pagerAdapter = null;
    private RecentFragment recentFragment = null;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.friends_activity_main;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.friend);
        initNewMessageBroadCast();// 新消息的广播
        fragmentList = new ArrayList<>();
        tabNames = new ArrayList<>();
        recentFragment = new RecentFragment();
        fragmentList.add(recentFragment);
        fragmentList.add(new ContactFragment());
        tabNames.add("消息");
        tabNames.add("联系人");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //MODE_FIXED模式会使及格tab均分屏幕的宽度
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
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
    protected void onResume() {
        super.onResume();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new_friend_menu) {
            go(AddFriendActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessage(BmobMsg message) {
        refreshNewMsg(message);
    }

    @Override
    public void onOffline() {
        showOfflineDialog();
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (isNetConnected) {
            showToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
    }


    /**
     * 刷新界面
     */
    private void refreshNewMsg(BmobMsg message) {
        //刷新最近聊天的页面
        if (recentFragment != null) {
            recentFragment.refresh();
        }
        // 也要存储起来
        if (message != null) {
            BmobChatManager.getInstance(FriendMainActivity.this).saveReceiveMessage(true, message);
        }
    }

    private void initNewMessageBroadCast() {
        // 注册接收消息广播
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // 优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }


    /**
     * 新消息广播接收者
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshNewMsg(null);
            abortBroadcast();
        }
    }

    class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        public SimpleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames.get(position);
        }

    }
}
