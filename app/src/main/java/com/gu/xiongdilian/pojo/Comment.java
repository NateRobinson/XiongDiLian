package com.gu.xiongdilian.pojo;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * @author nate
 * @ClassName: Comment
 * @Description: 帖子评论
 * @date 2015-5-27 下午7:16:56
 */
public class Comment extends BmobObject {
    private static final long serialVersionUID = 1L;

    private String content;// 评论内容

    private Account user;// 评论的用户，Pointer类型，一对一关系

    private Post post; // 所评论的帖子，这里体现的是一对多的关系，一个评论只能属于一个微博

    private List<ChildComment> childComments; // 子评论

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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<ChildComment> getChildComments() {
        return childComments;
    }

    public void setChildComments(List<ChildComment> childComments) {
        this.childComments = childComments;
    }

}
