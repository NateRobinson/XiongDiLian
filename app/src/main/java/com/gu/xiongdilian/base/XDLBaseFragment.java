package com.gu.xiongdilian.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import com.gu.baselibrary.baseui.BaseFragment;
import com.gu.xiongdilian.activity.XiongDiLianApplication;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * Created by Nate on 2015/9/18. XiongdiLian的Fragmeng基类
 */
public abstract class XDLBaseFragment extends BaseFragment {
    protected BmobUserManager userManager;
    protected BmobChatManager manager;
    protected XiongDiLianApplication mApplication;
    protected LayoutInflater mInflater;
    private Handler handler = new Handler();

    public void runOnWorkThread(Runnable action) {
        new Thread(action).start();
    }

    public void runOnUiThread(Runnable action) {
        handler.post(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = XiongDiLianApplication.getXiongDiLianInstance();
        userManager = BmobUserManager.getInstance(getActivity());
        manager = BmobChatManager.getInstance(getActivity());
        mInflater = LayoutInflater.from(getActivity());
    }

}
