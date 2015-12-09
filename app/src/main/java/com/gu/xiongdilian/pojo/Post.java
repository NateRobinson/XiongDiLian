package com.gu.xiongdilian.pojo;

import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.XiongDiLian;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author nate
 * @ClassName: Post
 * @Description: 兄弟连里的帖子
 * @date 2015-5-25 下午3:24:06
 */
public class Post extends BmobObject {
    private static final long serialVersionUID = 1L;

    private String title;// 帖子标题

    private String content;// 帖子内容

    private Account author;// 帖子的发布者，这里体现的是一对一的关系，一个用户发表一个帖子

    private List<String> imgs;// 帖子图片

    private BmobRelation parises;// 多对多关系：用于存储喜欢该帖子的所有用户

    private XiongDiLian xiongDiLian;// 帖子所属的兄弟连 一对多

    private BmobGeoPoint gpsPointer;// 帖子创始位置

    private Integer pariseNum;// 赞的数量

    private Integer commentNum;// 评论的数量

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public BmobRelation getParises() {
        return parises;
    }

    public void setParises(BmobRelation parises) {
        this.parises = parises;
    }

    public XiongDiLian getXiongDiLian() {
        return xiongDiLian;
    }

    public void setXiongDiLian(XiongDiLian xiongDiLian) {
        this.xiongDiLian = xiongDiLian;
    }

    public BmobGeoPoint getGpsPointer() {
        return gpsPointer;
    }

    public void setGpsPointer(BmobGeoPoint gpsPointer) {
        this.gpsPointer = gpsPointer;
    }

    public Integer getPariseNum() {
        return pariseNum;
    }

    public void setPariseNum(Integer pariseNum) {
        this.pariseNum = pariseNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

}
