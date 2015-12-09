package com.gu.xiongdilian.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.bmob.im.bean.BmobChatUser;

/**
 * @author nate
 * @ClassName: CollectionUtils
 * @Description: Bmob的集合处理工具类
 * @date 2015-6-5 下午3:29:56
 */
public class CollectionUtils {

    public static boolean isNotNull(Collection<?> collection) {
        if (collection != null && collection.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * list转map 以用户名为key
     */
    public static Map<String, BmobChatUser> list2map(List<BmobChatUser> users) {
        Map<String, BmobChatUser> friends = new HashMap<String, BmobChatUser>();
        for (BmobChatUser user : users) {
            friends.put(user.getUsername(), user);
        }
        return friends;
    }

    /**
     * map转list
     */
    public static List<BmobChatUser> map2list(Map<String, BmobChatUser> maps) {
        List<BmobChatUser> users = new ArrayList<BmobChatUser>();
        Iterator<Entry<String, BmobChatUser>> iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, BmobChatUser> entry = iterator.next();
            users.add(entry.getValue());
        }
        return users;
    }
}