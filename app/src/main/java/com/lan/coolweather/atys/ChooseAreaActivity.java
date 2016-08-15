package com.lan.coolweather.atys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lan.coolweather.R;
import com.lan.coolweather.db.CoolWeatherDB;
import com.lan.coolweather.model.City;
import com.lan.coolweather.model.County;
import com.lan.coolweather.model.Province;
import com.lan.coolweather.util.HttpCallbackListener;
import com.lan.coolweather.util.HttpUtil;
import com.lan.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private List<String> datalist = new ArrayList<>();
    private ListView lv_location;
    private TextView tv_title;
    private ArrayAdapter adapter;
    private CoolWeatherDB coolWeatherDB;

    /**
     * 省、市、县列表
     */
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    /**
     * 被选中的省
     */
    private Province selectedProvince;

    /**
     * 被选中的市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private boolean isFromWeatherAty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        isFromWeatherAty = getIntent().getBooleanExtra("isFromWeatherAty", false);
        if(sp.getBoolean("city_selected",false) &&!isFromWeatherAty){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_choose_area);
        lv_location = (ListView) findViewById(R.id.lv_location);
        tv_title = (TextView) findViewById(R.id.tv_title);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datalist);
        lv_location.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        lv_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(getApplicationContext(),WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    System.out.println("--------------"+countyCode);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        });

        queryProvinces();
    }

    /**
     * 先从数据库查找省份信息，如果没有去服务器找
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvince();
        if(provinceList.size() > 0){
            datalist.clear();
            for(Province province : provinceList){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv_location.setSelection(0);
            tv_title.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 先从数据库查找市信息，如果没有去服务器找
     */
    private void queryCities() {
        cityList = coolWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size() > 0){
            datalist.clear();
            for(City city : cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_location.setSelection(0);
            tv_title.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 先从数据库查找县信息，如果没有去服务器找
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size() > 0){
            datalist.clear();
            for(County county : countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_location.setSelection(0);
            tv_title.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     *从服务器寻找数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
        }else {
            address ="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            /**
             *将服务器的数据写入数据库中
             *  @param response
             */
            @Override
            public void onFinish(String response) {
                boolean result =false;
                if("province".equals(type)){
                    result =Utility.handleProvinceResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(coolWeatherDB,response, selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(coolWeatherDB,response, selectedCity.getId());
                }
                /**
                 *获取数据成功后，根据级别加载布局
                 */
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }else {
                    System.out.println("----" +code);
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败。。",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 链接服务器时显示加载dialog
     */
  private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    /**
     * 取消dialog
     */
    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    /**
     * 修改后退键的设置
     * 如果当前是省份，就退出
     * 如果是市，就退到省
     * 如果是县，就退到市
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_PROVINCE){
            finish();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(isFromWeatherAty){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
