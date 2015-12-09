package com.gu.xiongdilian.activity.xiongdilian;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.xiongdilian.MyXiongDiLianAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.events.XiongDiLianEvent;
import com.gu.xiongdilian.events.XiongDiLianRefreshEvent;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * @author nate
 * @ClassName: MyXiongDiLianHomeActivity
 * @Description: 我的兄弟连主页面
 * @date 2015-5-25 上午11:24:22
 */
public class MyXiongDiLianHomeActivity extends XDLBaseWithCheckLoginActivity implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.my_xiongdilian_swipe)
    SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.my_xiongdilian_lv)
    ListView mListView;
    @InjectView(R.id.my_xiongdilian_empty_ll)
    LinearLayout myXiongdilianEmptyLl;
    private MyXiongDiLianAdapter mAdapter = null;
    private List<XiongDiLian> xiongDiLians = new ArrayList<>();
    private ArrayList<DialogMenuItem> menuItems = new ArrayList<>();
    private NormalListDialog normalListDialog = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.my_xiongdilian_layout;
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
        return true;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.my_xiongdilian);
        mAdapter = new MyXiongDiLianAdapter(this, R.layout.my_xiongdilian_lv_item_layout, xiongDiLians);
        mSwipeLayout.setColorSchemeResources(R.color.primary, R.color.primary_light);
        mSwipeLayout.setOnRefreshListener(this);
        //设置在滑动或者猛滑过程中，不加载图片
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        menuItems.add(new DialogMenuItem(getString(R.string.create), R.mipmap.xiongdianlian_add_menu_bg));
        menuItems.add(new DialogMenuItem(getString(R.string.search), R.mipmap.xiongdilian_search_menu_bg));
        normalListDialog = new NormalListDialog(this, menuItems);
        normalListDialog.title(getString(R.string.please_select))//
                .titleBgColor(Color.parseColor("#00BCD4"));
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    go(CreateXiongDiLianActivity.class);
                } else if (position == 1) {
                    go(SearchXionDiLianActivity.class);
                }
                normalListDialog.dismiss();
            }
        });

        getData();
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {
    }

    @Override
    protected void doOnNetworkDisConnected() {
    }

    // Called in Android UI's main thread
    public void onEventMainThread(XiongDiLianEvent event) {
        for (int i = 0; i < xiongDiLians.size(); i++) {
            if (xiongDiLians.get(i).getObjectId().equals(event.getXiongDiLian().getObjectId())) {
                XiongDiLian xiongDiLian = xiongDiLians.get(i);
                xiongDiLian.setPostNum(xiongDiLian.getPostNum() + 1);
            }
        }
        LogUtils.d(TAG_LOG, "onEventMainThread");
        mAdapter.notifyDataSetChanged();
    }

    // Called in Android UI's main thread
    public void onEventMainThread(XiongDiLianRefreshEvent event) {
        getData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.xiongdilian_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_xiongdilian_menu) {
            normalListDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取该用户加入过的所有兄弟连数据
     */
    private void getData() {
        BmobQuery<XiongDiLian> query = new BmobQuery<>();
        Account account = BmobChatUser.getCurrentUser(MyXiongDiLianHomeActivity.this, Account.class);
        query.addWhereEqualTo("members", account);
        // 根据创建时间降序
        query.order("-createdAt");
        query.findObjects(this, new FindListener<XiongDiLian>() {
            @Override
            public void onSuccess(List<XiongDiLian> object) {
                AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
                animAdapter.setAbsListView(mListView);
                mListView.setAdapter(animAdapter);
                mSwipeLayout.setRefreshing(false);
                xiongDiLians.clear();
                xiongDiLians = object;
                if (object == null || object.size() == 0) {
                    mSwipeLayout.setVisibility(View.GONE);
                    myXiongdilianEmptyLl.setVisibility(View.VISIBLE);
                } else {
                    mSwipeLayout.setVisibility(View.VISIBLE);
                    myXiongdilianEmptyLl.setVisibility(View.GONE);
                    mAdapter.setList(xiongDiLians);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int code, String msg) {
                mSwipeLayout.setRefreshing(false);
                showToast(R.string.select_data_error);
            }
        });
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        getData();
    }

    @OnClick({R.id.my_xiongdilian_empty_add_iv, R.id.my_xiongdilian_empty_add_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_xiongdilian_empty_add_iv:
            case R.id.my_xiongdilian_empty_add_tv:
                normalListDialog.show();
                break;
            default:
                break;
        }
    }

}
