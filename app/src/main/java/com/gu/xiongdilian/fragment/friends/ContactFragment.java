package com.gu.xiongdilian.fragment.friends;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.activity.friends.NearPeopleActivity;
import com.gu.xiongdilian.activity.friends.NewFriendActivity;
import com.gu.xiongdilian.activity.settings.SetMyInfoActivity;
import com.gu.xiongdilian.adapter.friends.UserFriendAdapter;
import com.gu.xiongdilian.base.XDLBaseFragment;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.utils.CharacterParser;
import com.gu.xiongdilian.utils.CollectionUtils;
import com.gu.xiongdilian.utils.PinyinComparator;
import com.gu.xiongdilian.view.ClearEditText;
import com.gu.xiongdilian.view.MyLetterView;
import com.gu.xiongdilian.view.MyLetterView.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author nate
 * @ClassName: ContactFragment
 * @Description: 联系人
 * @date 2015年6月4日14:48:04
 */
@SuppressLint("DefaultLocale")
public class ContactFragment extends XDLBaseFragment implements OnItemClickListener, OnItemLongClickListener {
    @InjectView(R.id.et_msg_search)
    ClearEditText mClearEditText;
    @InjectView(R.id.dialog)
    TextView dialog;
    @InjectView(R.id.list_friends)
    ListView list_friends;
    @InjectView(R.id.right_letter)
    MyLetterView right_letter;
    private UserFriendAdapter userAdapter = null;// 好友
    private List<Account> friends = new ArrayList<>();
    private InputMethodManager inputMethodManager = null;
    private CharacterParser characterParser;//汉字转换成拼音的类
    private PinyinComparator pinyinComparator;//根据拼音来排列ListView里面的数据类
    private ImageView iv_msg_tips = null;
    private LinearLayout layout_new = null;// 新朋友
    private LinearLayout layout_near = null;// 附近的人
    private boolean hidden = false;

    /**
     * @return Fragment绑定的布局文件id
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_contacts;
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
        queryMyfriends();
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
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        initListView();
        initRightLetterView();
        initEditText();
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
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Account account = userAdapter.getItem(position - 1);
        // 先进入好友的详细资料页面
        Bundle bundle = new Bundle();
        bundle.putString("from", "other");
        bundle.putString("username", account.getUsername());
        go(SetMyInfoActivity.class, bundle);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Account account = userAdapter.getItem(position - 1);
        showDeleteDialog(account);
        return true;
    }


    /**
     * 初始化输入框
     */
    private void initEditText() {
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     */
    private void filterData(String filterStr) {
        List<Account> filterDateList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = friends;
        } else {
            filterDateList.clear();
            for (Account sortModel : friends) {
                String name = sortModel.getUsername();
                if (name != null) {
                    if (name.indexOf(filterStr.toString()) != -1
                            || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                        filterDateList.add(sortModel);
                    }
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        userAdapter.updateListView(filterDateList);
    }

    /**
     * 为ListView填充数据
     */
    private void filledData(List<BmobChatUser> datas) {
        friends.clear();
        int total = datas.size();
        for (int i = 0; i < total; i++) {
            BmobChatUser Account = datas.get(i);
            Account sortModel = new Account();
            sortModel.setAvatar(Account.getAvatar());
            sortModel.setNick(Account.getNick());
            sortModel.setUsername(Account.getUsername());
            sortModel.setObjectId(Account.getObjectId());
            sortModel.setContacts(Account.getContacts());
            // 汉字转换成拼音
            String username = sortModel.getUsername();
            // 若没有username
            if (username != null) {
                String pinyin = characterParser.getSelling(sortModel.getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
            } else {
                sortModel.setSortLetters("#");
            }
            friends.add(sortModel);
        }
        // 根据a-z进行排序
        Collections.sort(friends, pinyinComparator);
    }

    /**
     * 初始化listview
     */
    private void initListView() {
        RelativeLayout headView = (RelativeLayout) mInflater.inflate(R.layout.include_new_friend, null);
        iv_msg_tips = (ImageView) headView.findViewById(R.id.iv_msg_tips);
        layout_new = (LinearLayout) headView.findViewById(R.id.layout_new);
        layout_near = (LinearLayout) headView.findViewById(R.id.layout_near);
        layout_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Bundle bundle = new Bundle();
                bundle.putString("from", "contact");
                go(NewFriendActivity.class, bundle);
            }
        });
        layout_near.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                go(NearPeopleActivity.class);
            }
        });

        list_friends.addHeaderView(headView);
        userAdapter = new UserFriendAdapter(getActivity(), R.layout.item_user_friend, friends);
        list_friends.setAdapter(userAdapter);
        list_friends.setOnItemClickListener(this);
        list_friends.setOnItemLongClickListener(this);

        list_friends.setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

    }

    /**
     * 长按右侧，展示A-Z
     */
    private void initRightLetterView() {
        right_letter.setTextView(dialog);
        right_letter.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = userAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    list_friends.setSelection(position);
                }
            }
        });
    }

    /**
     * 获取好友列表 queryMyfriends
     */
    private void queryMyfriends() {
        // 是否有新的好友请求
        if (BmobDB.create(getActivity()).hasNewInvite()) {
            iv_msg_tips.setVisibility(View.VISIBLE);
        } else {
            iv_msg_tips.setVisibility(View.GONE);
        }
        // 在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
        // 重新设置下内存中保存的好友列表
        XiongDiLianApplication.getXiongDiLianInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getActivity())
                .getContactList()));

        Map<String, BmobChatUser> users = XiongDiLianApplication.getXiongDiLianInstance().getContactList();
        // 组装新的User
        filledData(CollectionUtils.map2list(users));
        if (userAdapter == null) {
            userAdapter = new UserFriendAdapter(getActivity(), R.layout.item_user_friend, friends);
            list_friends.setAdapter(userAdapter);
        } else {
            userAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 刷新好友
     */
    public void refresh() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    queryMyfriends();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示删除好友的dialog确认框
     *
     * @param account
     */
    private void showDeleteDialog(final Account account) {
        final NormalDialog dialog = new NormalDialog(getActivity());
        dialog.content(getString(R.string.delete_one_friend))
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
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("正在删除...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        userManager.deleteContact(account.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast(getString(R.string.delete_one_friend_success));
                // 删除内存
                XiongDiLianApplication.getXiongDiLianInstance().getContactList().remove(account.getUsername());
                // 更新界面
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        progress.dismiss();
                        userAdapter.remove(account);
                    }
                });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                showToast(getString(R.string.delete_one_friend_fail));
                progress.dismiss();
            }
        });
    }
}