package com.gu.xiongdilian.adapter.friends;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.config.MyConfig;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;

/**
 * @author nate
 * @ClassName: AddFriendAdapter
 * @Description: 查找好友
 * @date 2015年6月6日17:33:00
 */
public class AddFriendAdapter extends MyBaseAdapter<BmobChatUser> {


    public AddFriendAdapter(Context context, int resource, List<BmobChatUser> list) {
        super(context, resource, list);
    }

    /**
     * @param viewHolder
     * @param contract
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, final BmobChatUser contract) {
        Button btn_add = viewHolder.getView(R.id.btn_add);
        String avatar = contract.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            viewHolder.setRoundCornerImgFromNet(R.id.avatar, avatar, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setResRoundConerImg(R.id.avatar, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        }
        viewHolder.setTextView(R.id.name, contract.getUsername());
        btn_add.setText("添加");
        btn_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final ProgressDialog progress = new ProgressDialog(getContext());
                progress.setMessage("正在添加...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                // 发送tag请求
                BmobChatManager.getInstance(getContext()).sendTagMessage(MsgTag.ADD_CONTACT,
                        contract.getObjectId(),
                        new PushListener() {

                            @Override
                            public void onSuccess() {
                                progress.dismiss();
                                showToast(R.string.add_peo_request_send_success);
                            }

                            @Override
                            public void onFailure(int arg0, final String arg1) {
                                progress.dismiss();
                                showToast(R.string.add_peo_request_send_success);
                                LogUtils.d(TAG_LOG, "发送请求失败:" + arg1);
                            }
                        });
            }
        });
    }
}