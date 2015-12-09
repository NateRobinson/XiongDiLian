package com.gu.baselibrary.baseui;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nate on 2015/9/10.Activity的堆栈管理类
 */
public class ActivityCollections {
    private static final String TAG = ActivityCollections.class.getSimpleName();

    private static ActivityCollections instance = null;
    private static List<Activity> mActivities = new LinkedList<>();

    private ActivityCollections() {

    }

    /**
     * @return 单例模式下的ActivityCollections对象
     */
    public synchronized static ActivityCollections getInstance() {
        if (instance == null) {
            instance = new ActivityCollections();
        }
        return instance;
    }

    /**
     * @return 应用Activity堆栈中目前Activity的数量
     */
    public int getSize() {
        return mActivities.size();
    }

    /**
     * 将一个Activity添加到应用的Activity堆栈中
     *
     * @param activity
     */
    public synchronized void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    /**
     * 将一个Activity移除出应用的Activity堆栈
     *
     * @param activity
     */
    public synchronized void removeActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
        }
    }

    /**
     * 清空应用的Activity堆栈
     */
    public synchronized void clear() {
        for (int i = mActivities.size() - 1; i > -1; i--) {
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
            i = mActivities.size();
        }
    }

    /**
     * 保留最新打开的一个Activity，其余的都从应用的Activity堆栈中清除
     */
    public synchronized void clearToTop() {
        for (int i = mActivities.size() - 2; i > -1; i--) {
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
            i = mActivities.size() - 1;
        }
    }
}
