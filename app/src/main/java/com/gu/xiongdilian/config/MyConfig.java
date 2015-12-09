package com.gu.xiongdilian.config;

import java.io.File;

/**
 * @author nate
 * @ClassName: MyConfig
 * @Description: 常量管理类
 * @date 2015-5-25 下午10:08:18
 */
public class MyConfig {

    public static final String SHARED_KEY_NOTIFY = "shared_key_notify";//是否提示消息key

    public static final String SHARED_KEY_VOICE = "shared_key_sound";//是否提示声音key

    public static final String SHARED_KEY_VIBRATE = "shared_key_vibrate";//是否震动key

    public static final String PREF_LONGTITUDE = "longtitude";// 经度

    public static final String PREF_LATITUDE = "latitude";// 经度

    public static final String ACCESS_KEY = "1c863bb992807b80fad089cd7e0b0e8f";

    public static final String APPLICATION_ID = "fa0b61cc20910983ddef06cd2787562f";

    public static final String APP_ID = "900003801"; // 上Bugly(bugly.qq.com)注册产品获取的AppId

    public static boolean IS_DEBUG = false; // true代表App处于调试阶段，false代表App发布阶段

    public static final String HEAD_DIRTH = "xiongdilian" + File.separator + "heads";

    public static final String XIONGDILIAN_HEAD_DIRTH = "xiongdilian" + File.separator + "xiongdilianheads";

    public static final String SEND_PICS = "xiongdilian" + File.separator + "image";

    public static final float IMG_CORNER_RADIUS = 40f;
}
