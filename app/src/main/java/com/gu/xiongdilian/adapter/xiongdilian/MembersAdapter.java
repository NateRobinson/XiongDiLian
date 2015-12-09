package com.gu.xiongdilian.adapter.xiongdilian;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author nate
 * @ClassName: MembersAdapter
 * @Description: 兄弟连成员列表Adapter
 * @date 2015-6-1 下午3:53:27
 */
public class MembersAdapter extends MyBaseAdapter<Account> {

    private Account mAccount;

    public MembersAdapter(Context context, int resource, List<Account> list, Account account) {
        super(context, resource, list);
        this.mAccount = account;
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, final Account account) {
        if (TextUtils.isEmpty(account.getAvatar())) {
            viewHolder.setResRoundConerImg(R.id.member_head_iv, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setRoundCornerImgFromNet(R.id.member_head_iv, account.getAvatar(), MyConfig.IMG_CORNER_RADIUS);
        }
        if (TextUtils.isEmpty(account.getNick())) {
            viewHolder.setTextView(R.id.member_desc, "未设置状态");
        } else {
            viewHolder.setTextView(R.id.member_desc, account.getNick());
        }
        viewHolder.setTextView(R.id.member_name, account.getUsername());
        viewHolder.setTextView(R.id.people_friend_num_tv, account.getFriendNum() + "");
        viewHolder.setTextView(R.id.post_num_tv, account.getPostNum() + "");
        viewHolder.setTextView(R.id.pic_story_num_tv, account.getPicStoryNum() + "");
        Button addAndDelBtn = viewHolder.getView(R.id.add_del_friend_iv);

        if (mAccount.getObjectId().endsWith(account.getObjectId())) {
            addAndDelBtn.setBackgroundResource(R.drawable.member_list_self_btn_bg);
            addAndDelBtn.setText("自己");
        } else {
            if (XiongDiLianApplication.getXiongDiLianInstance().getContactList().containsKey(account.getUsername())) {
                addAndDelBtn.setBackgroundResource(R.drawable.del_friend_btn_bg);
                addAndDelBtn.setText("删除");
                addAndDelBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(account);
                    }
                });
            } else {
                addAndDelBtn.setBackgroundResource(R.drawable.add_friend_btn_bg);
                addAndDelBtn.setText("添加");
                addAndDelBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progress = new ProgressDialog(getContext());
                        progress.setMessage("正在添加...");
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();
                        Log.d("guxuewu", "account.getUsername()==>" + account.getUsername());
                        // 发送tag请求
                        BmobChatManager.getInstance(getContext()).sendTagMessage(MsgTag.ADD_CONTACT,
                                account.getObjectId(),
                                new PushListener() {
                                    @Override
                                    public void onSuccess() {
                                        progress.dismiss();
                                        showToast("发送请求成功，等待对方验证!");
                                    }

                                    @Override
                                    public void onFailure(int arg0, final String arg1) {
                                        progress.dismiss();
                                        showToast("发送请求失败，请重新添加!");
                                    }
                                });

                    }
                });
            }
        }

    }

    public void showDeleteDialog(final Account account) {
        final NormalDialog dialog = new NormalDialog(getContext());
        dialog.content("确定删除联系人?")
                .showAnim(new FlipVerticalSwingEnter())
                .dismissAnim(new FadeExit())
                .show();

        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                deleteContact(account);
                dialog.dismiss();
            }
        });

        dialog.setOnBtnRightClickL(new OnBtnRightClickL() {
            @Override
            public void onBtnRightClick() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 删除联系人 deleteContact
     */
    private void deleteContact(final Account account) {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("正在删除...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUserManager.getInstance(getContext()).deleteContact(account.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                showToast("删除成功");
                // 删除内存
                XiongDiLianApplication.getXiongDiLianInstance().getContactList().remove(account.getUsername());
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                showToast("删除失败：" + arg1);
                progress.dismiss();
            }
        });
    }

}
