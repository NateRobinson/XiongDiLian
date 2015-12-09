package com.gu.xiongdilian.adapter.friends;

import android.content.Context;
import android.text.TextUtils;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.config.MyConfig;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;

/**
 * 黑名单页面的Listview Adapter
 */
public class BlackListAdapter extends MyBaseAdapter<BmobChatUser> {

    public BlackListAdapter(Context context, int resource, List<BmobChatUser> list) {
        super(context, resource, list);
    }

    /**
     * @param viewHolder
     * @param bmobChatUser
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, BmobChatUser bmobChatUser) {
        viewHolder.setTextView(R.id.tv_friend_name, bmobChatUser.getUsername());
        String avatar = bmobChatUser.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            viewHolder.setRoundCornerImgFromNet(R.id.img_friend_avatar, avatar, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setResRoundConerImg(R.id.img_friend_avatar, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        }
    }
}