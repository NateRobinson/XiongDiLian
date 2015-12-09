package com.gu.baselibrary.netstatus;

import com.gu.baselibrary.utils.NetUtils;

/**
 * Created by Nate on 2015/9/9. 网络状况发生改变的时候的回调接口
 */
public interface NetChangeCallBack {
    void onNetConnected(NetUtils.NetType type);//网络连接

    void onNetDisConnected();//网络断开
}
