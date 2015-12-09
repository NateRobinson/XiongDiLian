package com.gu.xiongdilian.fragment.xiongdilian;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gu.baselibrary.baseui.BaseFragment;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.xiongdilian.SearchXiongDiLianAdapter;
import com.gu.xiongdilian.pojo.XiongDiLian;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * @author nate
 * @ClassName: SearchXiongDiLianByNameFragment
 * @Description: 根据名称搜索兄弟连
 * @date 2015-6-2 下午2:49:58
 */
public class SearchXiongDiLianByNameFragment extends BaseFragment {

    @InjectView(R.id.search_name_et)
    EditText search_name_et;
    @InjectView(R.id.do_search_btn)
    Button do_search_btn;
    @InjectView(R.id.search_result_lv)
    ListView search_result_lv;

    private List<XiongDiLian> result = new ArrayList<>();

    private SearchXiongDiLianAdapter mAdapter = null;

    @Override
    protected void ontUserFirsVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.search_xiongdilian_by_name_layout;
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
        do_search_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_name_et.getText() == null || search_name_et.getText().toString().equals("")) {
                    showToast("输入为空");
                    return;
                }
                queryData();
            }
        });
        mAdapter = new SearchXiongDiLianAdapter(getActivity(), R.layout.search_xiongdilian_lv_item_layout, result);
        search_result_lv.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
    }


    private void queryData() {
        BmobQuery<XiongDiLian> query = new BmobQuery<>();
        query.addWhereContains("title", search_name_et.getText().toString());
        query.findObjects(getActivity(), new FindListener<XiongDiLian>() {
            @Override
            public void onSuccess(List<XiongDiLian> arg0) {
                AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(mAdapter);
                animAdapter.setAbsListView(search_result_lv);
                search_result_lv.setAdapter(animAdapter);
                result.clear();
                result = arg0;
                mAdapter.setList(result);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int arg0, String arg1) {
                showToast("查询失败");
            }
        });
    }

}
