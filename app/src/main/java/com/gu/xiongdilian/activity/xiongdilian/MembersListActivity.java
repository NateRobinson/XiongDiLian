package com.gu.xiongdilian.activity.xiongdilian;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.xiongdilian.MembersAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * @author nate
 * @ClassName: MakePostActivity
 * @Description: 兄弟连成员列表页
 * @date 2015-5-27 下午8:29:13
 */
public class MembersListActivity extends XDLBaseWithCheckLoginActivity implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.my_xiongdilian_swipe)
    SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.my_xiongdilian_lv)
    ListView mListView;
    private MembersAdapter mAdapter = null;
    private List<Account> accounts = new ArrayList<>();
    private XiongDiLian mXiongDiLian = null;
    private Account account = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.xiongdilian_members_layout;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.members_list);
        account = BmobChatUser.getCurrentUser(this, Account.class);
        mXiongDiLian = (XiongDiLian) getIntent().getExtras().getSerializable("xiongdilian");
        mAdapter = new MembersAdapter(this, R.layout.xiongdilian_members_lv_item_layout, accounts, account);
        // listview滑动的时候，停止加载图片
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mSwipeLayout.setColorSchemeResources(R.color.primary, R.color.primary_light);
        mSwipeLayout.setOnRefreshListener(this);
        getData();
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    /**
     * 查询一个兄弟连里的成员数据
     */
    private void getData() {
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<Account> query = new BmobQuery<>();
        // members是XiongDiLian表中的字段，用来存储所有加入这个兄弟连的成员
        query.addWhereRelatedTo("members", new BmobPointer(mXiongDiLian));
        query.findObjects(this, new FindListener<Account>() {
            @Override
            public void onSuccess(List<Account> object) {
                AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
                animAdapter.setAbsListView(mListView);
                mListView.setAdapter(animAdapter);
                mSwipeLayout.setRefreshing(false);
                accounts.clear();
                accounts = object;
                mAdapter.setList(accounts);
                mAdapter.notifyDataSetChanged();
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
}
