package com.gu.xiongdilian.activity.xiongdilian;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.view.PinchImageView;
import com.gu.baselibrary.view.PinchImageViewPager;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.Post;

import java.util.LinkedList;

import butterknife.InjectView;

/**
 * Created by Nate on 2015/12/1.帖子图片查看界面
 */
public class PostPicActivity extends XDLBaseWithCheckLoginActivity {
    @InjectView(R.id.pager)
    PinchImageViewPager pager;
    @InjectView(R.id.position_tv)
    TextView positionTv;
    private Post mPost = null;
    private int position = 0;//初始选中的位置
    private LinkedList<PinchImageView> viewCache = new LinkedList<>();
    private int size;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.post_pic_activity;
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
        mPost = (Post) extras.getSerializable("post");
        position = extras.getInt("position", 0);
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
        setCustomToolbar(ToolbarType.WITHBACK, "图片");

        if (mPost == null || mPost.getImgs().isEmpty()) {
            showToast("数据异常");
            return;
        }

        size = mPost.getImgs().size();

        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return size;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PinchImageView piv;
                if (viewCache.size() > 0) {
                    piv = viewCache.remove();
                    piv.reset();
                } else {
                    piv = new PinchImageView(PostPicActivity.this);
                }
                DrawableUtils.displayNormalImgOnNet(piv, mPost.getImgs().get(position));
                container.addView(piv);
                return piv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                PinchImageView piv = (PinchImageView) object;
                container.removeView(piv);
                viewCache.add(piv);
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                pager.setMainPinchImageView((PinchImageView) object);
            }
        });

        pager.setOnPageChangeListener(new PinchImageViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                positionTv.setText((position + 1) + "/" + size);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pager.setCurrentItem(position);
        positionTv.setText((position + 1) + "/" + size);
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

}
