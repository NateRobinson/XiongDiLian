package com.gu.xiongdilian.adapter.xiongdilian;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.Comment;
import com.gu.xiongdilian.utils.TimeUtil;

import java.util.List;

public class OnePostAdapter extends MyBaseAdapter<Comment> {

    public OnePostAdapter(Context context, int resource, List<Comment> list) {
        super(context, resource, list);
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, Comment comment) {
        if (viewHolder.getPosition() == 0) {
            viewHolder.getView(R.id.during_line).setVisibility(View.GONE);
        } else {
            viewHolder.getView(R.id.during_line).setVisibility(View.VISIBLE);
        }
        Account author = comment.getUser();
        if (TextUtils.isEmpty(author.getAvatar())) {
            viewHolder.setResRoundConerImg(R.id.comment_man_headimg, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setRoundCornerImgFromNet(R.id.comment_man_headimg, author.getAvatar(), MyConfig.IMG_CORNER_RADIUS);

        }

        viewHolder.setTextView(R.id.main_comment_tv, comment.getContent());
        viewHolder.setTextView(R.id.comment_man_name, comment.getUser().getUsername());
        viewHolder.setTextView(R.id.comment_turn_num, "第" + (viewHolder.getPosition() + 1) + "楼");
        viewHolder.setTextView(R.id.comment_create_time,
                TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(comment.getCreatedAt(),
                        TimeUtil.FORMAT_DATE_TIME_SECOND)));
        viewHolder.setMTextView(R.id.main_comment_tv, comment.getContent());
    }

}
