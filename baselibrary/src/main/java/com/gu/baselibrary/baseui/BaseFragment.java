package com.gu.baselibrary.baseui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gu.baselibrary.view.LoadingDialog;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

/**
 * Created by Nate on 2015/9/10. Fragment的基类 这里使用的是v4的Fragment
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Log tag
     */
    protected static String TAG_LOG = null;

    /**
     * 屏幕信息
     */
    protected int mScreenWidth = 0;

    protected int mScreenHeight = 0;

    protected float mScreenDensity = 0.0f;

    /**
     * 上下文
     */
    protected Context mContext = null;

    private boolean isFirstResume = true;

    private boolean isFirstVisible = true;

    private boolean isFirstInvisible = true;

    private boolean isPrepared = false;

    protected LoadingDialog dialog = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG_LOG = this.getClass().getSimpleName();
        if (isBindEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentViewLayoutID() != 0) {
            return inflater.inflate(getContentViewLayoutID(), null);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        // 初始化屏幕相关数据
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        initViewsAndEvents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBindEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    /**
     * 当Fragment第一次从可见变不可见的时候，可以做的事情，不推荐在里面做处理
     */
    private void onFirstUserInvisible() {

    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            ontUserFirsVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 跳转一个Activity
     *
     * @param clazz
     */
    protected void go(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * 跳转一个Activity then kill
     *
     * @param clazz
     */
    protected void goThenKill(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * 跳转一个Activity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void go(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转一个Activity with bundle then kill
     *
     * @param clazz
     * @param bundle
     */
    protected void goThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        getActivity().finish();
    }


    /**
     * 跳转一个Activity并等待返回结果
     *
     * @param clazz
     * @param requestCode
     */
    protected void goForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转一个Activity并等待返回结果 with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void goForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * show toast
     *
     * @param msg
     */
    protected void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Snackbar.make(((Activity) mContext).getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * 展示一个等待框
     */
    protected void showShapeLoadingDialog() {
        if (null == dialog) {
            dialog = new LoadingDialog(getActivity());
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    /**
     * 取消一个等待框
     */
    protected void dissmissShapLoadingDialog() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    /**
     * 成功类型的弹出框
     *
     * @param title
     * @param content
     */
    protected void showSweetDialogSuccess(String title, String content) {
        SweetAlertDialog sd = new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE);
        sd.setTitleText(title);
        sd.setConfirmText("好的");
        sd.setContentText(content);
        // 可以按返回取消
        sd.setCancelable(true);
        // 可以点击外部取消
        sd.setCanceledOnTouchOutside(true);
        sd.show();
    }

    /**
     * 错误提示类型的弹出框
     *
     * @param title
     * @param content
     */
    protected void showSweetDialogFail(String title, String content) {
        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE).setTitleText(title)
                .setContentText(content)
                .setConfirmText("好的")
                .show();
    }

    /**
     * @return Fragment绑定的布局文件id
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 是否绑定EventBus
     */
    protected abstract boolean isBindEventBus();

    /**
     * 当用户第一次可以看到这个Fragment的时候，我们可以在里面进行一些数据的请求初始化操作
     */
    protected abstract void ontUserFirsVisible();

    /**
     * Fragment用户不可见的时候可以 做的事情 就是onPause中应该做的事情就放这个方法
     */
    protected abstract void onUserInvisible();

    /**
     * Fragment用户可见的时候，可以做的事情
     */
    protected abstract void onUserVisible();

    /**
     * 初始化一些布局和数据
     */
    protected abstract void initViewsAndEvents();


}
