package com.coolweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private ProgressDialog progressDialog;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        //一进来先查出来所有的省
        //保证当前的状态在省级
        queryProvinces();
    }

    private void queryProvinces() {
        tittleText.setText("中国");
        backButton.setVisibility(View.GONE);
        //从数据库拉取数据
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() == 0) {
            //如果没有数据的话,联网读取数据
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        } else {
            //如果有数据的话,直接调用
            //清空原先的数据
            dataList.clear();
            //之后添加当前的数据
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //改变数据显示
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        }
    }

    private void queryCities() {
        //逻辑基本一样
        tittleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", "" + selectedProvince.getId()).find(City.class);
        if (cityList.size() == 0) {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(address, "city");
        } else {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        }
    }

    private void queryCounties() {
        tittleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", selectedCity.getId() + "").find(County.class);
        if (countyList.size() == 0) {
            String address = "http://guolin.tech/api/china/" +
                    selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            queryFromServer(address, "county");
        } else {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //这两个方法的回调都是在子线程中的
            //这很关键

            //加载失败调用这个方法
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //加载成功回调这个方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (type.equals("province")) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if (type.equals("city")) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if (type.equals("county")) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    //这里必须切换回到住线程才可以
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("province")) {
                                queryProvinces();
                            } else if (type.equals("city")) {
                                queryCities();
                            } else if (type.equals("county")) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("全力加载");
            //点击其他地方不能关闭窗口
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
