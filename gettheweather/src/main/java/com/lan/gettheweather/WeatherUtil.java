package com.lan.gettheweather;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

/**
 * Created by lan on 2016/7/19.
 */
public class WeatherUtil {
    static String getWeather(String city) {
        String httpUrl = "http://apis.baidu.com/heweather/weather/free";
        String httpArg = "city=" + city;
        String jsonResult = request(httpUrl, httpArg);
        System.out.println(jsonResult);
        return jsonResult;
    }


    /**
     * @param httpUrl
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "170f20e8fc732d9c6e9e1920c2254326");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}
