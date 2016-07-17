package com.lan.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lan on 2016/7/17.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{

    /**
     * 省份建表语句
     * id:自增长主键
     * province_name：省名
     * province_code：省份代码
     */
    public static final String CREAT_PROVINCE = "create table Province (" + "id integer primary key autoincrement," + "province_name text" +
            "province_code text)";
    /**
     * 市建表语句
     * province_id：关联省份表的外键
     */
    public static final String CREAT_CITY = "create table Province (" + "id integer primary key autoincrement," + "city_name text" +
            "city_code text," + "province_id integer)";
    /**
     * 县建表语句
     * city_id：关联市表的外键
     */
    public static final String CREAT_COUNTY = "create table Province (" + "id integer primary key autoincrement," + "county_name text" +
            "county_code text" +"city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAT_PROVINCE);
        sqLiteDatabase.execSQL(CREAT_CITY);
        sqLiteDatabase.execSQL(CREAT_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
