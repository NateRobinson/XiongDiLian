package com.gu.xiongdilian.activity.xiongdilian;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.utils.SDCardUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.XiongDiLianApplication;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.events.PostEvent;
import com.gu.xiongdilian.events.XiongDiLianRefreshEvent;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import de.greenrobot.event.EventBus;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * @author nate
 * @ClassName: CreateXiongDiLianActivity
 * @Description: 创建兄弟连
 * @date 2015-5-25 下午3:08:57
 */
public class CreateXiongDiLianActivity extends XDLBaseWithCheckLoginActivity {
    // 选择相册
    private static final int IMAGE_LOCAL_REQUEST_CODE = 0;
    @InjectView(R.id.xiongdilian_head_iv)
    ImageView xiongDiLianHeadIv;
    @InjectView(R.id.xiongdilian_title_et)
    EditText xiongDiLianTitleEt;
    @InjectView(R.id.xiongdilian_desc_et)
    EditText xiongDiLianDescEt;
    private Account account = null;
    private String filePath = null;
    private String realUrl = "";
    private Animation mAnimation = null;
    private ActionSheetDialog actionSheetDialog = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.create_xiongdilian_layout;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.create);
        DrawableUtils.disPlayLocRoundCornerImg(xiongDiLianHeadIv, R.mipmap.default_xiongdilian_headimg);
        account = BmobChatUser.getCurrentUser(this, Account.class);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.create_xiongdilian_headimg_ani);
        xiongDiLianHeadIv.startAnimation(mAnimation);
        String[] stringItems = {getString(R.string.pop_choose)};
        actionSheetDialog = new ActionSheetDialog(this, stringItems, null);
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (!SDCardUtils.isDirExist(MyConfig.XIONGDILIAN_HEAD_DIRTH)) {
                        SDCardUtils.createSDDirs(MyConfig.XIONGDILIAN_HEAD_DIRTH);
                    }
                    filePath =
                            SDCardUtils.getSDCardPath() + MyConfig.XIONGDILIAN_HEAD_DIRTH + File.separator + account.getObjectId()
                                    + System.currentTimeMillis() + ".png";
                    Intent intent = new Intent(CreateXiongDiLianActivity.this, MultiImageSelectorActivity.class);
                    // 是否显示调用相机拍照
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                    startActivityForResult(intent, IMAGE_LOCAL_REQUEST_CODE);
                }
                actionSheetDialog.dismiss();
            }
        });
    }

    @Override
    protected void doOnNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void doOnNetworkDisConnected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnimation != null) {
            mAnimation.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_LOCAL_REQUEST_CODE:
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
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (null == path || path.isEmpty()) {
                        return;
                    }
                    for (String str : path) {
                        uri = Uri.parse("file://" + str);
                    }
                    Uri destination = Uri.fromFile(new File(filePath));
                    Crop.of(uri, destination).asSquare().start(this);
                } else {
                    showToast(R.string.get_pic_error);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    DrawableUtils.disPlayLocRoundCornerImg(xiongDiLianHeadIv, Crop.getOutput(data).toString());
                } else if (resultCode == Crop.RESULT_ERROR) {
                    showToast(R.string.crop_pic_error);
                }
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.create_xiongdilian_btn, R.id.xiongdilian_head_iv})
    public void bindClick(View v) {
        switch (v.getId()) {
            case R.id.create_xiongdilian_btn:
                if (xiongDiLianTitleEt.getText() == null || xiongDiLianTitleEt.getText().toString().equals("")) {
                    showToast(R.string.create_xiongdilian_name_is_null);
                    return;
                }
                if (xiongDiLianDescEt.getText() == null || xiongDiLianDescEt.getText().toString().equals("")) {
                    showToast(R.string.create_xiongdilian_desc_is_null);
                    return;
                }
                showLoadingDialog();
                if (filePath != null && !filePath.equals("")) {
                    BmobProFile.getInstance(this).upload(filePath, new UploadListener() {
                        @Override
                        public void onSuccess(String fileName, String url, BmobFile var3) {
                            // 图片在服务器的真实url
                            realUrl =
                                    BmobProFile.getInstance(CreateXiongDiLianActivity.this).signURL(fileName,
                                            url,
                                            MyConfig.ACCESS_KEY,
                                            0,
                                            null);
                            saveXiongDiLian();
                        }

                        @Override
                        public void onProgress(int ratio) {
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            showToast(R.string.upload_pic_error);
                            dismissLoadingDialog();
                        }
                    });
                } else {
                    saveXiongDiLian();
                }
                break;
            case R.id.xiongdilian_head_iv:
                actionSheetDialog.isTitleShow(false).show();
                break;
            default:
                break;
        }
    }

    /**
     * 保存创建的兄弟连对象
     */
    private void saveXiongDiLian() {
        final XiongDiLian xiongDiLian = new XiongDiLian();
        xiongDiLian.setTitle(xiongDiLianTitleEt.getText().toString());
        xiongDiLian.setDesc(xiongDiLianDescEt.getText().toString());
        xiongDiLian.setAuthor(account);
        xiongDiLian.setHeadImg(realUrl);
        xiongDiLian.setMemberNum(1);
        xiongDiLian.setPostNum(0);
        // 坐标
        xiongDiLian.setGpsPointer(XiongDiLianApplication.lastPoint);
        // 自己创建的兄弟连，自己将是第一个成员
        BmobRelation relation = new BmobRelation();
        relation.add(account);
        xiongDiLian.setMembers(relation);
        xiongDiLian.save(CreateXiongDiLianActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                dismissLoadingDialog();
                EventBus.getDefault().post(new XiongDiLianRefreshEvent());
                finish();
            }

            @Override
            public void onFailure(int code, String arg0) {
                dismissLoadingDialog();
                showToast(R.string.create_xiongdilian_fail);
            }
        });
    }

}
