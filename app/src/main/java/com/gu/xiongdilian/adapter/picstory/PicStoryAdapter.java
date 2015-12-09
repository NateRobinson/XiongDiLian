package com.gu.xiongdilian.adapter.picstory;

import android.content.Context;
import android.text.TextUtils;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.pojo.PicStory;

import java.util.List;

/**
 * Created by Nate on 2015/12/9.
 */
public class PicStoryAdapter extends MyBaseAdapter<PicStory> {
    public PicStoryAdapter(Context context, int resource, List<PicStory> list) {
        super(context, resource, list);
    }

    /**
     * @param viewHolder
     * @param picStory
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, PicStory picStory) {
//        if (TextUtils.isEmpty(picStory.getAuthor().getAvatar())) {
//            DrawableUtils.disPlayLocRoundCornerImg(headImg, R.mipmap.default_xiongdilian_headimg);
//        } else {
//            DrawableUtils.displayRoundCornerImgOnNet(headImg, picStory.getAuthor().getAvatar());
//        }
    }
}
