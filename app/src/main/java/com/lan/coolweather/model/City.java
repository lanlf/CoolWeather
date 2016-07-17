package com.lan.coolweather.model;

/**
 * Created by lan on 2016/7/17.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;


    public String getCityCode() {
        return cityCode;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }


    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }


}
