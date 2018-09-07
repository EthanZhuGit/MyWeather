package com.example.myweather.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.myweather.R;
import com.example.myweather.db.WeatherDB;
import java.util.ArrayList;
/**
 * Created by zyx10 on 2016/11/27 0027.
 */

public class ChooseCityActivity extends BaseActivity implements View.OnClickListener{
    private Button locate;
    private Button xuzhou;
    private Button nanjing;
    private Button beijing;
    public static String CITY_ID;
    public static String CITY_NAME;
    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;
    private LocationClient client;
    private WeatherDB weatherDB;
    public static final String TAG = "ChooseCityActivity";
    private AutoCompleteTextView autoCompleteTextView;
    boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: "+"before");
        Intent intent=getIntent();
        isFromWeatherActivity = intent.getBooleanExtra("isFromWeatherActivity", false);
        if (!isFromWeatherActivity) {
            SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
            int num = preferences.getInt("city_num", 0);
            if (num != 0) {
                Intent intent2 = new Intent(ChooseCityActivity.this, WeatherActivity.class);
                startActivity(intent2);
                finish();
            }
        }
        setContentView(R.layout.choose_city_layout);
        Log.d(TAG, "onCreate: "+"after");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolBarTitle.setText("添加城市");
        setSupportActionBar(toolbar);
        weatherDB = WeatherDB.getInstance(this);


        locate= (Button) findViewById(R.id.location);
        xuzhou= (Button) findViewById(R.id.xuzhou);
        nanjing= (Button) findViewById(R.id.nanjing);
        beijing= (Button) findViewById(R.id.beijing);
        getPermissions();//获取运行时权限
        client = new LocationClient(getApplicationContext());//定位
        client.registerLocationListener(mListener);
        initLocation();
        locate.setOnClickListener(this);
        xuzhou.setOnClickListener(this);
        nanjing.setOnClickListener(this);
        beijing.setOnClickListener(this);

        //cursorAdapter实现的自动填充搜索框
        autoCompleteTextView= (AutoCompleteTextView) findViewById(R.id.cityManual);
        MyCursorAdapter adapter = new MyCursorAdapter(this, null,true);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=view.getTag().toString();
                WeatherActivity.actionStart(ChooseCityActivity.this,s.substring(0,11),s.substring(11));
            }
        });



    }

    class MyCursorAdapter extends CursorAdapter {
        private LayoutInflater inflater;

        public MyCursorAdapter(Context context, Cursor c,boolean autoRequrey) {
            super(context, c,autoRequrey);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String s = cursor.getString(cursor.getColumnIndex("cityZh"))+"-"+cursor.getString(cursor.getColumnIndex("provinceZh"));
            String idAndName = cursor.getString(cursor.getColumnIndex("_id")) + cursor.getString(cursor.getColumnIndex("cityZh"));
            view.setTag(idAndName);

            ((TextView) view).setText(s);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            inflater = LayoutInflater.from(context);
            return  inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (constraint != null) {
                String selection="select * from City where cityZh like '%"+constraint.toString()+"%'";
                return weatherDB.query(selection);
            } else {
                return null;
            }
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location:
                client.start();
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        //execute the task
                        if (CITY_NAME == null) {
                            locate.setText("定位失败，点击重试");
                        }else {
                            Log.d(TAG, "run: "+CITY_NAME);
                            Cursor c= weatherDB.query("select * from City where cityZh like '%"+CITY_NAME.substring(0,2)+"%'");
                            if (c.moveToFirst()) {
                                CITY_ID = c.getString(c.getColumnIndex("_id"));
                                CITY_NAME = c.getString(c.getColumnIndex("cityZh"));
                                Log.d(TAG, "run: "+CITY_ID);
                            }
                            c.close();
                            locate.setText("定位成功"+" "+CITY_NAME);
                            WeatherActivity.actionStart(ChooseCityActivity.this,CITY_ID,CITY_NAME);

                        }
                    }
                }, 1000);
                break;
            case R.id.xuzhou:
                CITY_ID="CN101190801";
                WeatherActivity.actionStart(ChooseCityActivity.this,CITY_ID,"徐州");
                break;
            case R.id.nanjing:
                CITY_ID = "CN101190101";
                WeatherActivity.actionStart(ChooseCityActivity.this,CITY_ID,"南京");
                break;
            case R.id.beijing:
                CITY_ID = "CN101010100";
                WeatherActivity.actionStart(ChooseCityActivity.this,CITY_ID,"北京");
                break;
        }

    }
    
    

    @Override
    protected void onStop() {
        client.unRegisterLocationListener(mListener);
        super.onStop();
        Log.d(TAG, "onStop: ");
        autoCompleteTextView.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
        if (isFromWeatherActivity) {
            Intent intent = new Intent(ChooseCityActivity.this, WeatherActivity.class);
            startActivity(intent);
        }
    }

    private void initLocation() {
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span=1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        //option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        //option.setOpenGps(true);
        client.setLocOption(option);
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                CITY_NAME=location.getCity();
            }
        }

    };



    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }





    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
