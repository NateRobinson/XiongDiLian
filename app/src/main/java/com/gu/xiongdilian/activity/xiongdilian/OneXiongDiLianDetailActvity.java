package com.gu.xiongdilian.activity.xiongdilian;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.view.MTextView;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.xiongdilian.OneXiongDiLianPostsAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.events.PostEvent;
import com.gu.xiongdilian.events.XiongDiLianEvent;
import com.gu.xiongdilian.events.XiongDiLianRefreshEvent;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.Post;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.gu.xiongdilian.utils.TimeUtil;
import com.gu.xiongdilian.view.XListView;
import com.gu.xiongdilian.view.XListView.IXListViewListener;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

/**
 * @author nate
 * @ClassName: OneXiongDiLianDetailActvity
 * @Description: 一个兄弟连细节展示
 * @date 2015-5-27 下午4:27:20
 */
public class OneXiongDiLianDetailActvity extends XDLBaseWithCheckLoginActivity implements IXListViewListener {
    private static final int REFRESH_DELAY_TIME = 800;
    @InjectView(R.id.pull_lv)
    XListView mListView;
    private ImageView headImg;
    private TextView oneXiongdilianName;
    private TextView peopleNumTv;
    private TextView postNumTv;
    private MTextView descTv;
    private Button joinBtn;
    private LinearLayout peopleNumLayout;
    private XiongDiLian mXiongDiLian;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 5; // 每页的数据是5条
    private int curPage = 0; // 当前页的编号，从0开始
    private List<Post> posts = new ArrayList<>();
    private OneXiongDiLianPostsAdapter mAdapter = null;
    //延迟用的handler
    private Handler mHandler = null;
    private View headView = null;
    private boolean isNeedRequest = false;
    private boolean isJoined = false;
    private Account account = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.one_xiongdilian_detail_layout;
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
        LayoutInflater iInflater = LayoutInflater.from(this);
        headView = iInflater.inflate(R.layout.one_xiongdilian_detail_headview_layout, null);
        headImg = (ImageView) headView.findViewById(R.id.one_xiongdilian_headimg);
        peopleNumTv = (TextView) headView.findViewById(R.id.one_people_num_tv);
        postNumTv = (TextView) headView.findViewById(R.id.one_post_num_tv);
        joinBtn = (Button) headView.findViewById(R.id.join_btn);
        descTv = (MTextView) headView.findViewById(R.id.one_xiongdilian_desc);
        peopleNumLayout = (LinearLayout) headView.findViewById(R.id.one_xiongdilian_people_ll);
        oneXiongdilianName = (TextView) headView.findViewById(R.id.one_xiongdilian_name);

        account = BmobChatUser.getCurrentUser(this, Account.class);
        mHandler = new Handler();
        // 上拉加载更多
        mListView.setPullLoadEnable(true);
        // 下拉加载
        mListView.setPullRefreshEnable(true);
        //Listview 在滑动过程中，停止加载图片
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mXiongDiLian = (XiongDiLian) getIntent().getExtras().getSerializable("xiongdilian");
        if (mXiongDiLian != null) {
            refreshUI();
        } else {
            showToast(R.string.get_data_error);
            return;
        }
        mListView.addHeaderView(headView);
        mAdapter = new OneXiongDiLianPostsAdapter(this, R.layout.one_xiongdilian_posts_item_layout, posts);
        // 动画效果
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapter);
        animAdapter.setAbsListView(mListView);
        mListView.setAdapter(animAdapter);
        // 初次会请求第一页
        queryData(0, STATE_REFRESH);
        // 查询该用户是否加入了这个兄弟连
        queryIfJoined();
        setListener();
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    /**
     * EventBus 监听有没有新的帖子创建
     *
     * @param event
     */
    public void onEventMainThread(XiongDiLianEvent event) {
        this.mXiongDiLian = event.getXiongDiLian();
        refreshUI();
        posts.add(event.getPost());
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                long lnsTime = 0;
                long rhsTime = 0;
                //降序排序
                if (lhs.getUpdatedAt() == null) {
                    lnsTime = new Date().getTime();
                } else {
                    lnsTime = TimeUtil.stringToLong(lhs.getUpdatedAt(), TimeUtil.FORMAT_DATE_TIME_SECOND);
                }

                if (rhs.getUpdatedAt() == null) {
                    rhsTime = new Date().getTime();
                } else {
                    rhsTime = TimeUtil.stringToLong(rhs.getUpdatedAt(), TimeUtil.FORMAT_DATE_TIME_SECOND);
                }
                if (lnsTime < rhsTime) {
                    return 1;
                } else if (lnsTime == rhsTime) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Event监听处理，帖子的评论数，赞数有没有发生改变
     *
     * @param event
     */
    public void onEventMainThread(PostEvent event) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getObjectId().equals(event.getId())) {
                Post mPost = posts.get(i);
                switch (event.getCode()) {
                    case PostEvent.ADD_COLLECTION_NUM:
                        mPost.setPariseNum(mPost.getPariseNum() + 1);
                        break;
                    case PostEvent.ADD_COMMENT_NUM:
                        mPost.setCommentNum(mPost.getCommentNum() + 1);
                        break;
                    case PostEvent.DELETE_COLLECTION_NUM:
                        if (mPost.getPariseNum() >= 1) {
                            mPost.setPariseNum(mPost.getPariseNum() - 1);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_xiongdilian_detail_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.post_edit_menu) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("xiongdilian", mXiongDiLian);
            go(MakePostActivity.class, bundle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 查询当前用户是否已经加入了此兄弟连
     */
    private void queryIfJoined() {
        BmobQuery<XiongDiLian> query = new BmobQuery<XiongDiLian>();
        query.addWhereEqualTo("objectId", mXiongDiLian.getObjectId());
        query.addWhereEqualTo("members", account);
        query.count(this, XiongDiLian.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > 0) {
                    isJoined = true;
                    refreshJoinBtn();
                } else {
                    isJoined = false;
                    refreshJoinBtn();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.d(TAG_LOG, "查询是否关注失败,msg==》" + msg);
            }
        });
    }

    /**
     * 刷新加入兄弟连的按钮，根据上面是否已经加入的查询结果
     */
    private void refreshJoinBtn() {
        if (isJoined) {
            joinBtn.setVisibility(View.INVISIBLE);
        } else {
            joinBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置监听
     */
    private void setListener() {
        mListView.setXListViewListener(this);
        peopleNumLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("xiongdilian", mXiongDiLian);
                go(MembersListActivity.class, bundle);
            }
        });
        joinBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobRelation relation = new BmobRelation();
                relation.add(account);
                mXiongDiLian.setMembers(relation);
                mXiongDiLian.update(OneXiongDiLianDetailActvity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        isJoined = true;
                        refreshJoinBtn();
                        mXiongDiLian.increment("memberNum"); // 分数递增1
                        mXiongDiLian.update(OneXiongDiLianDetailActvity.this, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                mXiongDiLian.setMemberNum(mXiongDiLian.getMemberNum() + 1);
                                EventBus.getDefault().post(new XiongDiLianRefreshEvent());
                                refreshUI();
                            }

                            @Override
                            public void onFailure(int arg0, String arg1) {
                                LogUtils.d(TAG_LOG, "人数自增失败,msg==>" + arg1);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        showToast("哎呀,加入失败");
                    }
                });
            }
        });
    }

    /**
     * 刷新一个兄弟连基本数据，头像，成员数，发帖数等等
     */
    private void refreshUI() {
        if (TextUtils.isEmpty(mXiongDiLian.getHeadImg())) {
            DrawableUtils.disPlayLocRoundCornerImg(headImg, R.mipmap.default_xiongdilian_headimg);
        } else {
            DrawableUtils.displayRoundCornerImgOnNet(headImg, mXiongDiLian.getHeadImg());
        }
        oneXiongdilianName.setText(mXiongDiLian.getTitle());
        peopleNumTv.setText(mXiongDiLian.getMemberNum() + "");
        postNumTv.setText(mXiongDiLian.getPostNum() + "");
        setCustomToolbar(ToolbarType.WITHBACK, mXiongDiLian.getTitle());
        SpannableString ss = new SpannableString(mXiongDiLian.getDesc());
        descTv.setMText(ss);
        descTv.invalidate();
    }

    /**
     * 分页获取数据
     */
    private void queryData(final int page, final int actionType) {
        LogUtils.d(TAG_LOG, "pageN:" + page + " limit:" + limit + " actionType:" + actionType);
        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereEqualTo("xiongDiLian", mXiongDiLian);
        query.setLimit(limit); // 设置每页多少条数据
        query.setSkip(page * limit); // 从第几条数据开始，
        query.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
        query.order("-updatedAt");// 按照更新时间降序排列
        query.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> arg0) {
                isNeedRequest = true;
                if (arg0.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        posts.clear();
                    }
                    // 将本次查询的数据添加到bankCards中
                    for (Post post : arg0) {
                        posts.add(post);
                    }
                    mAdapter.notifyDataSetChanged();
                    // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                    curPage++;
                } else if (actionType == STATE_MORE) {
                    showToast(R.string.no_more_post);
                } else if (actionType == STATE_REFRESH) {
                    showToast(R.string.no_post);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                isNeedRequest = true;
                LogUtils.d(TAG_LOG, "查询失败:" + arg1);
            }
        });
    }

    /**
     * 上拉加载，下拉刷新结束之后，需要停止
     */
    private void onLoadFinish() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 下拉刷新(从第一页开始装载数据)
                queryData(0, STATE_REFRESH);
                onLoadFinish();
            }
        }, REFRESH_DELAY_TIME);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNeedRequest) {
                    // 上拉加载更多(加载下一页数据)
                    queryData(curPage, STATE_MORE);
                    isNeedRequest = false;
                }
                onLoadFinish();
            }
        }, REFRESH_DELAY_TIME);
    }

}
