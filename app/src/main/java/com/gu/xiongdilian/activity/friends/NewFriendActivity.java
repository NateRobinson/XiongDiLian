package com.gu.xiongdilian.activity.friends;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.home.HomeActivity;
import com.gu.xiongdilian.adapter.friends.NewFriendAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;

import butterknife.InjectView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

/**
 * @author nate
 * @ClassName: NewFriendActivity
 * @Description: 新朋友
 * @date 2015年6月4日17:15:41
 */
public class NewFriendActivity extends XDLBaseWithCheckLoginActivity implements OnItemLongClickListener {
    @InjectView(R.id.list_newfriend)
    ListView listview;
    @InjectView(R.id.list_empty_ll)
    LinearLayout list_empty_ll;
    private NewFriendAdapter adapter = null;
    private String from = "";

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_new_friend;
    }

    /**
     * 是否开启应用的全屏展示
     *
     * @return
     */
    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return false;
    }

    /**
     * 是否绑定了EventBus
     *
     * @return
     */
    @Override
    protected boolean isBindEventBus() {
        return false;
    }

    /**
     * 处理Bundle传参
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {
        from = extras.getString("from");
    }

    /**
     * @return true--自定义页面的切换动画   false--不自定义
     */
    @Override
    protected boolean isCustomPendingTransition() {
        return true;
    }

    /**
     * @return 返回自定义的动画切换方式
     */
    @Override
    protected TransitionMode getCustomPendingTransitionType() {
        return TransitionMode.FADE;
    }

    /**
     * 初始化所有布局和event事件
     */
    @Override
    protected void initViewsAndEvents() {
        setCustomToolbar(ToolbarType.WITHBACK, R.string.new_friends);
        adapter = new NewFriendAdapter(this, R.layout.item_add_friend, BmobDB.create(this).queryBmobInviteList());
        listview.setAdapter(adapter);
        if (from == null) {// 若来自通知栏的点击，则定位到最后一条
            listview.setSelection(adapter.getCount());
        }
        listview.setOnItemLongClickListener(this);
        listview.setEmptyView(list_empty_ll);
    }

    /**
     * 网络连接连起来了
     *
     * @param type
     */
    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    /**
     * 网络连接断开
     */
    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果来自通知栏点击，则在其销毁的时候进入一个新的HomeActivity
        if (from == null) {
            go(HomeActivity.class);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        BmobInvitation invite = adapter.getItem(position);
        showDeleteDialog(position, invite);
        return true;
    }

    /**
     * 展示删除请求的弹出框
     *
     * @param position
     * @param invite
     */
    private void showDeleteDialog(final int position, final BmobInvitation invite) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content(getString(R.string.ask_delete_friend))
                .showAnim(new FlipVerticalSwingEnter())
                .dismissAnim(new FadeExit())
                .show();
        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                deleteInvite(position, invite);
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
     * 删除请求 deleteRecent
     */
    private void deleteInvite(int position, BmobInvitation invite) {
        adapter.remove(position);
        BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
    }
}
