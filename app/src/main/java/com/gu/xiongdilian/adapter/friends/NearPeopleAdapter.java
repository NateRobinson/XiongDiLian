package com.gu.xiongdilian.adapter.friends;

import android.content.Context;
import android.text.TextUtils;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.baselibrary.utils.LocationUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;

import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * @author nate
 * @ClassName: BlackListAdapter
 * @Description: 附近的人ListView Adapter
 * @date 2015年6月4日15:05:33
 */
public class NearPeopleAdapter extends MyBaseAdapter<Account> {

    public NearPeopleAdapter(Context context, int resource, List<Account> list) {
        super(context, resource, list);
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, Account account) {
        String avatar = account.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            viewHolder.setRoundCornerImgFromNet(R.id.iv_avatar, avatar, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setResRoundConerImg(R.id.iv_avatar, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        }
        BmobGeoPoint location = account.getLocation();
        String currentLat = XiongDiLianApplication.getXiongDiLianInstance().getLatitude();
        String currentLong = XiongDiLianApplication.getXiongDiLianInstance().getLongtitude();
        if (location != null && !currentLat.equals("") && !currentLong.equals("")) {
            double distance =
                    LocationUtils.DistanceOfTwoPoints(Double.parseDouble(currentLat),
                            Double.parseDouble(currentLong),
                            account.getLocation().getLatitude(),
                            account.getLocation().getLongitude());
            viewHolder.setTextView(R.id.tv_distance, String.valueOf(distance) + "米");
        } else {
            viewHolder.setTextView(R.id.tv_distance, "未知");
        }
        viewHolder.setTextView(R.id.tv_name, account.getUsername()).setTextView(R.id.tv_logintime, "最近登录时间:" + account.getUpdatedAt());
    }
}
