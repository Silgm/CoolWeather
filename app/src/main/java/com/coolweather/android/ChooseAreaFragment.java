package com.coolweather.android;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //当前选中的级别
    private int currentLevel = 0;

    //记录当前选中的省份
    private Province selectedProvince;
    //记录当前选中的城市
    private City selectedCity;
    //记录当前选中的县
    private County selectedCounty;

    //保存省的数据
    private List<Province> provinceList;
    //保存市的数据
    private List<City> cityList;
    //保存县的数据
    private List<County> countyList;

    private Button backButton;
    private TextView tittleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //初始化每个组件
        backButton = (Button) view.findViewById(R.id.back_button);
        tittleText = (TextView) view.findViewById(R.id.title_text);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
            }
        });
    }

    private void queryCities() {
        tittleText.setText("中国");
        backButton.setVisibility(View.GONE);
        //从数据库拉取数据
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() == 0) {
            //如果没有数据的话,联网读取数据
        } else {

        }
    }
}
