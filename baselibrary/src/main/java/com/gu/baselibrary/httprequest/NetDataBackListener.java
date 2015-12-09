package com.gu.baselibrary.httprequest;

import com.android.volley.VolleyError;

/**
 * Created by Nate on 2015/10/9. 网络请求回调返回接口
 */
public interface NetDataBackListener {

    /**
     * 成功的数据
     *
     * @param data
     */
    void successData(Object data, int code);

    /**
     * 失败的数据
     *
     * @param error
     */
    void errorData(VolleyError error, int code);
}
