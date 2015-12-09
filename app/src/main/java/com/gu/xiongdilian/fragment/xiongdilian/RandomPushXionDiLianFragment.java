package com.gu.xiongdilian.fragment.xiongdilian;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gu.baselibrary.baseui.BaseFragment;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.xiongdilian.OneXiongDiLianDetailActvity;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.gu.xiongdilian.view.KeywordsFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * @author nate
 * @ClassName: RandomPushXionDiLianFragment
 * @Description: 随机推荐兄弟连
 * @date 2015-6-5 下午3:25:23
 */
public class RandomPushXionDiLianFragment extends BaseFragment implements OnClickListener {
    @InjectView(R.id.frameLayout1)
    KeywordsFlow keywordsFlow;
    @InjectView(R.id.up_page_btn)
    Button up_page_btn;
    @InjectView(R.id.down_page_btn)
    Button down_page_btn;
    private List<XiongDiLian> mList = new ArrayList<>();
    private static final int UP_PAGE = 0;// 上一页
    private static final int NEXT_PAGE = 1;// 下一页
    private static final int FIRST_PAGE = 2;// 第一页
    private int limit = 10; // 每页的数据是5条
    private int curPage = 0; // 当前页的编号，从0开始
    private boolean canNext = true;
    private boolean isCanRequest = true;

    @Override
    protected void ontUserFirsVisible() {
        queryData(curPage, FIRST_PAGE);
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.random_push_xiondidlian_layout;
    }

    /**
     * 是否绑定EventBus
     */
    @Override
    protected boolean isBindEventBus() {
        return false;
    }

    @Override
    protected void initViewsAndEvents() {
        up_page_btn.setOnClickListener(this);
        down_page_btn.setOnClickListener(this);
        keywordsFlow.setDuration(800l);
        keywordsFlow.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == up_page_btn) {
            if (curPage > 0) {
                curPage--;
                keywordsFlow.rubKeywords();
                queryData(curPage, UP_PAGE);
            } else {
                showToast("已经是第一页");
            }
        } else if (v == down_page_btn) {
            if (isCanRequest) {
                if (canNext) {
                    curPage++;
                    keywordsFlow.rubKeywords();
                    queryData(curPage, NEXT_PAGE);
                    isCanRequest = false;
                } else {
                    showToast("已经是最后一页");
                }
            }
        } else if (v instanceof TextView) {
            String keyword = ((TextView) v).getText().toString();
            Bundle bundle = new Bundle();
            bundle.putSerializable("xiongdilian", getClickXiongDiLian(keyword));
            goThenKill(OneXiongDiLianDetailActvity.class, bundle);
        }

    }

    /**
     * 生成随机展示
     *
     * @param keywordsFlow
     * @param list
     */
    private void feedKeywordsFlow(KeywordsFlow keywordsFlow, List<XiongDiLian> list) {
        Random random = new Random();
        for (int i = 0; i < KeywordsFlow.MAX; i++) {
            int ran = random.nextInt(list.size());
            String tmp = list.get(ran).getTitle();
            keywordsFlow.feedKeyword(tmp);
        }
    }

    /**
     * 分页获取数据
     */
    private void queryData(final int page, final int actionType) {
        Log.d("guxuewu", "pageN:" + page + " limit:" + limit + " actionType:" + actionType);
        BmobQuery<XiongDiLian> query = new BmobQuery<XiongDiLian>();
        query.setLimit(limit); // 设置每页多少条数据
        query.setSkip(page * limit); // 从第几条数据开始，
        query.order("-updatedAt");// 按照更新时间降序排列
        query.findObjects(getActivity(), new FindListener<XiongDiLian>() {
            @Override
            public void onSuccess(List<XiongDiLian> arg0) {
                if (arg0.size() > 0) {
                    if (actionType == NEXT_PAGE) {
                        isCanRequest = true;
                    }
                    if (actionType == UP_PAGE) {
                        canNext = true;
                    }
                    mList.clear();
                    // 将本次查询的数据添加到bankCards中
                    for (XiongDiLian xiongDiLian : arg0) {
                        mList.add(xiongDiLian);
                    }
                    feedKeywordsFlow(keywordsFlow, mList);
                    if (actionType == FIRST_PAGE || actionType == NEXT_PAGE) {
                        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
                    } else {
                        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
                    }
                } else {
                    showToast("没有更多兄弟连了");
                    if (actionType == NEXT_PAGE) {
                        canNext = false;
                        isCanRequest = true;
                        curPage--;
                    }
                    if (actionType == UP_PAGE) {
                        canNext = true;
                    }
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                showToast("查询失败:" + arg1);
                if (actionType == NEXT_PAGE) {
                    isCanRequest = true;
                }
            }
        });
    }

    private XiongDiLian getClickXiongDiLian(String text) {
        for (XiongDiLian xiongDiLian : mList) {
            if (xiongDiLian.getTitle().equals(text)) {
                return xiongDiLian;
            }
        }
        return null;
    }
}
