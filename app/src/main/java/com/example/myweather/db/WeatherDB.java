package com.example.myweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by zyx10 on 2016/11/26 0026.
 */

public class WeatherDB {
    /**
     * 数据库名
     */
    //public static final String DB_NAME="MyWeather";
    /**
     * 数据库版本
     */
    //public static final int VERSION=1;
    private static WeatherDB WeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private WeatherDB(Context context) {

        db = openDatabase(context);
    }

    private SQLiteDatabase openDatabase(Context context) {
        String pathFile = "data/data/com.example.myweather/databases/MyWeather.db";
        String pathDir = "data/data/com.example.myweather/databases";
        SQLiteDatabase db;
        File databaseFile = new File(pathFile);
        if (!databaseFile.exists()) {
            File path1 = new File(pathDir);
            if (path1.mkdir()) {
                System.out.println("创建成功");
            } else {
                System.out.println("创建失败");
            }
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(context.getAssets().open("MyWeather.db"));
                bos = new BufferedOutputStream(new FileOutputStream(pathFile));
                int len;
                byte[] b = new byte[1024];
                while ((len = bis.read(b)) != -1) {
                    bos.write(b, 0, len);
                    bos.flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        db = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        return db;

    }

    /**
     * 获取CoolWeatherDB的实例
     */
    public synchronized static WeatherDB getInstance(Context context) {
        if (WeatherDB == null) {
            WeatherDB = new WeatherDB(context);
        }
        return WeatherDB;
    }

    public void saveWeather(String cityId, String cityZh, String updateTime, String condCode, String condTxt, String tmp, String fl, String dir, String sc) {
        ContentValues values = new ContentValues();
        values.put("_id", cityId);
        values.put("cityZh", cityZh);
        values.put("updateTime", updateTime);
        values.put("condCode", condCode);
        values.put("condTxt", condTxt);
        values.put("tmp", tmp);
        values.put("fl", fl);
        values.put("dir", dir);
        values.put("sc", sc);
        /**if((db.rawQuery("select * from WeatherDetail where _id='" + cityId+"'",null).getCount()==0)){
         db.insert("WeatherDetail", null, values);
         Log.d("insert", "saveWeather: "+cityId+cityZh+condTxt);
         }else {
         db.update("WeatherDetail", values, "updateTime=? and condCode=? and condTxt=? and tmp=?", new String[]{updateTime, condCode, condTxt, tmp});
         Log.d("update", "saveWeather: "+cityId+cityZh+condTxt);
         }
         */
        db.replace("WeatherDetail", null, values);

    }

    public String[] readWeather(String cityId) {
        String[] weatherData = new String[9];
        //Cursor cursor=db.query("WeatherDetail", null, cityId, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from WeatherDetail where _id='" + cityId + "'", null);
        if (cursor.moveToFirst()) {
            do {
                weatherData[0] = cursor.getString(cursor.getColumnIndex("_id"));
                weatherData[1] = cursor.getString(cursor.getColumnIndex("cityZh"));
                weatherData[2] = cursor.getString(cursor.getColumnIndex("updateTime"));
                weatherData[3] = cursor.getString(cursor.getColumnIndex("condCode"));
                weatherData[4] = cursor.getString(cursor.getColumnIndex("condTxt"));
                weatherData[5] = cursor.getString(cursor.getColumnIndex("tmp"));
                weatherData[6] = cursor.getString(cursor.getColumnIndex("fl"));
                weatherData[7] = cursor.getString(cursor.getColumnIndex("dir"));
                weatherData[8] = cursor.getString(cursor.getColumnIndex("sc"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("WeatherDB", "readWeather: " + Arrays.toString(weatherData));
        return weatherData;
    }

    public Cursor query(String selection) {
        return db.rawQuery(selection, null);
    }

}

