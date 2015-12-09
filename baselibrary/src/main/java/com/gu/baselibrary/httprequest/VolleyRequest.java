package com.gu.baselibrary.httprequest;

import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gu.baselibrary.baseui.BaseApplication;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Nate on 2015/10/9 自封装的volley请求
 */
public class VolleyRequest<T> extends BaseRequest {

    public VolleyRequest(NetDataBackListener listener, int code) {
        this.mListener = listener;
        this.code = code;
    }

    /**
     * 发起GET请求 0--普通接口  1--登录请求接口
     */
    @Override
    public void askGetRequest(String url, String tag, Type type, String cookie) {
        askGetRequest(url, tag, type, 0, cookie);
    }

    /**
     * 发起GET请求 0--普通接口  1--登录请求接口
     */
    public void askGetRequest(String url, String tag, Type type, int requestType, String cookie) {
        String requestUrl = "";
        if (TextUtils.isEmpty(url)) {
            if (null != mListener) {
                mListener.errorData(new VolleyError("url为空"), code);
            }
            return;
        }
        if (requestType == 0) {
            requestUrl = RequestConfig.REQUEST_BASE_URL + url;
        } else if (requestType == 1) {
            requestUrl = RequestConfig.LOGIN_REQUEST_BASE_URL + url;
        }

        GsonGetRequest<T> request = new GsonGetRequest<>(requestUrl, type,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T t) {
                        //获取到值
                        if (null != mListener) {
                            mListener.successData(t, code);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //请求失败的情况
                        if (null != mListener) {
                            mListener.errorData(error, code);
                        }
                    }
                }, cookie);
        BaseApplication.addRequest(request, tag);
    }


    /**
     * 发起POST请求  0--普通接口  1--登录请求接口
     */
    @Override
    public void askPostRequest(String url, String tag, Type type, Map map, String cookie) {
        askPostRequest(url, tag, type, map, 0, cookie);
    }

    /**
     * 发起POST请求 0--普通接口  1--登录请求接口
     */
    public void askPostRequest(String url, final String tag, Type type, final Map map, int requestType, String cookie) {
        String requestUrl = "";
        if (TextUtils.isEmpty(url)) {
            if (null != mListener) {
                mListener.errorData(new VolleyError("url为空"), code);
            }
            return;
        }

        if (requestType == 0) {
            requestUrl = RequestConfig.REQUEST_BASE_URL + url;
        } else if (requestType == 1) {
            requestUrl = RequestConfig.LOGIN_REQUEST_BASE_URL + url;
        }

        GsonPostRequest<T> request = new GsonPostRequest<>(requestUrl, map, type, new Response.Listener<T>() {
            @Override
            public void onResponse(T t) {
                //获取到值
                if (null != mListener) {
                    mListener.successData(t, code);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //请求失败的情况
                if (null != mListener) {
                    mListener.errorData(error, code);
                }
            }
        }, cookie);
        BaseApplication.addRequest(request, tag);
    }
}
