package com.lan.coolweather.util;

/**
 * Created by lan on 2016/7/18.
 */
public interface HttpCallbackListener {
    /**
     *  @param response 向服务器请求返回的字符
     */
    void onFinish(String response);
    void onError(Exception e);
}
