package com.gu.xiongdilian.activity.friends;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.view.CustomViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;

/**
 * @author nate
 * @ClassName: ImageBrowserActivity
 * @Description: 图片浏览
 * @date 2015年6月5日09:50:48
 */
public class ImageBrowserActivity extends XDLBaseWithCheckLoginActivity implements OnPageChangeListener {
    @InjectView(R.id.pagerview)
    CustomViewPager mSvpPager;
    private ImageBrowserAdapter mAdapter;
    private int mPosition;
    private ArrayList<String> mPhotos;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_showpicture;
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
        mPhotos = extras.getStringArrayList("photos");
        mPosition = extras.getInt("position", 0);
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.pic_browser);
        mAdapter = new ImageBrowserAdapter(this);
        mSvpPager.setAdapter(mAdapter);
        mSvpPager.setCurrentItem(mPosition, false);
        mSvpPager.setOnPageChangeListener(this);
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
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        mPosition = arg0;
    }

    private class ImageBrowserAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImageBrowserAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mPhotos != null ? mPhotos.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(R.layout.item_show_picture, container, false);
            final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.photoview);
            final ProgressBar progress = (ProgressBar) imageLayout.findViewById(R.id.progress);
            final String imgUrl = mPhotos.get(position);
            ImageLoader.getInstance().displayImage(imgUrl,
                    photoView,
                    DrawableUtils.DISPLAY_OPTIONS,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            progress.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            progress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            progress.setVisibility(View.GONE);
                        }
                    });

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}