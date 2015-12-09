package com.gu.xiongdilian.pojo;

import cn.bmob.v3.BmobObject;

/**
 * @author nate
 * @ClassName: ChildComment
 * @Description: 帖子子评论
 * @date 2015-5-27 下午7:16:56
 */
public class ChildComment extends BmobObject {
    private static final long serialVersionUID = 1L;

    private String content;// 评论内容

    private Account user;// 评论的用户，Pointer类型，一对一关系

    private Comment comment; // 所评论的评论，这里体现的是一对多的关系，一个子评论只能属于一个主评论

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}
