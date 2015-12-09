package com.gu.xiongdilian.pojo;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * @author nate
 * @ClassName: Account
 * @Description: 用户实例
 * @date 2015-5-24 下午11:08:51
 */
public class Account extends BmobChatUser {
    private static final long serialVersionUID = 1L;

    private Integer level;// 等级

    private Integer age;// 年龄

    private Integer friendNum;// 好友数

    private Integer postNum;// 发帖数

    private Integer picStoryNum;// 图片故事数

    private String sortLetters;// 显示数据拼音的首字母

    private boolean sex;// 性别-true-男

    private String city;

    private BmobGeoPoint location; // 地理坐标

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public Integer getFriendNum() {
        return friendNum;
    }

    public void setFriendNum(Integer friendNum) {
        this.friendNum = friendNum;
    }

    public Integer getPostNum() {
        return postNum;
    }

    public void setPostNum(Integer postNum) {
        this.postNum = postNum;
    }

    public Integer getPicStoryNum() {
        return picStoryNum;
    }

    public void setPicStoryNum(Integer picStoryNum) {
        this.picStoryNum = picStoryNum;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
