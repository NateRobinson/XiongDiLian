package com.gu.baselibrary.httprequest;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Nate on 2015/10/9. 请求的基类，后面可以有子类
 */
public abstract class BaseRequest {
    protected NetDataBackListener mListener;
    protected int code;

    /**
     * 发起GET请求
     */
    public abstract void askGetRequest(String url, String tag, Type type, String cookie);

    /**
     * 发起POST请求
     */
    public abstract void askPostRequest(String url, String tag, Type type, Map map, String cookie);

}
