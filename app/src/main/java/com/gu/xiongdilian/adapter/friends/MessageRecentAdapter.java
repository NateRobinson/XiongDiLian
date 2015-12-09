package com.gu.xiongdilian.adapter.friends;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filterable;
import android.widget.TextView;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.utils.FaceTextUtils;
import com.gu.xiongdilian.utils.TimeUtil;

import java.util.List;

import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

/**
 * 最近聊天页面的Listview适配器
 */
public class MessageRecentAdapter extends MyBaseAdapter<BmobRecent> implements Filterable {


    public MessageRecentAdapter(Context context, int resource, List<BmobRecent> list) {
        super(context, resource, list);
    }

    /**
     * @param viewHolder
     * @param bmobRecent
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, BmobRecent bmobRecent) {
        //填充数据
        String avatar = bmobRecent.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            viewHolder.setRoundCornerImgFromNet(R.id.iv_recent_avatar, avatar, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setResRoundConerImg(R.id.iv_recent_avatar, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        }

        viewHolder.setTextView(R.id.tv_recent_name, bmobRecent.getUserName());
        viewHolder.setTextView(R.id.tv_recent_time, TimeUtil.getChatTime(bmobRecent.getTime()));

        //显示内容
        if (bmobRecent.getType() == BmobConfig.TYPE_TEXT) {
            SpannableString spannableString = FaceTextUtils.toSpannableString(getContext(), bmobRecent.getMessage());
            viewHolder.setTextView(R.id.tv_recent_msg, spannableString);
        } else if (bmobRecent.getType() == BmobConfig.TYPE_IMAGE) {
            viewHolder.setTextView(R.id.tv_recent_msg, R.string.recent_chat_pic);
        } else if (bmobRecent.getType() == BmobConfig.TYPE_LOCATION) {
            String all = bmobRecent.getMessage();
            if (all != null && !all.equals("")) {//位置类型的信息组装格式：地理位置&维度&经度
                String address = all.split("&")[0];
                viewHolder.setTextView(R.id.tv_recent_msg, "[位置]" + address);
            }
        } else if (bmobRecent.getType() == BmobConfig.TYPE_VOICE) {
            viewHolder.setTextView(R.id.tv_recent_msg, R.string.recent_chat_audio);
        }

        int num = BmobDB.create(getContext()).getUnreadCount(bmobRecent.getTargetid());
        TextView tv_recent_unread = viewHolder.getView(R.id.tv_recent_unread);
        if (num > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            if (num < 100) {
                tv_recent_unread.setText(num + "");
            } else {
                tv_recent_unread.setText("99+");
            }
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }
    }

}