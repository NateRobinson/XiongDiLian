package com.gu.xiongdilian.activity.picstory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.picstory.PicStoryAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.PicStory;
import com.gu.xiongdilian.view.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Nate on 2015/12/9.图片故事主页面
 */
public class PicStoryMainActivity extends XDLBaseWithCheckLoginActivity implements XListView.IXListViewListener {
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private static final int GO_TO_CREATE_PIC_STORY_CODE = 2;
    @InjectView(R.id.pic_story_main_list)
    XListView picStoryMainList;
    @InjectView(R.id.list_empty_ll)
    LinearLayout listEmptyLl;
    private List<PicStory> picStories = new ArrayList<>();
    private int currentPage = 0;
    private int pageSize = 10;
    private PicStoryAdapter mAdapter = null;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.pic_story_main_activity_layout;
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
        setCustomToolbar(ToolbarType.WITHBACK, "图片故事");
        mAdapter = new PicStoryAdapter(this, R.layout.pic_story_main_activity_item_layout, picStories);
        // 上拉加载更多
        picStoryMainList.setPullLoadEnable(true);
        // 下拉加载
        picStoryMainList.setPullRefreshEnable(true);
        //Listview 在滑动过程中，停止加载图片
        picStoryMainList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        picStoryMainList.setAdapter(mAdapter);
        picStoryMainList.setEmptyView(listEmptyLl);
        // 初次会请求第一页
        queryData(STATE_REFRESH);
        picStoryMainList.setXListViewListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pic_story_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_pic_story_menu) {
            goForResult(MakePicStoryActivity.class, GO_TO_CREATE_PIC_STORY_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        queryData(STATE_REFRESH);
    }

    @Override
    public void onLoadMore() {
        queryData(STATE_MORE);
    }


    /**
     * 分页获取数据
     */
    private void queryData(final int actionType) {
        if (actionType == STATE_REFRESH) {
            currentPage = 0;
        }
        BmobQuery<PicStory> query = new BmobQuery<>();
        query.setLimit(pageSize); // 设置每页多少条数据
        query.setSkip(currentPage * pageSize); // 从第几条数据开始，
        query.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
        query.order("-createdAt");// 按照更新时间降序排列
        query.findObjects(this, new FindListener<PicStory>() {
            @Override
            public void onSuccess(List<PicStory> arg0) {
                if (arg0.size() > 0) {
                    picStoryMainList.stopRefresh();
                    picStoryMainList.stopLoadMore();
                    if (actionType == STATE_REFRESH) {
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        currentPage = 0;
                        picStories.clear();
                    }
                    // 将本次查询的数据添加到bankCards中
                    for (PicStory picStory : arg0) {
                        picStories.add(picStory);
                    }
                    mAdapter.notifyDataSetChanged();
                    // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                    currentPage++;
                } else {
                    if (actionType == STATE_MORE) {
                        picStoryMainList.stopLoadMore();
                        picStoryMainList.stopRefresh();
                        showToast("没有更多数据了");
                    } else if (actionType == STATE_REFRESH) {
                        picStoryMainList.stopLoadMore();
                        picStoryMainList.stopRefresh();
                        showToast("没有数据");
                    }
                }

            }

            @Override
            public void onError(int arg0, String arg1) {
                showToast("哎呀,查询失败了");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GO_TO_CREATE_PIC_STORY_CODE) {
            if (resultCode == RESULT_OK) {
                queryData(STATE_REFRESH);
            }
        }

    }
}
