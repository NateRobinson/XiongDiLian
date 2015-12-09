package com.gu.xiongdilian.activity.friends;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gu.baselibrary.utils.KeyBoardUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.settings.SetMyInfoActivity;
import com.gu.xiongdilian.adapter.friends.AddFriendAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

/**
 * @author nate
 * @ClassName: AddFriendActivity
 * @Description: 添加好友
 * @date 2014-6-5 下午5:26:41
 */
public class AddFriendActivity extends XDLBaseWithCheckLoginActivity implements OnItemClickListener {
    @InjectView(R.id.et_find_name)
    EditText et_find_name;
    @InjectView(R.id.list_search)
    ListView mListView;
    @InjectView(R.id.list_empty_ll)
    LinearLayout list_empty_ll;
    private List<BmobChatUser> users = new ArrayList<>();
    private AddFriendAdapter adapter = null;
    private ProgressDialog progress = null;
    private String searchName = "";

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_add_contact;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.search_friend);
        initXListView();
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(list_empty_ll);
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

    @OnClick({R.id.btn_search})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:// 搜索
                users.clear();
                searchName = et_find_name.getText().toString();
                if (searchName != null && !searchName.equals("")) {
                    initSearchList();
                } else {
                    showToast(R.string.please_enter_user_name);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        BmobChatUser user = adapter.getItem(position - 1);
        Bundle bundle = new Bundle();
        bundle.putString("from", "add");
        bundle.putString("username", user.getUsername());
        go(SetMyInfoActivity.class, bundle);
    }


    /**
     * 初始化XListView
     */
    private void initXListView() {
        adapter = new AddFriendAdapter(this, R.layout.item_add_friend, users);
        mListView.setAdapter(adapter);
    }

    /**
     * 加载搜索结果到listview
     */
    private void initSearchList() {
        KeyBoardUtils.closeKeybord(et_find_name, this);
        progress = new ProgressDialog(AddFriendActivity.this);
        progress.setMessage("正在搜索...");
        progress.setCanceledOnTouchOutside(true);
        progress.show();
        userManager.queryUserByPage(false, 0, searchName, new FindListener<BmobChatUser>() {

            @Override
            public void onError(int arg0, String arg1) {
                BmobLog.i("查询错误:" + arg1);
                if (users != null) {
                    users.clear();
                }
                showToast(R.string.user_is_not_exist);
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                if (CollectionUtils.isNotNull(arg0)) {
                    adapter.addAll(arg0);
                    if (arg0.size() < BRequest.QUERY_LIMIT_COUNT) {
                        showToast(R.string.user_search_finish);
                    } else {
                    }
                } else {
                    if (users != null) {
                        users.clear();
                    }
                    showToast(R.string.user_is_not_exist);
                }
                progress.dismiss();
                // 这样能保证每次查询都是从头开始
            }
        });

    }
}
