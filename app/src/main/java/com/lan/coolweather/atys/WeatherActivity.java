package com.lan.coolweather.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lan.coolweather.R;
import com.lan.coolweather.util.HttpCallbackListener;
import com.lan.coolweather.util.HttpUtil;
import com.lan.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refresh;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.layout_weather_info);
        cityNameText = (TextView) findViewById(R.id.tv_cityName);
        publishText = (TextView) findViewById(R.id.tv_pulish);
        weatherDespText = (TextView) findViewById(R.id.tv_weather);
        temp1Text = (TextView) findViewById(R.id.tv_temp1);
        temp2Text = (TextView) findViewById(R.id.tv_temp2);
        currentDateText = (TextView) findViewById(R.id.tv_date);
        switchCity = (Button) findViewById(R.id.bt_switch);
        refresh = (Button) findViewById(R.id.bt_refresh);
        switchCity.setOnClickListener(this);
        refresh.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }
    }

    /**
     * 将获取的天气信息展现在界面上
     */
    private void showWeather() {
        cityNameText.setText(sp.getString("city_name", ""));
        temp1Text.setText(sp.getString("temp1", ""));
        temp2Text.setText(sp.getString("temp2", ""));
        weatherDespText.setText(sp.getString("weather_desp", ""));
        publishText.setText("今天" + sp.getString("publish_time", "") + "更新");
        currentDateText.setText(sp.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        System.out.println(sp.getString("city_name","")+sp.getString("weather_code",""));
    }

    /**
     * 查找城市对应天气代号
     *
     * @param countyCode
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        System.out.println("+++++++"+address);
        queryFromServer(address, "countyCode");
    }

    /**
     * 查找天气代号对应天气
     *
     * @param weatherCode
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        System.out.println("address"+address);
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型向服务器查询天气代号或天气信息
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        System.out.println(response);
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            System.out.println(array[0]+" ---"+array[1]);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    System.out.println("response:"+ response);
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败...");
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_refresh:
                refresh();
                break;
            case R.id.bt_switch:
                switchCity();
                break;
        }
    }

    private void refresh() {
        publishText.setText("同步中...");
        String weatherCode = sp.getString("weather_code", "");
        if (!TextUtils.isEmpty(weatherCode)) {
            queryWeatherInfo(weatherCode);
        } else {
            publishText.setText("同步失败。。。");
        }
    }

    private void switchCity() {
        Intent intent = new Intent(getApplicationContext(),ChooseAreaActivity.class);
        intent.putExtra("isFromWeatherAty",true);
        startActivity(intent);
    }

}
