package com.gu.xiongdilian.events;

import com.gu.xiongdilian.pojo.Post;
import com.gu.xiongdilian.pojo.XiongDiLian;

/**
 * Created by Nate on 2015/9/25.
 * 用来给EventBus的事件---进行兄弟连的成员数，帖子数更新操作
 */
public class XiongDiLianEvent {
    private XiongDiLian xiongDiLian;
    private Post post;

    public XiongDiLianEvent(XiongDiLian xiongDiLian,Post post) {
        this.xiongDiLian = xiongDiLian;
        this.post=post;
    }

    public XiongDiLian getXiongDiLian() {
        return xiongDiLian;
    }

    public Post getPost() {
        return post;
    }
}
