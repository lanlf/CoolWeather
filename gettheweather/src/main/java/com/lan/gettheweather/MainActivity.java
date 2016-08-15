package com.lan.gettheweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText city;
    private TextView weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (EditText) findViewById(R.id.city);
        weather = (TextView) findViewById(R.id.weather);
    }
    void get(View v){
        AsyncTask<String,String,String> task = new AsyncTask<String, String, String>() {


            private String getCity;

            @Override
            protected String doInBackground(String... strings) {
                String wea = WeatherUtil.getWeather(getCity);
                return wea;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getCity = String.valueOf(city.getText());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                weather.setText(s);
            }
        };
        task.execute();
    }
}
