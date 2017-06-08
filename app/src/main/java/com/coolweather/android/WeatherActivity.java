package com.coolweather.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化组件
        //最外层主界面的那个滚动的ScrollView
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        //当前显示的城市名字
        titleCity = (TextView) findViewById(R.id.title_city);
        //标题栏上面那个更新时间
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        //当前天气的温度
        degreeText = (TextView) findViewById(R.id.degree_text);
        //当前天气的其他信息
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        //预报下面的一系列信息的那个布局
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        //aqi指数上面的信息
        aqiText = (TextView) findViewById(R.id.aqi_text);
        //pm2.5上面的信息
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        //生活建议下面的内容
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
    }
}
