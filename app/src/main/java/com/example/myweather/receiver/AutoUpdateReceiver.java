package com.example.myweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myweather.Util.GetWeatherDetail;
import com.example.myweather.db.WeatherDB;

/**
 * Created by zyx10 on 2016/12/26 0026.
 */

public class AutoUpdateReceiver extends BroadcastReceiver{
    private static final String TAG = "AutoUpdateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        final WeatherDB weatherDB = WeatherDB.getInstance(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        int cityNum = sharedPreferences.getInt("city_num", 0);
        for (int i=0; i<cityNum; i++) {
            String s=sharedPreferences.getString("city"+i,"");
            String cityId = s.substring(1, 12);
            String cityName = s.substring(12);
            long intervalTime = intent.getLongExtra("interval", 0);
            Log.d(TAG, "onReceive "+" "+cityId+" "+cityName+intervalTime);
            GetWeatherDetail.requestWeather(cityId, new GetWeatherDetail.HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    GetWeatherDetail.handleWeatherResponse(weatherDB,response);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
