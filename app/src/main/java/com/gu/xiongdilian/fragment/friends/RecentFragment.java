package com.gu.xiongdilian.fragment.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.friends.ChatActivity;
import com.gu.xiongdilian.adapter.friends.MessageRecentAdapter;
import com.gu.xiongdilian.base.XDLBaseFragment;
import com.gu.xiongdilian.view.ClearEditText;

import butterknife.InjectView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

/**
 * @author nate
 * @ClassName: ConversationFragment
 * @Description: 最近会话
 * @date 2015年6月4日16:54:35
 */
public class RecentFragment extends XDLBaseFragment implements OnItemClickListener, OnItemLongClickListener {
    @InjectView(R.id.list)
    ListView listview;
    private MessageRecentAdapter adapter;
    private boolean hidden;

    /**
     * @return Fragment绑定的布局文件id
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_recent;
    }

    /**
     * 是否绑定EventBus
     */
    @Override
    protected boolean isBindEventBus() {
        return false;
    }

    /**
     * 当用户第一次可以看到这个Fragment的时候，我们可以在里面进行一些数据的请求初始化操作
     */
    @Override
    protected void ontUserFirsVisible() {

    }

    /**
     * Fragment用户不可见的时候可以 做的事情 就是onPause中应该做的事情就放这个方法
     */
    @Override
    protected void onUserInvisible() {

    }

    /**
     * Fragment用户可见的时候，可以做的事情
     */
    @Override
    protected void onUserVisible() {

    }

    /**
     * 初始化一些布局和数据
     */
    @Override
    protected void initViewsAndEvents() {
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
        adapter =
                new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity())
                        .queryRecents());
        listview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        BmobRecent recent = adapter.getItem(position);
        showDeleteDialog(recent);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        BmobRecent recent = adapter.getItem(position);
        // 重置未读消息
        BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
        // 组装聊天对象
        BmobChatUser user = new BmobChatUser();
        user.setAvatar(recent.getAvatar());
        user.setNick(recent.getNick());
        user.setUsername(recent.getUserName());
        user.setObjectId(recent.getTargetid());
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        go(ChatActivity.class, bundle);
    }

    /**
     * 展示删除聊天的对话
     *
     * @param recent
     */
    public void showDeleteDialog(final BmobRecent recent) {
        final NormalDialog dialog = new NormalDialog(getActivity());
        dialog.content(getString(R.string.delete_recent_chat))
                .show();
        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                deleteRecent(recent);
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
     * 删除会话 deleteRecent
     */
    private void deleteRecent(BmobRecent recent) {
        adapter.remove(recent);
        BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
        BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
    }

    /**
     * 刷新列表
     */
    public void refresh() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    adapter =
                            new MessageRecentAdapter(getActivity(), R.layout.item_conversation,
                                    BmobDB.create(getActivity()).queryRecents());
                    listview.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
