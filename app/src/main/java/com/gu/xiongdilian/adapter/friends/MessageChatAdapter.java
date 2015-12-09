package com.gu.xiongdilian.adapter.friends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.friends.ImageBrowserActivity;
import com.gu.xiongdilian.activity.friends.LocationActivity;
import com.gu.xiongdilian.activity.settings.SetMyInfoActivity;
import com.gu.xiongdilian.utils.FaceTextUtils;
import com.gu.xiongdilian.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.DownloadListener;

/**
 * @author nate
 * @ClassName: MessageChatAdapter
 * @Description: 聊天适配器
 * @date 2015年6月5日09:34:02
 */
public class MessageChatAdapter extends BaseAdapter {

    // 8种Item的类型
    // 文本
    private final int TYPE_RECEIVER_TXT = 0;

    private final int TYPE_SEND_TXT = 1;

    // 图片
    private final int TYPE_SEND_IMAGE = 2;

    private final int TYPE_RECEIVER_IMAGE = 3;

    // 位置
    private final int TYPE_SEND_LOCATION = 4;

    private final int TYPE_RECEIVER_LOCATION = 5;

    // 语音
    private final int TYPE_SEND_VOICE = 6;

    private final int TYPE_RECEIVER_VOICE = 7;

    private String currentObjectId = "";

    private List<BmobMsg> list = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    // adapter中的内部点击事件
    public Map<Integer, onInternalClickListener> canClickItem;

    public MessageChatAdapter(Context context, List<BmobMsg> msgList) {
        this.list = msgList;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public BmobMsg getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(BmobMsg e) {
        this.list.add(e);
        notifyDataSetChanged();
    }

    public List<BmobMsg> getList() {
        return list;
    }

    public void setList(List<BmobMsg> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = bindView(position, convertView, parent);
        // 绑定内部点击监听
        addInternalClickListener(convertView, position, list.get(position));
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        BmobMsg msg = list.get(position);
        if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
        } else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        } else {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @SuppressLint("InflateParams")
    private View createViewByType(BmobMsg message, int position) {
        int type = message.getMsgType();
        if (type == BmobConfig.TYPE_IMAGE) {// 图片类型
            return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? mInflater.inflate(R.layout.item_chat_received_image,
                    null)
                    : mInflater.inflate(R.layout.item_chat_sent_image, null);
        } else if (type == BmobConfig.TYPE_LOCATION) {// 位置类型
            return getItemViewType(position) == TYPE_RECEIVER_LOCATION ? mInflater.inflate(R.layout.item_chat_received_location,
                    null)
                    : mInflater.inflate(R.layout.item_chat_sent_location, null);
        } else if (type == BmobConfig.TYPE_VOICE) {// 语音类型
            return getItemViewType(position) == TYPE_RECEIVER_VOICE ? mInflater.inflate(R.layout.item_chat_received_voice,
                    null)
                    : mInflater.inflate(R.layout.item_chat_sent_voice, null);
        } else {// 剩下默认的都是文本
            return getItemViewType(position) == TYPE_RECEIVER_TXT ? mInflater.inflate(R.layout.item_chat_received_message,
                    null)
                    : mInflater.inflate(R.layout.item_chat_sent_message, null);
        }
    }

    private View bindView(final int position, View convertView, ViewGroup parent) {
        final BmobMsg item = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(item, position);
        }
        // 文本类型
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);// 失败重发
        final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);// 发送状态
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
        // 图片
        ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
        final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);// 进度条
        // 位置
        TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);
        // 语音
        final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
        // 语音长度
        final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);

        // 点击头像进入个人资料
        String avatar = item.getBelongAvatar();

        if (!TextUtils.isEmpty(avatar)) {
            DrawableUtils.displayRoundCornerImgOnNet(iv_avatar, avatar);
        } else {
            DrawableUtils.disPlayLocRoundCornerImg(iv_avatar, R.mipmap.default_xiongdilian_headimg);
        }

        iv_avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, SetMyInfoActivity.class);
                if (getItemViewType(position) == TYPE_RECEIVER_TXT || getItemViewType(position) == TYPE_RECEIVER_IMAGE
                        || getItemViewType(position) == TYPE_RECEIVER_LOCATION
                        || getItemViewType(position) == TYPE_RECEIVER_VOICE) {
                    intent.putExtra("from", "other");
                    intent.putExtra("username", item.getBelongUsername());
                } else {
                    intent.putExtra("from", "me");
                }
                mContext.startActivity(intent);
            }
        });
        tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));
        if (getItemViewType(position) == TYPE_SEND_TXT || getItemViewType(position) == TYPE_SEND_LOCATION
                || getItemViewType(position) == TYPE_SEND_VOICE) {// 只有自己发送的消息才有重发机制
            // 状态描述
            if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 发送成功
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已发送");
                }
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// 服务器无响应或者查询失败等原因造成的发送失败，均需要重发
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// 对方已接收到
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_send_status.setVisibility(View.GONE);
                    tv_voice_length.setVisibility(View.VISIBLE);
                } else {
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已送达");
                }
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_START) {// 开始上传
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                    tv_voice_length.setVisibility(View.GONE);
                }
            }
        }
        // 根据类型显示内容
        final String text = item.getContent();
        switch (item.getMsgType()) {
            case BmobConfig.TYPE_TEXT:
                try {
                    SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, text);
                    tv_message.setText(spannableString);
                } catch (Exception e) {
                }
                break;

            case BmobConfig.TYPE_IMAGE:// 图片类
                try {
                    if (text != null && !text.equals("")) {// 发送成功之后存储的图片类型的content和接收到的是不一样的
                        dealWithImage(position, progress_load, iv_fail_resend, tv_send_status, iv_picture, item);
                    }
                    iv_picture.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                            ArrayList<String> photos = new ArrayList<>();
                            photos.add(getImageUrl(item));
                            intent.putStringArrayListExtra("photos", photos);
                            intent.putExtra("position", 0);
                            mContext.startActivity(intent);
                        }
                    });

                } catch (Exception e) {
                }
                break;

            case BmobConfig.TYPE_LOCATION:// 位置信息
                try {
                    if (text != null && !text.equals("")) {
                        String address = text.split("&")[0];
                        final String latitude = text.split("&")[1];// 维度
                        final String longtitude = text.split("&")[2];// 经度
                        tv_location.setText(address);
                        tv_location.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra("type", "scan");
                                intent.putExtra("latitude", Double.parseDouble(latitude));// 维度
                                intent.putExtra("longtitude", Double.parseDouble(longtitude));// 经度
                                mContext.startActivity(intent);
                            }
                        });
                    }
                } catch (Exception e) {

                }
                break;
            case BmobConfig.TYPE_VOICE:// 语音消息
                try {
                    if (text != null && !text.equals("")) {
                        tv_voice_length.setVisibility(View.VISIBLE);
                        String content = item.getContent();
                        if (item.getBelongId().equals(currentObjectId)) {// 发送的消息
                            if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED
                                    || item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 当发送成功或者发送已阅读的时候，则显示语音长度
                                tv_voice_length.setVisibility(View.VISIBLE);
                                String length = content.split("&")[2];
                                tv_voice_length.setText(length + "\''");
                            } else {
                                tv_voice_length.setVisibility(View.INVISIBLE);
                            }
                        } else {// 收到的消息
                            boolean isExists = BmobDownloadManager.checkTargetPathExist(currentObjectId, item);
                            if (!isExists) {// 若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
                                String netUrl = content.split("&")[0];
                                final String length = content.split("&")[1];
                                BmobDownloadManager downloadTask =
                                        new BmobDownloadManager(mContext, item, new DownloadListener() {

                                            @Override
                                            public void onStart() {
                                                progress_load.setVisibility(View.VISIBLE);
                                                tv_voice_length.setVisibility(View.GONE);
                                                iv_voice.setVisibility(View.INVISIBLE);// 只有下载完成才显示播放的按钮
                                            }

                                            @Override
                                            public void onSuccess() {
                                                progress_load.setVisibility(View.GONE);
                                                tv_voice_length.setVisibility(View.VISIBLE);
                                                tv_voice_length.setText(length + "\''");
                                                iv_voice.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onError(String error) {
                                                progress_load.setVisibility(View.GONE);
                                                tv_voice_length.setVisibility(View.GONE);
                                                iv_voice.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                downloadTask.execute(netUrl);
                            } else {
                                String length = content.split("&")[2];
                                tv_voice_length.setText(length + "\''");
                            }
                        }
                    }
                    // 播放语音文件
                    iv_voice.setOnClickListener(new NewRecordPlayClickListener(mContext, item, iv_voice));
                } catch (Exception e) {

                }

                break;
            default:
                break;
        }
        return convertView;
    }

    private void addInternalClickListener(final View itemV, final Integer position, final Object valuesMap) {
        if (canClickItem != null) {
            for (Integer key : canClickItem.keySet()) {
                View inView = itemV.findViewById(key);
                final onInternalClickListener inviewListener = canClickItem.get(key);
                if (inView != null && inviewListener != null) {
                    inView.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {
                            inviewListener.OnClickListener(itemV, v, position,
                                    valuesMap);
                        }
                    });
                }
            }
        }
    }

    public void setOnInViewClickListener(Integer key,
                                         onInternalClickListener onClickListener) {
        if (canClickItem == null)
            canClickItem = new HashMap<>();
        canClickItem.put(key, onClickListener);
    }

    /**
     * 获取图片的地址--
     */
    private String getImageUrl(BmobMsg item) {
        String showUrl = "";
        String text = item.getContent();
        if (item.getBelongId().equals(currentObjectId)) {//
            if (text.contains("&")) {
                showUrl = text.split("&")[0];
            } else {
                showUrl = text;
            }
        } else {// 如果是收到的消息，则需要从网络下载
            showUrl = text;
        }
        return showUrl;
    }

    /**
     * 处理图片
     */
    private void dealWithImage(int position, final ProgressBar progress_load, ImageView iv_fail_resend,
                               TextView tv_send_status, ImageView iv_picture, BmobMsg item) {
        String text = item.getContent();
        if (getItemViewType(position) == TYPE_SEND_IMAGE) {// 发送的消息
            if (item.getStatus() == BmobConfig.STATUS_SEND_START) {
                progress_load.setVisibility(View.VISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已发送");
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            } else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {
                progress_load.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已阅读");
            }
            // 如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
            String showUrl = "";
            if (text.contains("&")) {
                showUrl = text.split("&")[0];
            } else {
                showUrl = text;
            }
            // 为了方便每次都是取本地图片显示
            ImageLoader.getInstance().displayImage(showUrl, iv_picture);
        } else {
            ImageLoader.getInstance().displayImage(text, iv_picture, DrawableUtils.DISPLAY_OPTIONS, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progress_load.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progress_load.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progress_load.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progress_load.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private static class ViewHolder {
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    public interface onInternalClickListener {
        public void OnClickListener(View parentV, View v, Integer position,
                                    Object values);
    }
}
