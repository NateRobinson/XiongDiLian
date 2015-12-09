package com.gu.xiongdilian.activity.friends;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.listener.OnBtnLeftClickL;
import com.flyco.dialog.listener.OnBtnRightClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.utils.SDCardUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.friends.EmoViewPagerAdapter;
import com.gu.xiongdilian.adapter.friends.EmoteAdapter;
import com.gu.xiongdilian.adapter.friends.MessageChatAdapter;
import com.gu.xiongdilian.adapter.friends.NewRecordPlayClickListener;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.pojo.FaceText;
import com.gu.xiongdilian.receiver.MyMessageReceiver;
import com.gu.xiongdilian.utils.FaceTextUtils;
import com.gu.xiongdilian.view.EmoticonsEditText;
import com.gu.xiongdilian.view.XListView;
import com.gu.xiongdilian.view.XListView.IXListViewListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;

/**
 * @author nate
 * @ClassName: ChatActivity
 * @Description: 聊天界面
 * @date 2014-6-3 下午4:33:11
 */
public class ChatActivity extends XDLBaseWithCheckLoginActivity implements IXListViewListener, EventListener {
    /**
     * 存放发送图片的目录
     */
    public static String BMOB_PICTURE_PATH = SDCardUtils.getSDCardPath() + "xiongdilian" + File.separator + "image" + File.separator;

    public static final int REQUESTCODE_TAKE_CAMERA = 1;// 拍照

    public static final int REQUESTCODE_TAKE_LOCAL = 2;// 本地图片

    public static final int REQUESTCODE_TAKE_LOCATION = 3;// 位置

    public static final int NEW_MESSAGE = 4;// 收到消息

    @InjectView(R.id.btn_chat_send)
    Button btn_chat_send;

    @InjectView(R.id.btn_chat_keyboard)
    Button btn_chat_keyboard;

    @InjectView(R.id.btn_speak)
    Button btn_speak;

    @InjectView(R.id.btn_chat_voice)
    Button btn_chat_voice;

    @InjectView(R.id.mListView)
    XListView mListView;

    @InjectView(R.id.edit_user_comment)
    EmoticonsEditText edit_user_comment;

    @InjectView(R.id.layout_more)
    LinearLayout layout_more;

    @InjectView(R.id.layout_emo)
    LinearLayout layout_emo;

    @InjectView(R.id.layout_add)
    LinearLayout layout_add;

    @InjectView(R.id.pager_emo)
    ViewPager pager_emo;

    @InjectView(R.id.layout_record)
    RelativeLayout layout_record;

    @InjectView(R.id.tv_voice_tips)
    TextView tv_voice_tips;

    @InjectView(R.id.iv_record)
    ImageView iv_record;

    private String targetId = "";

    private BmobChatUser targetUser;

    private static int MsgPagerNum;

    private Drawable[] drawable_Anims;// 话筒动画

    private BmobRecordManager recordManager;

    private Toast toast;

    private MessageChatAdapter mAdapter;

    private String localCameraPath = "";// 拍照后得到的图片地址

    private List<FaceText> emos;

    private NewBroadcastReceiver receiver;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_chat;
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
        manager = BmobChatManager.getInstance(this);
        MsgPagerNum = 0;
        // 组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
        // 注册广播接收器
        initNewMessageBroadCast();
        setCustomToolbar(ToolbarType.WITHBACK, "与" + targetUser.getUsername() + "对话");
        initBottomView();
        initXListView();
        initVoiceView();
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

    private void initRecordManager() {
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                BmobLog.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    // 是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    @OnClick({R.id.btn_chat_emo, R.id.btn_chat_add, R.id.btn_chat_voice,
            R.id.btn_chat_keyboard, R.id.btn_chat_send, R.id.tv_camera, R.id.tv_picture, R.id.tv_location})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat_emo:// 点击笑脸图标
                if (layout_more.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_voice:// 语音按钮
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                showEditState(false);
                break;
            case R.id.btn_chat_send:// 发送文本
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    showToast("请输入发送消息!");
                    return;
                }
                boolean isNetConnected = NetUtils.isNetworkAvailable(this);
                if (!isNetConnected) {
                    showToast(R.string.network_tips);
                    return;
                }
                // 组装BmobMessage对象
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                // 默认发送完成，将数据保存到本地消息表和最近会话表中
                manager.sendTextMessage(targetUser, message);
                // 刷新界面
                refreshMessage(message);
                break;
            case R.id.tv_camera:// 拍照
                selectImageFromCamera();
                break;
            case R.id.tv_picture:// 图片
                selectImageFromLocal();
                break;
            case R.id.tv_location:// 位置
                selectLocationFromMap();
                break;
            default:
                break;
        }
    }

    /**
     * @Title: initVoiceView
     * @Description: 初始化语音布局
     */
    private void initVoiceView() {
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 长按说话
     */
    class VoiceTouchListen implements OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!SDCardUtils.isSDCardEnable()) {
                        showToast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                BmobLog.i("voice", "发送语音");
                                sendVoiceMessage(recordManager.getRecordFilePath(targetId), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 发送语音消息
     */
    private void sendVoiceMessage(String local, int length) {
        manager.sendVoiceMessage(targetUser, local, length, new UploadListener() {

            @Override
            public void onStart(BmobMsg msg) {
                refreshMessage(msg);
            }

            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String arg1) {
                LogUtils.d(TAG_LOG, "上传语音失败 -->arg1：" + arg1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 显示录音时间过短的Toast
     */
    @SuppressLint("InflateParams")
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    /**
     * 初始化语音动画资源
     */
    private void initVoiceAnimRes() {
        drawable_Anims =
                new Drawable[]{getResources().getDrawable(R.drawable.chat_icon_voice2),
                        getResources().getDrawable(R.drawable.chat_icon_voice3),
                        getResources().getDrawable(R.drawable.chat_icon_voice4),
                        getResources().getDrawable(R.drawable.chat_icon_voice5),
                        getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    /**
     * 加载消息历史，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId, MsgPagerNum);
        return list;
    }

    /**
     * 界面刷新
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news = MyMessageReceiver.mNewNum;// 有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for (int i = (news - 1); i >= 0; i--) {
                    mAdapter.add(initMsgData().get(size - (i + 1)));// 添加最后一条消息到界面显示
                }
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }

    private void initBottomView() {
        initEmoView();
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edit_user_comment.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    /**
     * 初始化表情布局
     */
    private void initEmoView() {
        emos = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText().insert(start, key);
                        edit_user_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }

    private void initXListView() {
        // 首先不允许加载更多
        mListView.setPullLoadEnable(false);
        // 允许下拉
        mListView.setPullRefreshEnable(true);
        // 设置监听器
        mListView.setXListViewListener(this);
        // 加载数据
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });

        // 重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend, new MessageChatAdapter.onInternalClickListener() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values) {
                // 重发消息
                showResendDialog(parentV, v, values);
            }
        });
    }

    /**
     * 显示重发按钮 showResendDialog
     */
    public void showResendDialog(final View parentV, View v, final Object values) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("确定重发该消息?")
                .showAnim(new FlipVerticalSwingEnter())
                .dismissAnim(new FadeExit())
                .show();
        dialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
            @Override
            public void onBtnLeftClick() {
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
                        || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 图片和语音类型的采用
                    resendFileMsg(parentV, values);
                } else {
                    resendTextMsg(parentV, values);
                }
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
     * 重发文本消息
     */
    private void resendTextMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(targetUser,
                (BmobMsg) values,
                new PushListener() {

                    @Override
                    public void onSuccess() {
                        LogUtils.d(TAG_LOG, "发送成功");
                        ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
                        ((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        LogUtils.d(TAG_LOG, "发送失败:" + arg1);
                        ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 重发图片消息
     */
    private void resendFileMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(targetUser,
                (BmobMsg) values,
                new UploadListener() {
                    @Override
                    public void onStart(BmobMsg msg) {
                    }

                    @Override
                    public void onSuccess() {
                        LogUtils.d(TAG_LOG, "发送成功");
                        ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
                        if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
                            parentV.findViewById(R.id.tv_send_status).setVisibility(View.GONE);
                            parentV.findViewById(R.id.tv_voice_length).setVisibility(View.VISIBLE);
                        } else {
                            parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
                            ((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        LogUtils.d(TAG_LOG, "发送失败" + arg1);
                        ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 启动地图
     */
    private void selectLocationFromMap() {
        Bundle bundle = new Bundle();
        bundle.putString("type", "select");
        goForResult(LocationActivity.class, REQUESTCODE_TAKE_LOCATION, bundle);
    }

    /**
     * 启动相机拍照 startCamera
     */
    public void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, REQUESTCODE_TAKE_CAMERA);
    }

    /**
     * 选择图片
     */
    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUESTCODE_TAKE_LOCAL);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
                    LogUtils.d(TAG_LOG, "本地图片的地址：" + localCameraPath);
                    sendImageMessage(localCameraPath);
                    break;
                case REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                            if (cursor.moveToNext()) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex("_data");
                                String localSelectPath = cursor.getString(columnIndex);
                                cursor.close();
                                if (localSelectPath == null || localSelectPath.equals("null")) {
                                    showToast("找不到您想要的图片");
                                    return;
                                }
                                sendImageMessage(localSelectPath);
                            } else {
                                showToast("找不到您想要的图片");
                            }
                        }
                    }
                    break;
                case REQUESTCODE_TAKE_LOCATION:// 地理位置
                    double latitude = data.getDoubleExtra("x", 0);// 维度
                    double longtitude = data.getDoubleExtra("y", 0);// 经度
                    String address = data.getStringExtra("address");
                    if (address != null && !address.equals("")) {
                        sendLocationMessage(address, latitude, longtitude);
                    } else {
                        showToast("无法获取到您的位置信息!");
                    }
                    break;
            }
        }
    }

    /**
     * 发送位置信息
     */
    private void sendLocationMessage(String address, double latitude, double longtitude) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        // 组装BmobMessage对象
        BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId, address, latitude, longtitude);
        // 默认发送完成，将数据保存到本地消息表和最近会话表中
        manager.sendTextMessage(targetUser, message);
        // 刷新界面
        refreshMessage(message);
    }

    /**
     * 默认先上传本地图片，之后才显示出来 sendImageMessage
     */
    private void sendImageMessage(String local) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        manager.sendImageMessage(targetUser, local, new UploadListener() {
            @Override
            public void onStart(BmobMsg msg) {
                LogUtils.d(TAG_LOG, "开始上传onStart：" + msg.getContent() + ",状态：" + msg.getStatus());
                refreshMessage(msg);
            }

            @Override
            public void onSuccess() {
                LogUtils.d(TAG_LOG, "onSuccess");
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String arg1) {
                LogUtils.d(TAG_LOG, "上传失败 -->arg1：" + arg1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 新消息到达，重新刷新界面
        initOrRefresh();
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        // 清空消息未读数-这个要在刷新之后
        MyMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 监听推送的消息
        // 停止录音
        if (recordManager.isRecording()) {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        // 停止播放录音
        if (NewRecordPlayClickListener.isPlaying && NewRecordPlayClickListener.currentPlayListener != null) {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m =
                        BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(),
                                message.getMsgTime());
                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                mAdapter.add(m);
                // 定位
                mListView.setSelection(mAdapter.getCount() - 1);
                // 取消当前聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };

    private void initNewMessageBroadCast() {
        // 注册接收消息广播
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 新消息广播接收者
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // 收到这个广播的时候，message已经在消息表中，可直接获取
            BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
            if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                return;
            // 添加到当前页面
            mAdapter.add(msg);
            // 定位
            mListView.setSelection(mAdapter.getCount() - 1);
            // 取消当前聊天对象的未读标示
            BmobDB.create(ChatActivity.this).resetUnread(targetId);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    /**
     * 刷新界面
     */
    private void refreshMessage(BmobMsg msg) {
        // 更新界面
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }

    @Override
    public void onMessage(BmobMsg message) {
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected) {
            showToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation invite) {

    }

    @Override
    public void onOffline() {
        showOfflineDialog();
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
        if (conversionId.split("&")[1].equals(targetId)) {
            // 修改界面上指定消息的阅读状态
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId) && msg.getMsgTime().equals(msgTime)) {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onRefresh() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                MsgPagerNum++;
                int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
                BmobLog.i("记录总数：" + total);
                int currents = mAdapter.getCount();
                if (total <= currents) {
                    showToast("聊天记录加载完了哦!");
                } else {
                    List<BmobMsg> msgList = initMsgData();
                    mAdapter.setList(msgList);
                    mListView.setSelection(mAdapter.getCount() - currents - 1);
                }
                mListView.stopRefresh();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_more.getVisibility() == View.VISIBLE) {
                layout_more.setVisibility(View.GONE);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftInputView();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }
}
