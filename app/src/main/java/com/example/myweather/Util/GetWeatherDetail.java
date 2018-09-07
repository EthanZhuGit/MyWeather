package com.example.myweather.Util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.myweather.MyApplication;
import com.example.myweather.db.WeatherDB;
import com.example.myweather.model.WeatherDetail;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zyx10 on 2016/12/25 0025.
 */

public class GetWeatherDetail {
    //private WeatherDB weatherDB=WeatherDB.getInstance(MyApplication.getContext());
    private static final String TAG = "GetWeatherDetail";

    /**
     * 通过天气API获取JSON格式天气数据
     *
     * @param cityId 城市id
     */
    public static void requestWeather(String cityId, final HttpCallBackListener listner) {
        final String address = "https://free-api.heweather.com/v5/now?city=" + cityId + "&key=c603b45f070741998d0f6e8306e9c61a";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String response = sb.toString();
                    Log.d(TAG, "run: " + response);

                    if (listner != null) {
                        listner.onFinish(response);
                    }
                } catch (Exception e) {
                    if (listner != null) {
                        listner.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public interface HttpCallBackListener {
        void onFinish(String response);

        void onError(Exception e);
    }


    /**
     * 解析天气数据并存入WeatherDetail表
     *
     * @param response Json字符串
     */
    public static void handleWeatherResponse(WeatherDB weatherDB, String response) {
        try {
            Gson gson = new Gson();
            WeatherDetail weatherDetail = gson.fromJson(response, WeatherDetail.class);
            WeatherDetail.HeWeather5Bean heWeather5Bean = weatherDetail.getHeWeather5().get(0);
            String cityId = heWeather5Bean.getBasic().getId();
            String cityZh = heWeather5Bean.getBasic().getCity();
            String updateTime = heWeather5Bean.getBasic().getUpdate().getLoc();
            String condCode = heWeather5Bean.getNow().getCond().getCode();
            String condTxt = heWeather5Bean.getNow().getCond().getTxt();
            String tmp = heWeather5Bean.getNow().getTmp();
            String fl = heWeather5Bean.getNow().getFl();
            String dir = heWeather5Bean.getNow().getWind().getDir();
            String sc = heWeather5Bean.getNow().getWind().getSc();
            weatherDB.saveWeather(cityId, cityZh, updateTime, condCode, condTxt, tmp, fl, dir, sc);
            Log.d(TAG, "handleWeatherResponse: " + "saveWeather");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
