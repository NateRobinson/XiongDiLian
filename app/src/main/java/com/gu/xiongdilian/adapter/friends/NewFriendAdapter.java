package com.gu.xiongdilian.adapter.friends;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.utils.CollectionUtils;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author nate
 * @ClassName: NewFriendAdapter
 * @Description: 新的好友请求
 * @date 2015年6月5日10:21:35
 */
public class NewFriendAdapter extends MyBaseAdapter<BmobInvitation> {

    public NewFriendAdapter(Context context, int resource, List<BmobInvitation> list) {
        super(context, resource, list);
    }

    /**
     * @param viewHolder
     * @param bmobInvitation
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, final BmobInvitation bmobInvitation) {
        final Button btn_add = viewHolder.getView(R.id.btn_add);
        String avatar = bmobInvitation.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            viewHolder.setRoundCornerImgFromNet(R.id.avatar, avatar, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setResRoundConerImg(R.id.avatar, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        }

        viewHolder.setTextView(R.id.name, bmobInvitation.getFromname());

        int status = bmobInvitation.getStatus();
        if (status == BmobConfig.INVITE_ADD_NO_VALIDATION || status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED) {
            btn_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    agressAdd(btn_add, bmobInvitation);
                }
            });
        } else if (status == BmobConfig.INVITE_ADD_AGREE) {
            btn_add.setText("已同意");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(getContext().getResources().getColor(R.color.base_color_text_black));
            btn_add.setEnabled(false);
        }
    }

    /**
     * 添加好友 agressAdd
     */
    private void agressAdd(final Button btn_add, final BmobInvitation msg) {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        try {
            // 同意添加好友
            BmobUserManager.getInstance(getContext()).agreeAddContact(msg, new UpdateListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onSuccess() {
                    progress.dismiss();
                    btn_add.setText("已同意");
                    btn_add.setBackgroundDrawable(null);
                    btn_add.setTextColor(getContext().getResources().getColor(R.color.base_color_text_black));
                    btn_add.setEnabled(false);
                    // 保存到application中方便比较
                    XiongDiLianApplication.getXiongDiLianInstance()
                            .setContactList(CollectionUtils.list2map(BmobDB.create(getContext()).getContactList()));
                }

                @Override
                public void onFailure(int arg0, final String arg1) {
                    progress.dismiss();
                    showToast(R.string.add_new_friend_fail);
                }
            });
        } catch (final Exception e) {
            progress.dismiss();
            showToast(R.string.add_new_friend_fail);
        }
    }
}