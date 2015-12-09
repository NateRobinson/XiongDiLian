package com.gu.xiongdilian.activity.picstory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.PicStory;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * @author 追梦工厂
 * @ClassName: MakePicStoryActivity
 * @Description: 发送图片故事页面
 * @date 2015-5-31 下午8:57:30
 */
public class MakePicStoryActivity extends XDLBaseWithCheckLoginActivity {
    private static final int SELECT_IMAGE_CODE = 1;

    @InjectView(R.id.pic_img)
    private ImageView mPicImg;

    @InjectView(R.id.add_pic_story_tv)
    private TextView mAddPicStoryTv;

    @InjectView(R.id.add_pic_story_desc_et)
    private EditText mAddPicStoryDescEt;

    private String picUrl = null;

    private String picRealUrl = null;

    private Account account = null;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.make_pic_story_layout;
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
        return false;
    }

    /**
     * @return 返回自定义的动画切换方式
     */
    @Override
    protected TransitionMode getCustomPendingTransitionType() {
        return null;
    }

    /**
     * 初始化所有布局和event事件
     */
    @Override
    protected void initViewsAndEvents() {
        setCustomToolbar(ToolbarType.WITHBACK, "发布图片");
        account = BmobChatUser.getCurrentUser(this, Account.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.make_xiongdilian_post_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.publish_menu) {
            if (TextUtils.isEmpty(picUrl)) {
                showToast("请选择一张图片");
                return true;
            }
            if (mAddPicStoryDescEt.getText() == null || mAddPicStoryDescEt.getText().toString().equals("")) {
                showToast("图片故事描述不能为空");
                return true;
            }
            addPicStory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.add_pic_story_tv, R.id.pic_img})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.pic_img:
            case R.id.add_pic_story_tv:
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SELECT_IMAGE_CODE) {
            if (data != null && data.getExtras() != null) {
                @SuppressWarnings("unchecked")
                List<String> photos = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (photos == null || photos.isEmpty()) {
                    showToast("未成功选择图片");
                } else {
                    picUrl = photos.get(0);
                    setPic();
                }
            }
        }
    }

    /**
     * 设置图片
     */
    private void setPic() {
        if (!TextUtils.isEmpty(picUrl)) {
            mPicImg.setVisibility(View.VISIBLE);
            mAddPicStoryTv.setVisibility(View.GONE);
            DrawableUtils.displayLocImg(mPicImg, picUrl);
        } else {
            mPicImg.setVisibility(View.GONE);
            mAddPicStoryTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 增长图片故事数量
     */
    private void incrementAccountPicStoryNum() {
        account.increment("picStoryNum");
        account.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                dismissLoadingDialog();
                finish();
                showToast("个人图片故事数自增失败");
            }
        });
    }

    // 发表图片故事
    private void addPicStory() {
        showLoadingDialog();
        BmobProFile.getInstance(this).upload(picUrl, new UploadListener() {
            @Override
            public void onSuccess(String fileName, String url, BmobFile bmobFile) {
                // 图片在服务器的真实url
                picRealUrl =
                        BmobProFile.getInstance(MakePicStoryActivity.this).signURL(fileName,
                                url,
                                MyConfig.ACCESS_KEY,
                                0,
                                null);
                Log.d("guxuewu", picRealUrl);
                final PicStory picStory = new PicStory();
                picStory.setPicUrl(picRealUrl);
                picStory.setPicDesc(mAddPicStoryDescEt.getText().toString());
                picStory.setAuthor(account);
                picStory.setPraiseNum(0);
                picStory.setViewNum(1);
                picStory.setPraiseNum(0);
                // 坐标
                picStory.setGpsPointer(XiongDiLianApplication.lastPoint);
                picStory.save(MakePicStoryActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        incrementAccountPicStoryNum();
                    }

                    @Override
                    public void onFailure(int code, String arg0) {
                        dismissLoadingDialog();
                        showToast("创建失败...");
                    }
                });
            }

            @Override
            public void onProgress(int ratio) {
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                showToast("头像上传出错：" + errormsg);
                dismissLoadingDialog();
            }
        });
    }
}
