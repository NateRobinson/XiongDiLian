package com.gu.xiongdilian.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author nate
 * @ClassName: CustomViewPager
 * @Description: 自定义ViewPager
 * @date 2015-6-5 下午5:02:09
 */
public class CustomViewPager extends ViewPager {

    private boolean mIsEnable = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsEnable) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsEnable) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public void setAdapter(PagerAdapter arg0) {
        super.setAdapter(arg0);
    }

    public void setAdapter(PagerAdapter arg0, int index) {
        super.setAdapter(arg0);
        setCurrentItem(index, false);
    }

    public void setEnableTouchScroll(boolean isEnable) {
        mIsEnable = isEnable;
    }
}
