package com.gu.xiongdilian.adapter.friends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;

import java.util.List;

/**
 * @author nate
 * @ClassName: UserFriendAdapter
 * @Description: 好友列表
 * @date 2015年6月4日15:11:37
 */
@SuppressLint("DefaultLocale")
public class UserFriendAdapter extends MyBaseAdapter<Account> implements SectionIndexer {

    public UserFriendAdapter(Context context, int resource, List<Account> list) {
        super(context, resource, list);
    }

    /**
     * @param viewHolder
     * @param account
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, Account account) {
        String avatar = account.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            viewHolder.setRoundCornerImgFromNet(R.id.img_friend_avatar, avatar, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setResRoundConerImg(R.id.img_friend_avatar, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        }
        viewHolder.setTextView(R.id.tv_friend_name, account.getUsername());
        int position = viewHolder.getPosition();
        TextView alphaTv = viewHolder.getView(R.id.alpha);
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            alphaTv.setVisibility(View.VISIBLE);
            alphaTv.setText(account.getSortLetters());
        } else {
            alphaTv.setVisibility(View.GONE);
        }
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @SuppressLint("DefaultLocale")
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}