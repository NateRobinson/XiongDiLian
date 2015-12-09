package com.gu.xiongdilian.activity.xiongdilian;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.fragment.xiongdilian.RandomPushXionDiLianFragment;
import com.gu.xiongdilian.fragment.xiongdilian.SearchXiongDiLianByNameFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * @author nate
 * @ClassName: SearchXionDiLianActivity
 * @Description: 查找兄弟连
 * @date 2015-6-2 上午9:16:17
 */
public class SearchXionDiLianActivity extends XDLBaseWithCheckLoginActivity {

    @InjectView(R.id.xiongdilian_search_viewpager)
    ViewPager viewPager;
    @InjectView(R.id.xiongdilian_search_sliding_tabs)
    TabLayout tabLayout;
    private SimpleFragmentPagerAdapter pagerAdapter = null;
    private static List<String> titleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private RandomPushXionDiLianFragment randomPushXionDiLianFragment = null;
    private SearchXiongDiLianByNameFragment searchXiongDiLianByNameFragment = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.search_xiongdilian_layout;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.search);
        titleList.add(getString(R.string.hot_xiongdilians));
        titleList.add(getString(R.string.search_xiongdilian_by_name));
        randomPushXionDiLianFragment = new RandomPushXionDiLianFragment();
        searchXiongDiLianByNameFragment = new SearchXiongDiLianByNameFragment();
        fragments.add(randomPushXionDiLianFragment);
        fragments.add(searchXiongDiLianByNameFragment);
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    private class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        public SimpleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

    }
}
