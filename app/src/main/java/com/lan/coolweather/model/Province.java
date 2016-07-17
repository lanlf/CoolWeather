package com.lan.coolweather.model;

/**
 * Created by lan on 2016/7/17.
 */
public class Province {
    private int id;
    private String provinceName;
    private String getProvinceCode;

    public String getProvinceCode() {
        return getProvinceCode;
    }

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String getProvinceCode) {
        this.getProvinceCode = getProvinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

}
