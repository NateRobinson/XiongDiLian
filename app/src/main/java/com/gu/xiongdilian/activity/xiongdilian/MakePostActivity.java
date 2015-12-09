package com.gu.xiongdilian.activity.xiongdilian;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.LogUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.events.XiongDiLianEvent;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.Post;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.gu.xiongdilian.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * @author nate
 * @ClassName: MakePostActivity
 * @Description: 发帖页面
 * @date 2015-5-27 下午8:29:13
 */
public class MakePostActivity extends XDLBaseWithCheckLoginActivity implements OnPageChangeListener {
    private static final int SELECT_IMAGE_CODE = 1;
    @InjectView(R.id.make_post_title_et)
    EditText postTitleEt;
    @InjectView(R.id.make_post_content_et)
    EditText postContentEt;
    @InjectView(R.id.make_post_picView)
    HorizontalScrollView postPicView;
    private List<String> imgs = new ArrayList<>();
    private List<String> realImgs = new ArrayList<>();
    private Account account = null;
    private XiongDiLian mXiongDiLian = null;
    private LinearLayout layoutPicListH = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.make_post_layout;
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
        mXiongDiLian = (XiongDiLian) extras.getSerializable("xiongdilian");
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.make_post);
        layoutPicListH = new LinearLayout(this);// 定义一个新的LinearLayout
        layoutPicListH.setOrientation(LinearLayout.HORIZONTAL);// 设置为水平
        layoutPicListH.setGravity(10);
        account = BmobChatUser.getCurrentUser(this, Account.class);
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SELECT_IMAGE_CODE) {
            if (data != null && data.getExtras() != null) {
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path == null || path.isEmpty()) {
                    showToast("未成功选择图片");
                } else {// 处理你自己的逻辑 ....
                    for (String str : path) {
                        // 防止重复添加
                        if (!imgs.contains(str)) {
                            imgs.add(str);
                        }
                    }
                }
                addView();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.make_xiongdilian_post_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.publish_menu) {
            if (postTitleEt.getText() == null || postTitleEt.getText().toString().equals("")) {
                showToast(R.string.post_title_is_null);
                return true;
            }
            if (postContentEt.getText() == null || postContentEt.getText().toString().equals("")) {
                showToast(R.string.post_content_is_null);
                return true;
            }
            publishPost();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.start_select_photo_iv})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.start_select_photo_iv:
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                // 是否显示调用相机拍照
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                startActivityForResult(intent, SELECT_IMAGE_CODE);
                break;
            default:
                break;
        }
    }

    /**
     * 发布一个帖子图片
     */
    private void publishPost() {
        showLoadingDialog();
        if (imgs != null && imgs.size() > 0) {
            String[] s = new String[imgs.size()];
            s = imgs.toArray(s);
            BmobProFile.getInstance(this).uploadBatch(s, new UploadBatchListener() {
                @Override
                public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] var4) {
                    if (isFinish) {
                        for (int i = 0; i < fileNames.length; i++) {
                            String realUrl =
                                    BmobProFile.getInstance(MakePostActivity.this).signURL(fileNames[i],
                                            urls[i],
                                            MyConfig.ACCESS_KEY,
                                            0,
                                            null);
                            realImgs.add(realUrl);
                        }
                        publishLast();
                    }
                }

                @Override
                public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                    LogUtils.d(TAG_LOG, "MainActivity -onProgress :" + curIndex + "---" + curPercent + "---" + total + "----"
                            + totalPercent);
                    modifyLoadingDialogTitle("进度:" + totalPercent + "%");
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    dismissLoadingDialog();
                    showToast(R.string.multi_pics_upload_error);
                }
            });
        } else {
            publishLast();
        }
    }

    /**
     * 发布一个帖子图片地址及内容
     */
    private void publishLast() {
        final Post post = new Post();
        post.setTitle(postTitleEt.getText().toString());
        post.setContent(postContentEt.getText().toString());
        post.setAuthor(account);
        post.setImgs(realImgs);
        // 坐标
        post.setGpsPointer(XiongDiLianApplication.lastPoint);
        post.setXiongDiLian(mXiongDiLian);
        post.setPariseNum(0);
        post.setCommentNum(0);
        post.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                // 对应的兄弟连的帖子数加1
                mXiongDiLian.increment("postNum"); // 帖子数递增1
                mXiongDiLian.update(MakePostActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        incrementAccountPostNum();
                        //发送一个事件，告诉OneXiongDiLianDetailActivity and MyXiongDiLianHomeActivity
                        //此时需要实时更改一下对应兄弟连的数据：帖子数
                        mXiongDiLian.setPostNum(mXiongDiLian.getPostNum()+1);
                        EventBus.getDefault().post(new XiongDiLianEvent(mXiongDiLian,post));
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        showToast(R.string.xiongdilian_post_num_increate_error);
                        incrementAccountPostNum();
                    }
                });
            }

            @Override
            public void onFailure(int code, String arg0) {
                dismissLoadingDialog();
                showToast(R.string.push_post_error);
            }
        });
    }

    /**
     * 增加用户的发帖数量
     */
    private void incrementAccountPostNum() {
        account.increment("postNum"); // 分数递增1
        account.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                dismissLoadingDialog();
                Intent mIntent = new Intent();
                setResult(RESULT_OK, mIntent);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                dismissLoadingDialog();
                finish();
                showToast(R.string.person_post_num_increate_error);
            }
        });
    }


    /**
     * 根据返回的图片url，添加图片浏览
     */
    @SuppressLint("InflateParams")
    private void addView() {
        layoutPicListH.removeAllViews();
        postPicView.removeAllViews();
        for (int i = 0; i < imgs.size(); i++) {
            // 加载布局
            View view = getLayoutInflater().inflate(R.layout.make_post_pic_viewpager_item_layout, null);
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams((int) (94 * mScreenDensity + 0.5f),
                            (int) (88 * mScreenDensity + 0.5f));
            lp.setMargins(0, 0, (int) (5 * mScreenDensity + 0.5f), 0);
            view.setLayoutParams(lp);
            ImageView img_posting_item = (ImageView) view.findViewById(R.id.img_post_item);
            FrameLayout.LayoutParams lpIMg =
                    new FrameLayout.LayoutParams((int) (88 * mScreenDensity + 0.5f),
                            (int) (88 * mScreenDensity + 0.5f));
            img_posting_item.setLayoutParams(lpIMg);
            DrawableUtils.displayLocImg(img_posting_item, imgs.get(i));
            ImageView img_posting_item_delete = (ImageView) view.findViewById(R.id.img_post_item_delete);
            img_posting_item_delete.setOnClickListener(new ImgDeleteOnClickListener(i));
            layoutPicListH.addView(view);
        }
        postPicView.addView(layoutPicListH);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (postPicView != null) {
            postPicView.invalidate();
        }
    }

    @Override
    public void onPageSelected(int arg0) {

    }

    // 布局删除监听事件
    private class ImgDeleteOnClickListener implements OnClickListener {
        private int position;

        public ImgDeleteOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_post_item_delete:
                    imgs.remove(position);
                    addView();
                    break;
                default:
                    break;
            }
        }
    }
}
