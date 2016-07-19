package com.lan.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 和服务端进行交互
 * Created by lan on 2016/7/18.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url =new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();
                    //connection.setUseCaches(false);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                    StringBuffer response = new StringBuffer();
                    String line = null;
                    while ((line=reader.readLine()) != null){
                        System.out.println("*******line"+line);
                        response.append(line);
                    }
                    if(listener != null){
                        listener.onFinish(response.toString());
                        if(reader!=null)
                        reader.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if (listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
