package com.gu.xiongdilian.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.gu.baselibrary.baseui.ActivityCollections;
import com.gu.baselibrary.baseui.BaseApplication;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.SPUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.utils.CollectionUtils;
import com.gu.xiongdilian.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.GetServerTimeListener;

/**
 * @author nate
 * @ClassName: XiongDiApplication
 * @Description: 应用启动初始化
 * @date 2015-5-24 下午11:14:45
 */
public class XiongDiLianApplication extends BaseApplication {

    public static XiongDiLianApplication xiongDiLianApplication = null;

    public LocationClient mLocationClient = null;

    public MyLocationListener mMyLocationListener = null;

    public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度

    public static String SERVER_TIME = null;

    private String longtitude = ""; //经度

    private String latitude = ""; //纬度

    private NotificationManager mNotificationManager = null;

    private MediaPlayer mMediaPlayer = null;

    private Map<String, BmobChatUser> contactList = new HashMap<>(); //缓存在内存中的好友列表

    @Override
    public void onCreate() {
        super.onCreate();
        xiongDiLianApplication = this;
        // Buggly初始化
        CrashReport.initCrashReport(this.getApplicationContext(), MyConfig.APP_ID, MyConfig.IS_DEBUG); // 初始化SDK
        Bmob.initialize(getApplicationContext(), MyConfig.APPLICATION_ID);
        // BmobIM SDK初始化--只需要这一段代码即可完成初始化
        BmobChat.getInstance(this).init(MyConfig.APPLICATION_ID);
        BmobConfiguration config = new BmobConfiguration.Builder(this).customExternalCacheDir("XiongDiLian").build();
        BmobPro.getInstance(this).initConfig(config);
        init();
        getServerTime();
    }

    /**
     * @return BaseApplication
     */
    public static XiongDiLianApplication getXiongDiLianInstance() {
        return xiongDiLianApplication;
    }

    private void getServerTime() {
        Bmob.getServerTime(this, new GetServerTimeListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onSuccess(long time) {
                SimpleDateFormat formatter = new SimpleDateFormat(TimeUtil.FORMAT_DATE_TIME_SECOND);
                SERVER_TIME = formatter.format(new Date(time * 1000L));
            }

            @Override
            public void onFailure(int code, String msg) {
                SERVER_TIME = null;
            }
        });
    }


    private void init() {
        //mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 若用户登陆过，则先从好友数据库中取出好友list存入内存中
        if (BmobUserManager.getInstance(getApplicationContext()).getCurrentUser() != null) {
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
        }
        initBaidu();
    }

    /**
     * 初始化百度相关sdk initBaidumap
     */
    private void initBaidu() {
        // 初始化地图Sdk
        SDKInitializer.initialize(this);
        // 初始化定位sdk
        initBaiduLocClient();
    }

    /**
     * 初始化百度定位sdk
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            LogUtils.d(XiongDiLianApplication.class.getSimpleName(), "latitude:" + latitude + "***" + "longtitude:" + longtitude);
            if (lastPoint != null) {
                if (lastPoint.getLatitude() == location.getLatitude()
                        && lastPoint.getLongitude() == location.getLongitude()) {
                    mLocationClient.stop();
                    return;
                }
            }
            lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }

    /**
     * @return 单例模式返回NotificationManager对象
     */
    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    /**
     * @return 单例模式返回MediaPlayer对象
     */
    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }

    /**
     * 获取经度
     */
    public String getLongtitude() {
        longtitude = (String) SPUtils.get(getApplicationContext(), MyConfig.PREF_LONGTITUDE, "");
        return longtitude;
    }

    /**
     * 设置经度
     */
    public void setLongtitude(String lon) {
        SPUtils.put(getApplicationContext(), MyConfig.PREF_LONGTITUDE, lon);
        longtitude = lon;
    }

    /**
     * 获取纬度
     */
    public String getLatitude() {
        latitude = (String) SPUtils.get(getApplicationContext(), MyConfig.PREF_LATITUDE, "");
        return latitude;
    }

    /**
     * 设置维度
     */
    public void setLatitude(String lat) {
        SPUtils.put(getApplicationContext(), MyConfig.PREF_LATITUDE, lat);
        latitude = lat;
    }

    /**
     * 获取内存中好友user list
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }


    /**
     * 不退出账户的退出
     */
    public void logout() {
        ActivityCollections.getInstance().clear();
        setContactList(null);
        System.gc();
        // TODO: 2015/9/11
        //MobclickAgent.onKillProcess(this);
        android.os.Process.killProcess(android.os.Process.myPid());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void bmobLogout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
        ActivityCollections.getInstance().clear();
        setContactList(null);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

}
