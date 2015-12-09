package com.gu.xiongdilian.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gu.baselibrary.utils.NetUtils;
import com.gu.baselibrary.utils.SPUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.adapter.citylist.HotCityGridAdapter;
import com.gu.xiongdilian.base.XDLBaseWithCheckLoginActivity;
import com.gu.xiongdilian.db.DBManager;
import com.gu.xiongdilian.pojo.citylist.CityModel;
import com.gu.xiongdilian.view.citylist.MyLetterListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nate on 2015/10/8.
 */
public class CityListActivity extends XDLBaseWithCheckLoginActivity {
    private static final int SEARCH_CITY_CODE = 1;
    public static final String CITY_KEY = "city";
    private BaseAdapter adapter;
    private ListView mCityLit;
    private TextView overlay, citysearch;
    private MyLetterListView letterListView;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private Handler handler;
    private OverlayThread overlayThread;
    private SQLiteDatabase database;
    private ArrayList<CityModel> mCityNames;
    private View city_locating_state;
    private View city_locate_failed;
    private TextView city_locate_state;
    private ProgressBar city_locating_progress;
    private ImageView city_locate_success_img;
    private LocationClient locationClient = null;
    private View hotcityall;
    // TODO: 2015/10/9 这个可以自己从服务器获取 
    private String[] hotcity = new String[]{"北京", "上海", "广州", "深圳", "杭州", "南京", "天津", "武汉", "重庆"};

    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.public_cityhot;
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
        setCustomToolbar(ToolbarType.WITHBACK, R.string.choose_city);
        LayoutInflater localLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        citysearch = (TextView) findViewById(R.id.city_search_edittext);
        mCityLit = (ListView) findViewById(R.id.public_allcity_list);
        overlay = (TextView) findViewById(R.id.overlay_tv);
        letterListView = (MyLetterListView) findViewById(R.id.cityLetterListView);

        View cityhot_header_blank = localLayoutInflater.inflate(R.layout.public_cityhot_header_padding_blank, mCityLit, false);
        mCityLit.addHeaderView(cityhot_header_blank, null, false);
        cityhot_header_blank = localLayoutInflater.inflate(R.layout.city_locate_layout, mCityLit, false);
        city_locating_state = cityhot_header_blank.findViewById(R.id.city_locating_state);
        city_locate_state = ((TextView) cityhot_header_blank.findViewById(R.id.city_locate_state));
        city_locating_progress = ((ProgressBar) cityhot_header_blank.findViewById(R.id.city_locating_progress));
        city_locate_success_img = ((ImageView) cityhot_header_blank.findViewById(R.id.city_locate_success_img));
        city_locate_failed = cityhot_header_blank.findViewById(R.id.city_locate_failed);
        mCityLit.addHeaderView(cityhot_header_blank);

        View hotheadview = localLayoutInflater.inflate(R.layout.public_cityhot_header_padding, mCityLit, false);
        mCityLit.addHeaderView(hotheadview, null, false);
        hotcityall = localLayoutInflater.inflate(R.layout.public_cityhot_allcity, mCityLit, false);
        final GridView localGridView = (GridView) hotcityall.findViewById(R.id.public_hotcity_list);

        mCityLit.addHeaderView(hotcityall);
        HotCityGridAdapter adapter = new HotCityGridAdapter(this, Arrays.asList(hotcity));
        localGridView.setAdapter(adapter);
        localGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityModel = parent.getAdapter()
                        .getItem(position).toString();
                SPUtils.put(CityListActivity.this, CITY_KEY, cityModel);
                Intent intent = new Intent();
                intent.putExtra(CITY_KEY, cityModel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        city_locating_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityModel = city_locate_state.getText().toString();
                SPUtils.put(CityListActivity.this, CITY_KEY, cityModel);
                Intent intent = new Intent();
                intent.putExtra(CITY_KEY, cityModel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        loadLocation();

        DBManager dbManager = new DBManager(this);
        dbManager.openDateBase();
        dbManager.closeDatabase();
        database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
                + DBManager.DB_NAME, null);
        mCityNames = getCityNames();
        database.close();
        letterListView
                .setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        overlay.setVisibility(View.INVISIBLE);
        setAdapter(mCityNames);
        mCityLit.setOnItemClickListener(new CityListOnItemClick());
        citysearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(CityListActivity.this, CitySearchActivity.class);
                startActivityForResult(intent, SEARCH_CITY_CODE);
                return false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_CITY_CODE:
                if (resultCode == RESULT_OK) {
                    if (null == data) {
                        return;
                    }
                    String city = data.getStringExtra(CITY_KEY);
                    Intent intent = new Intent();
                    intent.putExtra(CITY_KEY, city);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取位置
     */
    public void loadLocation() {
        city_locate_failed.setVisibility(View.GONE);
        city_locate_state.setVisibility(View.VISIBLE);
        city_locating_progress.setVisibility(View.VISIBLE);
        city_locate_success_img.setVisibility(View.GONE);
        city_locate_state.setText(getString(R.string.locating));
        if (locationClient == null) {
            locationClient = new LocationClient(CityListActivity.this);
            locationClient.registerLocationListener(new LocationListenner());
            LocationClientOption option = new LocationClientOption();
            option.setAddrType("all");
            option.setOpenGps(true);
            option.setCoorType("bd09ll");
            option.setScanSpan(2000);
            locationClient.setLocOption(option);
        }

        locationClient.start();
        locationClient.requestLocation();
    }

    /**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    private class LocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            city_locating_progress.setVisibility(View.GONE);

            if (location != null) {

                if (location.getCity() != null
                        && !location.getCity().equals("")) {
                    locationClient.stop();
                    city_locate_failed.setVisibility(View.GONE);
                    city_locate_state.setVisibility(View.VISIBLE);
                    city_locating_progress.setVisibility(View.GONE);
                    city_locate_success_img.setVisibility(View.VISIBLE);
                    city_locate_state.setText(location.getCity());

                } else {
                    city_locating_state.setVisibility(View.GONE);
                    city_locate_failed.setVisibility(View.VISIBLE);
                }
            } else {
                // 定位失败
                city_locating_state.setVisibility(View.GONE);
                city_locate_failed.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * @return 返回db中的所有城市
     */
    private ArrayList<CityModel> getCityNames() {
        ArrayList<CityModel> names = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM T_City ORDER BY NameSort", null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            CityModel cityModel = new CityModel();
            cityModel.setCityName(cursor.getString(cursor
                    .getColumnIndex("CityName")));
            cityModel.setNameSort(cursor.getString(cursor
                    .getColumnIndex("NameSort")));
            names.add(cityModel);
        }
        cursor.close();
        return names;
    }

    class CityListOnItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            CityModel cityModel = (CityModel) mCityLit.getAdapter()
                    .getItem(pos);
            if (cityModel != null) {
                SPUtils.put(CityListActivity.this, CITY_KEY, cityModel);
                Intent intent = new Intent();
                intent.putExtra(CITY_KEY, cityModel.getCityName());
                setResult(RESULT_OK, intent);
                finish();
            }
        }

    }

    /**
     * ListView
     */
    private void setAdapter(List<CityModel> list) {
        if (list != null) {
            adapter = new ListAdapter(this, list);
            mCityLit.setAdapter(adapter);
        }

    }

    /**
     * ListViewAdapter
     *
     * @author gugalor
     */
    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<CityModel> list;

        public ListAdapter(Context context, List<CityModel> list) {

            this.inflater = LayoutInflater.from(context);
            this.list = list;
            alphaIndexer = new HashMap<>();
            sections = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                String currentStr = list.get(i).getNameSort();
                String previewStr = (i - 1) >= 0 ? list.get(i - 1)
                        .getNameSort() : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = list.get(i).getNameSort();
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.public_cityhot_item,
                        null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                holder.name = (TextView) convertView
                        .findViewById(R.id.public_cityhot_item_textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getCityName());
            String currentStr = list.get(position).getNameSort();
            String previewStr = (position - 1) >= 0 ? list.get(position - 1)
                    .getNameSort() : " ";
            if (!previewStr.equals(currentStr)) {
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha;
            TextView name;
        }

    }

    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                mCityLit.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                handler.postDelayed(overlayThread, 1500);
            }
        }

    }

    // overlay
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            if (overlay != null) {
                overlay.setVisibility(View.GONE);
            }
        }
    }
}
