package com.gu.baselibrary.utils;

import java.util.regex.Pattern;

/**
 * Created by Nate on 2015/9/10. 常用的工具类
 */
public class CommonUtils {

    private CommonUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    /**
     * 判断字符串是不是url
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        Pattern pattern =
                Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        return pattern.matcher(url).matches();
    }

}
