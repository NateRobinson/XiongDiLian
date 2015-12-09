package com.gu.xiongdilian.activity.xiongdilian;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.view.MTextView;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.xiongdilian.OnePostAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.events.PostEvent;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.Comment;
import com.gu.xiongdilian.pojo.Post;
import com.gu.xiongdilian.utils.TimeUtil;
import com.gu.xiongdilian.view.XListView;
import com.gu.xiongdilian.view.XListView.IXListViewListener;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

/**
 * @author nate
 * @ClassName: PostDetailActivity
 * @Description: 帖子详情及评论功能页面
 * @date 2015-5-29 下午2:33:37
 */
public class PostDetailActivity extends XDLBaseWithCheckLoginActivity implements IXListViewListener {
    private static final int REFRESH_DELAY_TIME = 800;
    @InjectView(R.id.post_comment_et)
    EditText postCommentEt;
    @InjectView(R.id.pull_lv)
    XListView mListView;
    @InjectView(R.id.add_post_comment_ll)
    LinearLayout postCommentLinearLayout;
    private ImageView postManHeadIv;
    private TextView authorNameTv;
    private TextView postCreateTimeTv;
    private TextView postTitleTv;
    private MTextView postContentTv;
    private LinearLayout postImgLinearLayout;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 5; // 每页的数据是5条
    private int curPage = 0; // 当前页的编号，从0开始
    private List<Comment> comments = new ArrayList<>();
    private Handler mHandler = null;
    private View headView = null;
    private Post mPost = null;
    private OnePostAdapter mAdapter;
    private Account account = null;
    private boolean isCollected = false;
    private boolean collectRequest = false;
    private boolean isPostLinearShow = true;
    private int collectIconId = R.mipmap.collect_no;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.post_detail_layout;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.post);
        LayoutInflater iInflater = LayoutInflater.from(this);
        headView = iInflater.inflate(R.layout.post_detail_head_layout, null);
        postManHeadIv = (ImageView) headView.findViewById(R.id.post_man_head_iv);
        authorNameTv = (TextView) headView.findViewById(R.id.post_author_name_tv);
        postCreateTimeTv = (TextView) headView.findViewById(R.id.post_create_time_tv);
        postTitleTv = (TextView) headView.findViewById(R.id.post_title_tv);
        postContentTv = (MTextView) headView.findViewById(R.id.post_content_tv);
        postImgLinearLayout = (LinearLayout) headView.findViewById(R.id.post_img_ll);

        account = BmobChatUser.getCurrentUser(this, Account.class);
        mHandler = new Handler();
        mListView.setXListViewListener(this);
        // 上拉加载更多
        mListView.setPullLoadEnable(true);
        // 下拉加载
        mListView.setPullRefreshEnable(true);
        // listview在滑动时，停止加载图片
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mPost = (Post) getIntent().getExtras().getSerializable("post");
        if (mPost != null) {
            refreshUI();
        } else {
            showToast(R.string.get_data_error);
            return;
        }
        mListView.addHeaderView(headView);
        mAdapter = new OnePostAdapter(this, R.layout.one_post_item_layout, comments);

        // 动画效果
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapter);
        animAdapter.setAbsListView(mListView);
        mListView.setAdapter(animAdapter);
        queryData(0, STATE_REFRESH);
        // 查询该登录用户是否赞过此帖子
        queryIfPraised();
        refreshPraiseBtn();
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.xiongdilian_post_main, menu);
        MenuItem item = menu.findItem(R.id.add_collect_menu);
        item.setIcon(collectIconId);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_comment_menu) {
            if (isPostLinearShow) {
                postCommentLinearLayout.setVisibility(View.VISIBLE);
                isPostLinearShow = false;
            } else {
                postCommentLinearLayout.setVisibility(View.INVISIBLE);
                isPostLinearShow = true;
            }
            return true;
        } else if (item.getItemId() == R.id.add_collect_menu) {
            if (collectRequest) {
                if (isCollected) {
                    removeParise();
                } else {
                    addParise();
                }
                collectRequest = false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.send_comment_btn})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.send_comment_btn:
                if (postCommentEt.getText() == null || postCommentEt.getText().toString().equals("")) {
                    showToast(R.string.comment_is_null);
                    return;
                }
                sendComment();
                break;
            default:
                break;
        }
    }

    /**
     * 查询该帖子目前用户是否赞了
     */
    private void queryIfPraised() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", mPost.getObjectId());
        // 查询多对多关联要用到BmobPointer
        query.addWhereEqualTo("parises", account);
        query.count(this, Post.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                collectRequest = true;
                if (count > 0) {
                    isCollected = true;
                    refreshPraiseBtn();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                collectRequest = true;
            }
        });
    }

    /**
     * 刷新是否赞的图标
     */
    private void refreshPraiseBtn() {
        if (isCollected) {
            collectIconId = R.mipmap.collected;
            invalidateOptionsMenu();
        } else {
            collectIconId = R.mipmap.collect_no;
            invalidateOptionsMenu();
        }
    }

    /**
     * 根据返回的数据动态的设置headview
     */
    private void refreshUI() {
        if (TextUtils.isEmpty(mPost.getAuthor().getAvatar())) {
            DrawableUtils.disPlayLocRoundCornerImg(postManHeadIv, R.mipmap.default_xiongdilian_headimg);
        } else {
            DrawableUtils.displayRoundCornerImgOnNet(postManHeadIv, mPost.getAuthor().getAvatar());
        }
        authorNameTv.setText(mPost.getAuthor().getUsername());
        postCreateTimeTv.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(mPost.getCreatedAt(),
                TimeUtil.FORMAT_DATE_TIME_SECOND)));
        postTitleTv.setText(mPost.getTitle());
        SpannableString ss = new SpannableString(mPost.getContent());
        postContentTv.setMText(ss);
        postContentTv.invalidate();
        // 动态添加图
        List<String> imgs = mPost.getImgs();
        if (imgs != null && imgs.size() > 0) {
            postImgLinearLayout.removeAllViews();
            for (int i = 0; i < imgs.size(); i++) {
                final int position = i;
                final ImageView imageView = new ImageView(this);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("post", mPost);
                        bundle.putInt("position", position);
                        go(PostPicActivity.class, bundle);
                    }
                });
                DrawableUtils.displayAutoImgOnNet(imageView, imgs.get(i));
                postImgLinearLayout.addView(imageView);
            }
        }
    }

    /**
     * 添加赞
     */
    private void addParise() {
        // 将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
        BmobRelation relation = new BmobRelation();
        // 将当前用户添加到多对多关联中
        relation.add(account);
        // 多对多关联指向`post`的`parises`字段
        mPost.setParises(relation);
        mPost.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                collectRequest = true;
                isCollected = true;
                refreshPraiseBtn();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                collectRequest = true;
                showToast(R.string.post_add_collect_fail);
            }
        });
        // 赞数加1
        mPost.increment("pariseNum"); // 帖子数递增1
        mPost.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtils.d(TAG_LOG, "赞数加1成功");
                EventBus.getDefault().post(new PostEvent(PostEvent.ADD_COLLECTION_NUM, mPost.getObjectId()));
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                LogUtils.d(TAG_LOG, "赞数加1失败");
            }
        });
    }

    /**
     * 删除赞
     */
    private void removeParise() {
        BmobRelation relation = new BmobRelation();
        relation.remove(account);
        mPost.setParises(relation);
        mPost.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                collectRequest = true;
                isCollected = false;
                refreshPraiseBtn();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                collectRequest = true;
                showToast(R.string.post_cancel_collect_fail);
            }
        });
        if (mPost.getPariseNum() >= 1) {
            // 赞数减1
            mPost.increment("pariseNum", -1); // 帖子数递增1
            mPost.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    LogUtils.d(TAG_LOG, "赞数减1成功");
                    EventBus.getDefault().post(new PostEvent(PostEvent.DELETE_COLLECTION_NUM, mPost.getObjectId()));
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    LogUtils.d(TAG_LOG, "赞数减1失败");
                }
            });
        }

    }

    /**
     * 发送评论
     */
    private void sendComment() {
        showLoadingDialog();
        final Comment comment = new Comment();
        comment.setUser(account);
        comment.setPost(mPost);
        comment.setContent(postCommentEt.getText().toString());
        comment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                mPost.increment("commentNum"); // 分数递增1
                mPost.update(PostDetailActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        dismissLoadingDialog();
                        EventBus.getDefault().post(new PostEvent(PostEvent.ADD_COMMENT_NUM, mPost.getObjectId()));
                        postCommentLinearLayout.setVisibility(View.INVISIBLE);
                        isPostLinearShow = true;
                        postCommentEt.setText("");
                        queryData(0, STATE_REFRESH);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        dismissLoadingDialog();
                        LogUtils.d(TAG_LOG, "评论数自增失败...");
                    }
                });
            }

            @Override
            public void onFailure(int code, String arg0) {
                showToast(R.string.post_comment_fail);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 分页获取数据
     *
     * @param page
     * @param actionType
     */
    private void queryData(final int page, final int actionType) {
        LogUtils.d(TAG_LOG, "pageN:" + page + " limit:" + limit + " actionType:" + actionType);
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereEqualTo("post", mPost);
        query.setLimit(limit); // 设置每页多少条数据
        query.setSkip(page * limit); // 从第几条数据开始，
        query.include("user");// 希望在查询评论信息的同时也把发布人的信息查询出来
        query.order("updatedAt");// 按照更新时间降序排列
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> arg0) {
                if (arg0.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                        comments.clear();
                    }
                    // 将本次查询的数据添加到bankCards中
                    for (Comment comment : arg0) {
                        comments.add(comment);
                    }
                    mAdapter.notifyDataSetChanged();
                    // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                    curPage++;
                } else if (actionType == STATE_MORE) {
                    showToast(R.string.post_no_more_comment);
                } else if (actionType == STATE_REFRESH) {
                    showToast(R.string.post_no_comment);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                LogUtils.d(TAG_LOG, "查询失败:" + arg1);
            }
        });
    }

    /**
     * 刷新动作结束
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
                // 上拉加载更多(加载下一页数据)
                queryData(curPage, STATE_MORE);
                onLoadFinish();
            }
        }, REFRESH_DELAY_TIME);
    }

}
