package com.gu.baselibrary.netstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;

import java.util.ArrayList;

/**
 * Created by Nate on 2015/9/10. 网络状态监听广播
 */
public class NetStatusReceiver extends BroadcastReceiver {
    public final static String CUSTOM_NET_CHANGE_ACTION =
            "CUSTOM_NET_CHANGE_ACTION";

    private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private final static String TAG = NetStatusReceiver.class.getSimpleName();

    private static boolean isNetAvailable = false;

    private static NetUtils.NetType mNetType;

    private static ArrayList<NetChangeCallBack> mNetChangeCallBacks = new ArrayList<>();

    private static BroadcastReceiver mBroadcastReceiver;

    private static BroadcastReceiver getReceiver() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new NetStatusReceiver();
        }
        return mBroadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mBroadcastReceiver = NetStatusReceiver.this;
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
                || intent.getAction().equalsIgnoreCase(CUSTOM_NET_CHANGE_ACTION)) {
            if (!NetUtils.isNetworkAvailable(context)) {
                LogUtils.i(TAG, "<--- network disconnected --->");
                isNetAvailable = false;
            } else {
                LogUtils.i(TAG, "<--- network connected --->");
                isNetAvailable = true;
                mNetType = NetUtils.getAPNType(context);
            }
            notifyCallBack();
        }
    }

    /**
     * 刷新回调接口
     */
    private void notifyCallBack() {
        if (!mNetChangeCallBacks.isEmpty()) {
            int size = mNetChangeCallBacks.size();
            for (int i = 0; i < size; i++) {
                NetChangeCallBack callBack = mNetChangeCallBacks.get(i);
                if (callBack != null) {
                    if (isNetworkAvailable()) {
                        callBack.onNetConnected(mNetType);
                    } else {
                        callBack.onNetDisConnected();
                    }
                }
            }
        }
    }

    /**
     * 注册网络变化监听的广播
     *
     * @param mContext
     */
    public static void registerNetworkStateReceiver(Context mContext) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    /**
     * 注销网络变化监听的广播
     *
     * @param mContext
     */
    public static void unRegisterNetworkStateReceiver(Context mContext) {
        if (mBroadcastReceiver != null) {
            try {
                mContext.getApplicationContext().unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
                LogUtils.d(TAG, e.getMessage());
            }
        }
    }

    /**
     * 注册一个回调监听
     *
     * @param callBack
     */
    public static void registerNetChangeCallBack(NetChangeCallBack callBack) {
        if (mNetChangeCallBacks == null) {
            mNetChangeCallBacks = new ArrayList<>();
        }
        mNetChangeCallBacks.add(callBack);
    }

    /**
     * 注销一个回调监听
     *
     * @param callBack
     */
    public static void removeRegisterNetChangeCallBack(NetChangeCallBack callBack) {
        if (mNetChangeCallBacks != null) {
            if (mNetChangeCallBacks.contains(callBack)) {
                mNetChangeCallBacks.remove(callBack);
            }
        }
    }

    /**
     * @return 返回当前网络是否可以用
     */
    public static boolean isNetworkAvailable() {
        return isNetAvailable;
    }

    /**
     * @return 返回当前的网络类型
     */
    public static NetUtils.NetType getAPNType() {
        return mNetType;
    }
}
