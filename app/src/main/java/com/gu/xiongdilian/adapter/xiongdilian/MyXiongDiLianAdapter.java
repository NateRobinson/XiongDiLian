package com.gu.xiongdilian.adapter.xiongdilian;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.xiongdilian.OneXiongDiLianDetailActvity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.gu.xiongdilian.utils.TimeUtil;

import java.util.List;

/**
 * @author nate
 * @ClassName: MyXiongDiLianAdapter
 * @Description: 我的兄弟连列表adapter
 * @date 2015-5-27 上午9:55:12
 */
public class MyXiongDiLianAdapter extends MyBaseAdapter<XiongDiLian> {
    public MyXiongDiLianAdapter(Context context, int resource, List<XiongDiLian> list) {
        super(context, resource, list);
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, final XiongDiLian xiongDiLian) {
        if (TextUtils.isEmpty(xiongDiLian.getHeadImg())) {
            viewHolder.setResRoundConerImg(R.id.xiongdilian_headimg, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setRoundCornerImgFromNet(R.id.xiongdilian_headimg, xiongDiLian.getHeadImg(), MyConfig.IMG_CORNER_RADIUS);
        }

        if (TextUtils.isEmpty(xiongDiLian.getMemberNum() + "")) {
            xiongDiLian.setMemberNum(0);
        }
        if (TextUtils.isEmpty(xiongDiLian.getPostNum() + "")) {
            xiongDiLian.setPostNum(0);
        }

        viewHolder.setTextView(R.id.xiongdilian_name, xiongDiLian.getTitle())
                .setTextView(R.id.xiongdilian_desc, xiongDiLian.getDesc())
                .setTextView(R.id.xiongdilian_create_time,
                        TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(xiongDiLian.getCreatedAt(),
                                TimeUtil.FORMAT_DATE_TIME_SECOND)))
                .setTextView(R.id.people_num_tv, xiongDiLian.getMemberNum() + "")
                .setTextView(R.id.post_num_tv, xiongDiLian.getPostNum() + "");

        viewHolder.getView(R.id.xiongdilian_item_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("xiongdilian", xiongDiLian);
                go(getContext(), OneXiongDiLianDetailActvity.class, bundle);
            }
        });
    }
}
