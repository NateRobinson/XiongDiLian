package com.gu.xiongdilian.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.adapter.friends.BlackListAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.utils.CollectionUtils;

import butterknife.InjectView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author nate
 * @ClassName: BlackListActivity
 * @Description: 黑名单列表
 * @date 2015年6月4日17:07:42
 */
public class BlackListActivity extends XDLBaseWithCheckLoginActivity implements OnItemClickListener {
    @InjectView(R.id.list_blacklist)
    ListView listview;
    @InjectView(R.id.list_empty_ll)
    LinearLayout list_empty_ll;
    private BlackListAdapter adapter;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_blacklist;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.set_blacklist);
        adapter = new BlackListAdapter(this, R.layout.item_blacklist, BmobDB.create(this).getBlackList());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        BmobChatUser invite = adapter.getItem(arg2);
        showRemoveBlackDialog(arg2, invite);
    }

    /**
     * 显示移除黑名单对话框
     */
    private void showRemoveBlackDialog(final int position, final BmobChatUser user) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("你确定将" + user.getUsername() + "移出黑名单吗?")
                .style(NormalDialog.STYLE_TWO)
                .titleTextSize(23)
                .showAnim(new FlipVerticalSwingEnter())
                .dismissAnim(new FadeExit())
                .show();

        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                adapter.remove(position);
                userManager.removeBlack(user.getUsername(), new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        showToast(R.string.move_from_black_list_success);
                        // 重新设置下内存中保存的好友列表
                        XiongDiLianApplication.getXiongDiLianInstance()
                                .setContactList(CollectionUtils.list2map(BmobDB.create(getApplicationContext())
                                        .getContactList()));
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        showToast(R.string.move_from_black_list_fail);
                    }
                });
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
}