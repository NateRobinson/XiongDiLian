package com.gu.xiongdilian.pojo;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author nate
 * @ClassName: XiongDiLian
 * @Description: 兄弟连最大单元=》帖子=》评论
 * @date 2015-5-25 下午3:27:10
 */
public class XiongDiLian extends BmobObject {
    private static final long serialVersionUID = 1L;

    private String title;// 兄弟连标题

    private String desc;// 兄弟连描述

    private Account author;// 兄弟连的创建者，这里体现的是一对一的关系，一个用户发表一个帖子

    private String headImg;// 兄弟连头像

    private BmobRelation members;// 兄弟连成员

    private Integer memberNum;//兄弟连成员数量

    private Integer postNum;//兄弟连帖子数量

    private BmobGeoPoint gpsPointer;//兄弟连创始位置

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Account getAuthor() {
        return author;
    }

    public void setAuthor(Account author) {
        this.author = author;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public BmobRelation getMembers() {
        return members;
    }

    public void setMembers(BmobRelation members) {
        this.members = members;
    }

    public Integer getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }

    public Integer getPostNum() {
        return postNum;
    }

    public void setPostNum(Integer postNum) {
        this.postNum = postNum;
    }

    public BmobGeoPoint getGpsPointer() {
        return gpsPointer;
    }

    public void setGpsPointer(BmobGeoPoint gpsPointer) {
        this.gpsPointer = gpsPointer;
    }
}
