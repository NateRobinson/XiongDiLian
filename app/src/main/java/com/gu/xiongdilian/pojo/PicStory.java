package com.gu.xiongdilian.pojo;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @ClassName: PicStory
 * @Description: 图片故事实体类
 * @author 追梦工厂
 * @date 2015-5-29 下午1:53:43
 * 
 */
public class PicStory extends BmobObject
{
    private static final long serialVersionUID = 1L;
    
    private String picUrl;
    
    private String picDesc;
    
    private Account author;
    
    private Integer praiseNum;
    
    private Integer viewNum;
    
    private BmobRelation praises;
    
    private BmobGeoPoint gpsPointer;
    
    public String getPicUrl()
    {
        return picUrl;
    }
    
    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }
    
    public String getPicDesc()
    {
        return picDesc;
    }
    
    public void setPicDesc(String picDesc)
    {
        this.picDesc = picDesc;
    }
    
    public Account getAuthor()
    {
        return author;
    }
    
    public void setAuthor(Account author)
    {
        this.author = author;
    }
    
    public Integer getPraiseNum()
    {
        return praiseNum;
    }
    
    public void setPraiseNum(Integer praiseNum)
    {
        this.praiseNum = praiseNum;
    }
    
    public Integer getViewNum()
    {
        return viewNum;
    }
    
    public void setViewNum(Integer viewNum)
    {
        this.viewNum = viewNum;
    }
    
    public BmobRelation getPraises()
    {
        return praises;
    }
    
    public void setPraises(BmobRelation praises)
    {
        this.praises = praises;
    }
    
    public BmobGeoPoint getGpsPointer()
    {
        return gpsPointer;
    }
    
    public void setGpsPointer(BmobGeoPoint gpsPointer)
    {
        this.gpsPointer = gpsPointer;
    }
    
}
