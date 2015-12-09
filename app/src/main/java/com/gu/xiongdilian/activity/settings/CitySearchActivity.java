package com.gu.xiongdilian.activity.settings;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.gu.baselibrary.utils.NetUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.citylist.CitysearchAdapter;
import com.gu.xiongdilian.adapter.citylist.CitysearchNonAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.db.ContactsHelper;
import com.gu.xiongdilian.db.DBManager;
import com.gu.xiongdilian.pojo.citylist.Contacts;

/**
 * Created by Nate on 2015/10/8.
 */
public class CitySearchActivity extends XDLBaseWithCheckLoginActivity {
    private ListView searchresult;
    private EditText input;
    private ImageButton clear, left;
    private SQLiteDatabase database;

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.searchlayout;
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
        searchresult = (ListView) findViewById(R.id.searchresult);
        input = (EditText) findViewById(R.id.input);
        clear = (ImageButton) findViewById(R.id.clear);
        left = (ImageButton) findViewById(R.id.left_title_button);
        database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input.setText("");
            }
        });
        final CitysearchAdapter adapter = new CitysearchAdapter(ContactsHelper.mSearchContacts, this);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //开始初始化数据
        ContactsHelper.getInstance().startLoadContacts();
        searchresult.setAdapter(adapter);
        searchresult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (ContactsHelper.mSearchContacts.size() == 0) {
                    return;
                }
                final Contacts cityModel = ContactsHelper.mSearchContacts.get(position);
                Intent intent = new Intent();
                intent.putExtra(CityListActivity.CITY_KEY, cityModel.getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String curCharacter = s.toString().trim();
                if (TextUtils.isEmpty(curCharacter)) {
                    clear.setVisibility(View.INVISIBLE);
                    ContactsHelper.getInstance().parseQwertyInputSearchContacts(null);
                } else {
                    clear.setVisibility(View.VISIBLE);
                    ContactsHelper.getInstance().parseQwertyInputSearchContacts(curCharacter);
                }
                if (ContactsHelper.mSearchContacts.size() == 0) {
                    searchresult.setAdapter(new CitysearchNonAdapter(CitySearchActivity.this));
                } else {
                    searchresult.setAdapter(adapter);
                    adapter.refresh(ContactsHelper.mSearchContacts);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });
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
}
