package com.lan.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.lan.coolweather.db.CoolWeatherDB;
import com.lan.coolweather.model.City;
import com.lan.coolweather.model.County;
import com.lan.coolweather.model.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 解析处理数据
 * Created by lan on 2016/7/18.
 */
public class Utility {
    /**
     *解析处理服务器返回的省份数据
     *  @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response){
       if(!TextUtils.isEmpty(response)){
           String [] allProvinces = response.split(",");
           if(allProvinces !=null && allProvinces.length >0){
               for (String p : allProvinces){
                   System.out.println("*******" + p);
                   String[] array = p.split("\\|");
                   Province province = new Province();
                   province.setProvinceCode(array[0]);
                   province.setProvinceName(array[1]);
                   coolWeatherDB.saveProvince(province);
               }
               return true;
           }
       }
        return false;
    }

    /**
     *解析处理市数据
     * @param coolWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String [] allCities = response.split(",");
            if(allCities !=null && allCities.length >0){
                for (String p : allCities){
                    String[] array = p.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *解析处理县数据
     * @param coolWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String [] allCounties = response.split(",");
            if(allCounties !=null && allCounties.length >0){
                for (String p : allCounties){
                    String[] array = p.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器上返回的JSON数据，获取天气信息
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = "";
            if(response.contains("ptime"))
            publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName, weatherCode,temp1, temp2 ,weatherDesp ,publishTime);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
    }

    /**
     *将天气信息保存到sharePreference
     *  @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.apply();
    }

}
