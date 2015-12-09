package com.gu.xiongdilian.events;

/**
 * Created by Nate on 2015/9/25.
 * 兄弟连帖子页面评论数，收藏数监听的事件
 */
public class PostEvent {
    public static final int ADD_COLLECTION_NUM = 1;
    public static final int DELETE_COLLECTION_NUM = 2;
    public static final int ADD_COMMENT_NUM = 3;

    private int code;

    private String id;

    public PostEvent(int code,String id) {
        this.code = code;
        this.id=id;
    }

    public int getCode() {
        return code;
    }

    public String getId() {
        return id;
    }
}
