package com.gu.xiongdilian.activity.picstory;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.picstory.PicStoryAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.PicStory;
import com.gu.xiongdilian.view.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by Nate on 2015/12/9.图片故事主页面
 */
public class PicStoryMainActivity extends XDLBaseWithCheckLoginActivity {
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
        picStoryMainList.setAdapter(mAdapter);
        picStoryMainList.setEmptyView(listEmptyLl);
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
            go(MakePicStoryActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
