package com.gu.xiongdilian.activity.friends;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.settings.SetMyInfoActivity;
import com.gu.xiongdilian.adapter.friends.NearPeopleAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.utils.CollectionUtils;
import com.gu.xiongdilian.view.XListView;
import com.gu.xiongdilian.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * @author nate
 * @ClassName: NewFriendActivity
 * @Description: 附近的人列表
 * @date 2015年6月4日15:06:09
 */
public class NearPeopleActivity extends XDLBaseWithCheckLoginActivity implements IXListViewListener, OnItemClickListener {
    @InjectView(R.id.list_near)
    XListView mListView;
    @InjectView(R.id.list_empty_ll)
    LinearLayout list_empty_ll;
    private NearPeopleAdapter adapter = null;

    private List<Account> nears = new ArrayList<>();

    private double QUERY_KILOMETERS = 10;// 默认查询10公里范围内的人

    private int curPage = 0;

    private ProgressDialog progress = null;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_near_people;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.near_people);
        initXListView();
        mListView.setOnItemClickListener(this);
        // 设置监听器
        mListView.setXListViewListener(this);
        mListView.setEmptyView(list_empty_ll);
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Account account = adapter.getItem(position - 1);
        Bundle bundle = new Bundle();
        bundle.putString("from", "add");
        bundle.putString("username", account.getUsername());
        go(SetMyInfoActivity.class, bundle);
    }

    @Override
    public void onRefresh() {
        initNearByList(true);
    }

    @Override
    public void onLoadMore() {
        double latitude = Double.parseDouble(mApplication.getLatitude());
        double longtitude = Double.parseDouble(mApplication.getLongtitude());
        userManager.queryKiloMetersTotalCount(Account.class,
                "location",
                longtitude,
                latitude,
                true,
                QUERY_KILOMETERS,
                null,
                null,
                new CountListener() {
                    @Override
                    public void onSuccess(int arg0) {
                        if (arg0 > nears.size()) {
                            curPage++;
                            queryMoreNearList(curPage);
                        } else {
                            mListView.setPullLoadEnable(false);
                            refreshLoad();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        LogUtils.d(TAG_LOG, "查询附近的人总数失败" + arg1);
                        refreshLoad();
                    }
                });
    }

    /**
     * 初始化XListView
     */
    private void initXListView() {
        mListView.setPullLoadEnable(true);
        // 允许下拉
        mListView.setPullRefreshEnable(true);
        adapter = new NearPeopleAdapter(this, R.layout.item_near_people, nears);
        mListView.setAdapter(adapter);
        initNearByList(false);
    }

    /**
     * 查找附近的人
     *
     * @param isUpdate
     */
    private void initNearByList(final boolean isUpdate) {
        if (!isUpdate) {
            progress = new ProgressDialog(NearPeopleActivity.this);
            progress.setMessage(getString(R.string.is_looking_near_peo));
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        }
        if (!mApplication.getLatitude().equals("") && !mApplication.getLongtitude().equals("")) {
            double latitude = Double.parseDouble(mApplication.getLatitude());
            double longtitude = Double.parseDouble(mApplication.getLongtitude());
            // 封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
            // 此方法默认每页查询10条数据,若想查询多于10条，可在查询之前设置BRequest.QUERY_LIMIT_COUNT，如：BRequest.QUERY_LIMIT_COUNT=20
            // 此方法是新增的查询指定10公里内的性别为女性的用户列表，默认包含好友列表
            // 如果你不想查询性别为女的用户，可以将equalProperty设为null或者equalObj设为null即可
            userManager.queryKiloMetersListByPage(isUpdate,
                    0,
                    "location",
                    longtitude,
                    latitude,
                    true,
                    QUERY_KILOMETERS,
                    null,
                    null,
                    new FindListener<Account>() {
                        @Override
                        public void onSuccess(List<Account> arg0) {
                            if (CollectionUtils.isNotNull(arg0)) {
                                if (isUpdate) {
                                    nears.clear();
                                }
                                adapter.addAll(arg0);
                                if (arg0.size() < BRequest.QUERY_LIMIT_COUNT) {
                                    mListView.setPullLoadEnable(false);
                                    showToast(R.string.search_near_peo_finish);
                                } else {
                                    mListView.setPullLoadEnable(true);
                                }
                            } else {
                                showToast(R.string.no_near_peo);
                            }
                            if (!isUpdate) {
                                progress.dismiss();
                            } else {
                                refreshPull();
                            }
                        }

                        @Override
                        public void onError(int arg0, String arg1) {
                            showToast(R.string.no_near_peo);
                            mListView.setPullLoadEnable(false);
                            if (!isUpdate) {
                                progress.dismiss();
                            } else {
                                refreshPull();
                            }
                        }

                    });
        } else {
            showToast(R.string.no_near_peo);
            if (!isUpdate) {
                progress.dismiss();
            } else {
                refreshPull();
            }
        }

    }

    /**
     * 查询更多
     */
    private void queryMoreNearList(int page) {
        double latitude = Double.parseDouble(mApplication.getLatitude());
        double longtitude = Double.parseDouble(mApplication.getLongtitude());
        userManager.queryKiloMetersListByPage(true,
                page,
                "location",
                longtitude,
                latitude,
                true,
                QUERY_KILOMETERS,
                null,
                null,
                new FindListener<Account>() {
                    @Override
                    public void onSuccess(List<Account> arg0) {
                        if (CollectionUtils.isNotNull(arg0)) {
                            adapter.addAll(arg0);
                        }
                        refreshLoad();
                    }

                    @Override
                    public void onError(int arg0, String arg1) {
                        LogUtils.d(TAG_LOG, "查询更多附近的人出错:" + arg1);
                        mListView.setPullLoadEnable(false);
                        refreshLoad();
                    }

                });
    }


    private void refreshLoad() {
        mListView.stopLoadMore();
    }

    private void refreshPull() {
        mListView.stopRefresh();
    }
}
