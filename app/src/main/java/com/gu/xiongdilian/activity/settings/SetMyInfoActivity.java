package com.gu.xiongdilian.activity.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.utils.SDCardUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.activity.friends.ChatActivity;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.utils.CollectionUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * @author nate
 * @ClassName: SetMyInfoActivity
 * @Description: 个人资料页面
 * @date 2015年6月4日15:06:59
 */
public class SetMyInfoActivity extends XDLBaseWithCheckLoginActivity {
    // 选择相册
    private static final int IMAGE_LOCAL_REQUEST_CODE = 0;
    private static final int UPDATE_INFO = 1;
    private static final int UPDATE_CITY = 2;
    @InjectView(R.id.tv_set_name)
    TextView tv_set_name;
    @InjectView(R.id.set_sex_iv)
    ImageView set_sex_iv;
    @InjectView(R.id.set_sex_name)
    TextView set_sex_name;
    @InjectView(R.id.tv_set_nick)
    TextView tv_set_nick;
    @InjectView(R.id.tv_set_city)
    TextView tv_set_city;
    @InjectView(R.id.iv_set_avator)
    ImageView iv_set_avator;
    @InjectView(R.id.btn_chat)
    Button btn_chat;
    @InjectView(R.id.btn_black)
    Button btn_back;
    @InjectView(R.id.btn_add_friend)
    Button btn_add_friend;
    @InjectView(R.id.layout_black_tips)
    RelativeLayout layout_black_tips;
    @InjectView(R.id.set_set_ll)
    LinearLayout set_set_ll;
    private String from = "";
    private String username = "";
    private Account account = null;
    private boolean isFromMe = false;// 判断是不是来自我的个人资料
    private String path = null;
    private ActionSheetDialog actionSheetDialog = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_set_info;
    }

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

    @Override
    protected void getBundleExtras(Bundle extras) {
        from = extras.getString("from");
        LogUtils.d(TAG_LOG, "from==>" + from);
    }

    @Override
    protected boolean isCustomPendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getCustomPendingTransitionType() {
        return TransitionMode.FADE;
    }

    @Override
    protected void initViewsAndEvents() {
        username = getIntent().getStringExtra("username");
        btn_add_friend.setEnabled(false);
        btn_chat.setEnabled(false);
        btn_back.setEnabled(false);
        if (from.equals("me")) {
            isFromMe = true;
            setCustomToolbar(ToolbarType.WITHBACK, R.string.self_data);
            btn_back.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
            String[] stringItems = {getString(R.string.select_from_phone)};
            actionSheetDialog = new ActionSheetDialog(this, stringItems, null);
            actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        if (!SDCardUtils.isDirExist(MyConfig.HEAD_DIRTH)) {
                            SDCardUtils.createSDDirs(MyConfig.HEAD_DIRTH);
                        }
                        Intent intent = new Intent(SetMyInfoActivity.this, MultiImageSelectorActivity.class);
                        // 是否显示调用相机拍照
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                        startActivityForResult(intent, IMAGE_LOCAL_REQUEST_CODE);
                    }
                    actionSheetDialog.dismiss();
                }
            });
        } else {
            isFromMe = false;
            setCustomToolbar(ToolbarType.WITHBACK, R.string.detail_data);
            // 不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
            btn_chat.setVisibility(View.VISIBLE);
            if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
                if (mApplication.getContactList().containsKey(username)) {// 是好友
                    btn_back.setVisibility(View.VISIBLE);
                } else {
                    btn_back.setVisibility(View.GONE);
                    btn_add_friend.setVisibility(View.VISIBLE);
                }
            } else {// 查看他人
                btn_back.setVisibility(View.VISIBLE);
            }
            initOtherData(username);
        }
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (from.equals("me")) {
            initMeData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_LOCAL_REQUEST_CODE:// 本地修改头像
                Uri uri = null;
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!SDCardUtils.isSDCardEnable()) {
                        showToast(R.string.sdcard_is_useless);
                        return;
                    }
                    // 获取返回的图片列表
                    List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (null == paths || paths.isEmpty()) {
                        return;
                    }
                    for (String str : paths) {
                        uri = Uri.parse("file://" + str);
                    }
                    if (!SDCardUtils.isDirExist(MyConfig.HEAD_DIRTH)) {
                        SDCardUtils.createSDDirs(MyConfig.HEAD_DIRTH);
                    }
                    // 保存图片
                    String filename = account.getObjectId() + ".jpg";
                    path = SDCardUtils.getSDCardPath() + MyConfig.HEAD_DIRTH + File.separator + filename;
                    Uri destination = Uri.fromFile(new File(path));
                    Crop.of(uri, destination).asSquare().start(this);
                } else {
                    showToast(R.string.get_pic_error);
                }
                break;
            case Crop.REQUEST_CROP:// 裁剪头像返回
                if (resultCode == RESULT_OK) {
                    DrawableUtils.disPlayLocRoundCornerImg(iv_set_avator, Crop.getOutput(data).toString());
                    // 上传头像
                    uploadAvatar();
                } else if (resultCode == Crop.RESULT_ERROR) {
                    showToast(R.string.crop_pic_error);
                }
                break;
            case UPDATE_INFO:
                if (resultCode == RESULT_OK) {
                    showToast(R.string.modify_self_data_success);
                    String nickName = userManager.getCurrentUser().getNick();
                    if (TextUtils.isEmpty(nickName)) {
                        tv_set_nick.setText(nickName);
                    }
                }
                break;
            case UPDATE_CITY:
                if (data == null) {
                    return;
                }
                final String city = data.getStringExtra(CityListActivity.CITY_KEY);
                Account account = userManager.getCurrentUser(Account.class);
                account.setCity(city);
                account.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast(R.string.update_address_success);
                        tv_set_city.setText("来自:" + city);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        showToast(R.string.update_address_fail);
                    }
                });
                break;
            default:
                break;

        }
    }

    @OnClick({R.id.btn_chat, R.id.btn_black, R.id.iv_set_avator, R.id.tv_set_nick, R.id.set_set_ll
            , R.id.btn_add_friend, R.id.tv_set_city})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat:// 发起聊天
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", account);
                goThenKill(ChatActivity.class, bundle);
                break;
            case R.id.iv_set_avator:
                if (isFromMe) {
                    actionSheetDialog.isTitleShow(false).show();
                }
                break;
            case R.id.tv_set_nick:
                if (isFromMe) {
                    goForResult(UpdateInfoActivity.class, UPDATE_INFO);
                }
                break;
            case R.id.set_set_ll:// 性别
                if (isFromMe) {
                    showSexChooseDialog();
                }
                break;
            case R.id.tv_set_city:// 城市
                if (isFromMe) {
                    goForResult(CityListActivity.class, UPDATE_CITY);
                }
                break;
            case R.id.btn_black:// 黑名单
                if (!isFromMe) {
                    showBlackDialog(account.getUsername());
                }
                break;
            case R.id.btn_add_friend:// 添加好友
                if (!isFromMe) {
                    addFriend();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 加载自己的资料
     */
    private void initMeData() {
        Account account = userManager.getCurrentUser(Account.class);
        initOtherData(account.getUsername());
    }

    /**
     * 根据name查询其他人的用户资料
     *
     * @param name
     */
    private void initOtherData(String name) {
        userManager.queryUser(name, new FindListener<Account>() {
            @Override
            public void onError(int arg0, String arg1) {
                LogUtils.d(TAG_LOG, "onError onError:" + arg1);
            }

            @Override
            public void onSuccess(List<Account> arg0) {
                if (arg0 != null && arg0.size() > 0) {
                    account = arg0.get(0);
                    if (!isFromMe) {
                        btn_chat.setEnabled(true);
                        btn_back.setEnabled(true);
                        btn_add_friend.setEnabled(true);
                    }
                    updateUser(account);
                } else {
                    LogUtils.d(TAG_LOG, "onSuccess 查无此人");
                }
            }
        });
    }

    /**
     * 更新用户资料
     *
     * @param account
     */
    private void updateUser(Account account) {
        refreshAvatar(account.getAvatar());
        tv_set_name.setText("账号:" + account.getUsername());
        if (isFromMe) {
            if (TextUtils.isEmpty(account.getNick())) {
                tv_set_nick.setText(R.string.no_nick_name_notice);
            } else {
                tv_set_nick.setText("昵称:" + account.getNick());
            }

            if (TextUtils.isEmpty(account.getCity())) {
                tv_set_city.setText(R.string.no_city_name_notice);
            } else {
                tv_set_city.setText("来自:" + account.getCity());
            }
        } else {
            if (TextUtils.isEmpty(account.getNick())) {
                tv_set_nick.setText(R.string.others_no_nick_name_notice);
            } else {
                tv_set_nick.setText("昵称:" + account.getNick());
            }

            if (TextUtils.isEmpty(account.getCity())) {
                tv_set_city.setText(R.string.others_no_city_name_notice);
            } else {
                tv_set_city.setText("来自:" + account.getCity());
            }
        }
        boolean sex = account.isSex();
        set_sex_name.setText(sex ? getString(R.string.man) : getString(R.string.woman));
        set_sex_iv.setBackgroundResource(sex ? R.mipmap.sex_nan_white : R.mipmap.sex_nv_white);
        set_set_ll.setBackgroundResource(sex ? R.drawable.self_data_set_ll_bg_nan : R.drawable.self_data_set_ll_bg_nv);
        // 检测是否为黑名单用户
        if (from.equals("other")) {
            if (BmobDB.create(this).isBlackUser(account.getUsername())) {
                btn_back.setVisibility(View.GONE);
                layout_black_tips.setVisibility(View.VISIBLE);
            } else {
                btn_back.setVisibility(View.VISIBLE);
                layout_black_tips.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新头像 refreshAvatar
     */
    private void refreshAvatar(String avatar) {
        if (!TextUtils.isEmpty(avatar)) {
            DrawableUtils.displayRoundImgOnNet(iv_set_avator, avatar);
        } else {
            DrawableUtils.disPlayLocRoundImg(iv_set_avator, R.mipmap.default_xiongdilian_headimg);
        }
    }


    /**
     * 性别选择的弹出框
     */
    private void showSexChooseDialog() {
        ArrayList<DialogMenuItem> testItems = new ArrayList<>();
        testItems.add(new DialogMenuItem(getString(R.string.man), R.mipmap.iconfont_nan));
        testItems.add(new DialogMenuItem(getString(R.string.woman), R.mipmap.iconfont_nv));
        final NormalListDialog normalListDialog = new NormalListDialog(this, testItems);
        normalListDialog.title(getString(R.string.please_select_sex))//
                .titleBgColor(Color.parseColor("#00BCD4"))//
                .showAnim(new FlipVerticalSwingEnter())//
                .dismissAnim(new FadeExit());
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateInfo(position);
                normalListDialog.dismiss();
            }
        });
        normalListDialog.show();
    }

    /**
     * 修改资料 updateInfo 的性别
     *
     * @param which
     */
    private void updateInfo(int which) {
        final Account account = userManager.getCurrentUser(Account.class);
        if (which == 0) {
            account.setSex(true);
        } else {
            account.setSex(false);
        }
        account.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                showToast(R.string.modify_self_data_success);
                final Account u = userManager.getCurrentUser(Account.class);
                boolean sex = u.isSex();
                set_sex_name.setText(sex ? getString(R.string.man) : getString(R.string.woman));
                set_sex_iv.setBackgroundResource(sex ? R.mipmap.sex_nan_white : R.mipmap.sex_nv_white);
                set_set_ll.setBackgroundResource(sex ? R.drawable.self_data_set_ll_bg_nan : R.drawable.self_data_set_ll_bg_nv);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                showToast(R.string.modify_self_data_fail);
            }
        });
    }

    /**
     * 添加好友请求
     */
    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.add_friend_asking));
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // 发送tag请求
        BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT, account.getObjectId(), new PushListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                showToast(R.string.add_friend_ask_push_success);
            }

            @Override
            public void onFailure(int arg0, final String arg1) {
                progress.dismiss();
                showToast(R.string.add_friend_ask_push_fail);
            }
        });
    }

    /**
     * 显示确定加入黑名单提示框
     *
     * @param username
     */
    private void showBlackDialog(final String username) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content(getString(R.string.add_black_list_notice))
                .showAnim(new FlipVerticalSwingEnter())
                .dismissAnim(new FadeExit())
                .show();
        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                // 添加到黑名单列表
                userManager.addBlack(username, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        showToast(R.string.add_black_list_success);
                        btn_back.setVisibility(View.GONE);
                        layout_black_tips.setVisibility(View.VISIBLE);
                        // 重新设置下内存中保存的好友列表
                        XiongDiLianApplication.getXiongDiLianInstance()
                                .setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this)
                                        .getContactList()));
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        showToast(R.string.add_black_list_fail);
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

    /**
     * 上传头像至服务器
     */
    private void uploadAvatar() {
        showLoadingDialog();
        BmobLog.i("头像地址：" + path);
        BmobProFile.getInstance(this).upload(path, new UploadListener() {
            @Override
            public void onSuccess(String fileName, String url, BmobFile var3) {
                // 图片在服务器的真实url
                // 更新BmobUser对象
                updateUserAvatar(BmobProFile.getInstance(SetMyInfoActivity.this).signURL(fileName,
                        url,
                        MyConfig.ACCESS_KEY,
                        0,
                        null));
                dismissLoadingDialog();
            }

            @Override
            public void onProgress(int ratio) {
                modifyLoadingDialogTitle("进度:" + ratio + "%");
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                dismissLoadingDialog();
                showToast(R.string.upload_headimg_fail);
            }
        });
    }

    /**
     * 更新用户头像url
     *
     * @param url
     */
    private void updateUserAvatar(final String url) {
        Account Account = userManager.getCurrentUser(Account.class);
        Account.setAvatar(url);
        Account.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast(R.string.update_headimg_success);
                // 更新头像
                refreshAvatar(url);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtils.e(TAG_LOG, msg);
                showToast(R.string.update_headimg_fail);
            }
        });
    }
}
