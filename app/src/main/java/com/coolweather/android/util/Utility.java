package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    //解析和处理服务器返回的省级JSON数据
    public static boolean handleProvinceResponse(String response) {
        //先判断字符串是不是空的
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    //处理JSON数据
                    Province province = new Province();
                    JSONObject provinceData = allProvince.getJSONObject(i);
                    province.setProvinceCode(provinceData.getInt("id"));
                    province.setProvinceName(provinceData.getString("name"));
                    //存入数据库
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的省级JSON数据
    public static boolean handleCityResponse(String response, int provinceId) {
        //由于JSON的数据格式全部都一样
        //所以处理逻辑也都一样
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    City city = new City();
                    JSONObject cityData = allCity.getJSONObject(i);
                    city.setCityCode(cityData.getInt("id"));
                    city.setCityName(cityData.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {
        //由于JSON的数据格式全部都一样
        //所以处理逻辑也都一样
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    County county = new County();
                    JSONObject countyData = allCounty.getJSONObject(i);
                    county.setCityId(cityId);
                    county.setCountyName(countyData.getString("name"));
                    county.setWeatherId(countyData.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
