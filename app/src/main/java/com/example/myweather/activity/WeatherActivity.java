package com.example.myweather.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.myweather.MyApplication;
import com.example.myweather.R;
import com.example.myweather.Util.GetWeatherDetail;
import com.example.myweather.db.WeatherDB;
import com.example.myweather.model.City;
import com.example.myweather.service.AutoUpdateService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zyx10 on 2016/11/27 0027.
 */

public class WeatherActivity extends BaseActivity {
    private static WeatherDB weatherDB = WeatherDB.getInstance(MyApplication.getContext());
    private static List<City> cityList = new ArrayList<>();
    private static final String TAG = "WeatherActivity";
    private DrawerLayout drawer;
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CityAdapter cityAdapter;
    private SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        final long halfHour = 10 * 1000;
        final long oneHour = 20 * 1000;
        final long twoHour = 30 * 1000;

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        final TextView toolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        final Button set = (Button) findViewById(R.id.set);

        final boolean isUpdateBackground = sharedPreferences.getBoolean("isUpdateBackground", false);
        Log.d(TAG, "onCreate: " + isUpdateBackground);
        String updateInterval = sharedPreferences.getString("updateInterval", "");
        Log.d(TAG, "onCreate: " + updateInterval);
        if (isUpdateBackground) {
            long intervalTime;
            switch (updateInterval) {
                case "half":
                    intervalTime = halfHour;
                    break;
                case "one":
                    intervalTime = oneHour;
                    break;
                case "two":
                    intervalTime = twoHour;
                    break;
                default:
                    intervalTime = halfHour;
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("updateInterval", "half");
                    editor.commit();
                    break;
            }
            AutoUpdateService.startService(WeatherActivity.this, intervalTime);
            Log.d(TAG, "onCreate: " + "service start" + intervalTime);
        }

        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolBar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();
        drawer.addDrawerListener(actionBarDrawerToggle);

        weatherDB = WeatherDB.getInstance(MyApplication.getContext());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);


        Intent intent = getIntent();
        String cityId = intent.getStringExtra("city_id");
        String cityName = intent.getStringExtra("city_name");
        Log.d(TAG, "onCreate: " + cityId);

        if (cityId == null) {
            int pageNum = 0;

            int cityNum = sharedPreferences.getInt("city_num", 0);
            for (int i = 0; i < cityNum; i++) {
                String s = sharedPreferences.getString("city" + i, "");
                cityId = s.substring(1, 12);
                cityName = s.substring(12);
                boolean isDefault = s.substring(0, 1).equals("1");
                if (isDefault) {
                    pageNum = i;
                }
                City city = new City(cityId, cityName, isDefault);
                Log.d(TAG, "onCreate: " + " " + cityId + " " + cityName);
                if (!cityList.contains(city)) {
                    cityList.add(city);

                    getWeather(cityId);
                    //requestWeather(cityId);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }
            }
            viewPager.setCurrentItem(pageNum);
        } else {
            City city = new City(cityId, cityName, false);
            Log.d(TAG, "onCreate: " + cityId + " " + cityName);
            if (!cityList.contains(city)) {
                cityList.add(city);

                getWeather(cityId);
                //requestWeather(cityId);

                mSectionsPagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(cityList.indexOf(city));
            }
        }


        ListView list = (ListView) drawer.findViewById(R.id.city_list);
        cityAdapter = new CityAdapter(this, R.layout.city_list_item, cityList);
        list.setAdapter(cityAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position);
                drawer.closeDrawers();
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (cityList.size() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                    builder.setTitle("Notice");
                    builder.setMessage("是否删除" + " " + cityList.get(position).getCityName());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cityList.remove(position);
                            cityAdapter.notifyDataSetChanged();
                            mSectionsPagerAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onItemLongClick: " + "size=1");
                            Intent intent = new Intent(WeatherActivity.this, ChooseCityActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                } else {
                    if (cityList.get(position).getIsDefault()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                        builder.setTitle("Notice");
                        builder.setMessage("无法删除默认城市");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create().show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                        builder.setTitle("Notice");
                        builder.setMessage("是否删除" + " " + cityList.get(position).getCityName());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cityList.remove(position);
                                cityAdapter.notifyDataSetChanged();
                                mSectionsPagerAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onItemLongClick: " + "size大于1" + cityList.size());
                                if (cityList.size() == position + 1) {
                                    viewPager.setCurrentItem(position - 1);
                                } else {
                                    viewPager.setCurrentItem(position);
                                }
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.create().show();
                    }
                }

                return true;
            }
        });


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + "set");
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.
                        from(WeatherActivity.this).inflate(R.layout.pop_up_window, null, false);
                PopupWindow popupWindow = new PopupWindow(linearLayout,
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.showAsDropDown(set);
                popupWindow.update();
                final Switch updateBackground = (Switch) linearLayout.findViewById(R.id.update_background);
                final RadioGroup interval = (RadioGroup) linearLayout.findViewById(R.id.interval);
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                boolean isUpdateBackground = sharedPreferences.getBoolean("isUpdateBackground", false);
                String updateInterval = sharedPreferences.getString("updateInterval", "half");
                if (isUpdateBackground) {
                    updateBackground.setChecked(true);
                    interval.setVisibility(View.VISIBLE);
                    switch (updateInterval) {
                        case "half":
                            interval.check(R.id.half);
                            break;
                        case "one":
                            interval.check(R.id.one);
                            break;
                        case "two":
                            interval.check(R.id.two);
                            break;
                        default:
                            interval.check(R.id.half);
                            break;
                    }
                } else {
                    updateBackground.setChecked(false);
                    interval.setVisibility(View.GONE);
                }

                interval.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        long intervalTime;
                        switch (checkedId) {
                            case R.id.half:
                                interval.check(R.id.half);
                                intervalTime = halfHour;
                                editor.putString("updateInterval", "half");
                                editor.commit();
                                break;
                            case R.id.one:
                                interval.check(R.id.one);
                                intervalTime = oneHour;
                                editor.putString("updateInterval", "one");
                                editor.commit();
                                break;
                            case R.id.two:
                                interval.check(R.id.two);
                                intervalTime = twoHour;
                                editor.putString("updateInterval", "two");
                                editor.commit();
                                break;
                            default:
                                interval.check(R.id.one);
                                intervalTime = halfHour;
                                break;
                        }
                        AutoUpdateService.startService(WeatherActivity.this, intervalTime);
                        Log.d(TAG, "onCheckedChanged: " + "RadioGroup " + "service Start " + intervalTime);
                    }
                });

                updateBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.d(TAG, "onCheckedChanged: " + "updateBackground");
                        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                        if (isChecked) {
                            interval.setVisibility(View.VISIBLE);
                            String updateInterval = sharedPreferences.getString("updateInterval", "half");
                            long intervalTime;
                            switch (updateInterval) {
                                case "half":
                                    interval.check(R.id.half);
                                    intervalTime = halfHour;
                                    break;
                                case "one":
                                    interval.check(R.id.one);
                                    intervalTime = oneHour;
                                    break;
                                case "two":
                                    interval.check(R.id.two);
                                    intervalTime = twoHour;
                                    break;
                                default:
                                    interval.check(R.id.half);
                                    intervalTime = halfHour;
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    editor.putString("updateInterval", "half");
                                    editor.commit();
                                    break;
                            }
                            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putBoolean("isUpdateBackground", isChecked);
                            editor.commit();
                            AutoUpdateService.startService(WeatherActivity.this, intervalTime);
                            Log.d(TAG, "onCheckedChanged: " + "updateBackground " + "service start " + intervalTime);
                            /*
                            interval.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    long intervalTime;
                                    switch (checkedId) {
                                        case R.id.half:
                                            interval.check(R.id.half);
                                            intervalTime = halfHour;
                                            editor.putString("updateInterval", "half");
                                            editor.commit();
                                            break;
                                        case R.id.one:
                                            interval.check(R.id.one);
                                            intervalTime = oneHour;
                                            editor.putString("updateInterval", "one");
                                            editor.commit();
                                            break;
                                        case R.id.two:
                                            interval.check(R.id.two);
                                            intervalTime = twoHour;
                                            editor.putString("updateInterval", "two");
                                            editor.commit();
                                            break;
                                        default:
                                            interval.check(R.id.one);
                                            intervalTime = halfHour;
                                            break;
                                    }
                                    AutoUpdateService.startService(WeatherActivity.this, intervalTime);
                                    Log.d(TAG, "onCheckedChanged: " + "RadioGroup " + "service Start " + intervalTime);
                                }
                            });
                            */

                        } else {
                            interval.setVisibility(View.GONE);
                            AutoUpdateService.stopService(WeatherActivity.this);
                            Log.d(TAG, "onCheckedChanged: " + "Service stop");
                            SharedPreferences.Editor e = getSharedPreferences("data", MODE_PRIVATE).edit();
                            e.putBoolean("isUpdateBackground", isChecked);
                            e.commit();
                        }
                    }
                });
            }
        });

        Button addCity = (Button) findViewById(R.id.drawer_add_city);
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ChooseCityActivity.class);
                intent.putExtra("isFromWeatherActivity", true);
                startActivity(intent);
                drawer.closeDrawers();
                finish();
            }
        });

        toolBarTitle.setText(cityList.get(viewPager.getCurrentItem()).getCityName());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolBarTitle.setText(cityList.get(position).getCityName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public static void actionStart(Context context, String cityId, String cityName) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra("city_id", cityId);
        intent.putExtra("city_name", cityName);
        context.startActivity(intent);
        ((AppCompatActivity) context).finish();
    }

    private void getWeather(String cityId) {
        GetWeatherDetail.requestWeather(cityId, new GetWeatherDetail.HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                GetWeatherDetail.handleWeatherResponse(weatherDB, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
     * 通过天气API获取JSON格式天气数据
     * @param cityId 城市id
     */
    /*
    private  void requestWeather(String cityId) {
        final String address="https://free-api.heweather.com/v5/now?city="+cityId+"&key=c603b45f070741998d0f6e8306e9c61a";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
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
                    String response=sb.toString();
                    Log.d(TAG, "run: "+response);
                    Message message=new Message();
                    message.obj=response;
                    handle.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private  Handler handle=new Handler(){
        public void handleMessage(Message msg) {
            String response=(String)msg.obj;
            Log.d(TAG, "handleMessage: "+response);
            handleWeatherResponse(response);
            mSectionsPagerAdapter.notifyDataSetChanged();
        }

    };
    */

    /*
     * 解析天气数据并存入WeatherDetail表
     * @param response Json字符串

    private void handleWeatherResponse(String response){
        try {
            Gson gson=new Gson();
            WeatherDetail weatherDetail = gson.fromJson(response,WeatherDetail.class);
            WeatherDetail.HeWeather5Bean heWeather5Bean = weatherDetail.getHeWeather5().get(0);
            String cityId=heWeather5Bean.getBasic().getId();
            String cityZh=heWeather5Bean.getBasic().getCity();
            String updateTime=heWeather5Bean.getBasic().getUpdate().getLoc();
            String condCode=heWeather5Bean.getNow().getCond().getCode();
            String condTxt=heWeather5Bean.getNow().getCond().getTxt();
            String tmp=heWeather5Bean.getNow().getTmp();
            String fl=heWeather5Bean.getNow().getFl();
            String dir=heWeather5Bean.getNow().getWind().getDir();
            String sc=heWeather5Bean.getNow().getWind().getSc();
            weatherDB.saveWeather(cityId,cityZh,updateTime,condCode,condTxt,tmp,fl,dir,sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Log.d(TAG, "newInstance: " + cityList.get(sectionNumber - 1).getCityId() + "    " + sectionNumber);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            String id = cityList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getCityId();
            Log.d(TAG, "onCreateView: " + id + "    " + getArguments().getInt(ARG_SECTION_NUMBER));
            String[] weatherData = weatherDB.readWeather(id);
            TextView weatherTxt = (TextView) rootView.findViewById(R.id.weather_txt);
            weatherTxt.setText(weatherData[4]);
            ImageView weatherIcon = (ImageView) rootView.findViewById(R.id.weather_icon);
            weatherIcon.setImageResource(
                    getResources().
                            getIdentifier("a" + weatherData[3], "drawable", getContext().getPackageName()));
            TextView tmpFeel = (TextView) rootView.findViewById(R.id.tmp_feel);
            tmpFeel.setText(weatherData[6] + " " + "℃");
            TextView tmpReal = (TextView) rootView.findViewById(R.id.tmp_real);
            tmpReal.setText(weatherData[5] + " " + "℃");
            TextView windDir = (TextView) rootView.findViewById(R.id.wind_dir);
            windDir.setText(weatherData[7]);
            TextView windSc = (TextView) rootView.findViewById(R.id.wind_sc);
            windSc.setText(weatherData[8] + "级");
            TextView updateTime = (TextView) rootView.findViewById(R.id.update_time);
            updateTime.setText("发布时间 " + weatherData[2]);
            return rootView;
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int currentPosition;

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currentPosition = position;
            super.setPrimaryItem(container, position, object);
        }
    }

    public class CityAdapter extends ArrayAdapter<City> {
        private int resourceId;

        public CityAdapter(Context context, int textViewResourceId, List<City> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final City city = getItem(position);
            //final City city = cityList.get(position);
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            } else {
                view = convertView;
            }
            TextView cityName = (TextView) view.findViewById(R.id.drawer_listview_city_name);
            final Button setDefault = (Button) view.findViewById(R.id.set_default);
            if (city != null) {
                cityName.setText(city.getCityName());
                if (cityList.size() == 1) {
                    setDefault.setVisibility(View.INVISIBLE);
                } else {
                    setDefault.setEnabled(true);
                    setDefault.setText("设为默认");
                    if (city.getIsDefault()) {
                        setDefault.setText("当前默认");
                        setDefault.setEnabled(false);
                    } else {
                        setDefault.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (City c :
                                        cityList) {
                                    c.setDefault(false);
                                }
                                cityList.get(position).setDefault(true);
                                //city.setDefault(true);
                                setDefault.setText("当前默认");
                                setDefault.setEnabled(false);
                                cityAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
            return view;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putInt("city_num", cityList.size());
        for (int i = 0; i < cityList.size(); i++) {
            if (cityList.get(i).getIsDefault()) {
                editor.putString("city" + i, "1" + cityList.get(i).getCityId() + cityList.get(i).getCityName());
            } else {
                editor.putString("city" + i, "0" + cityList.get(i).getCityId() + cityList.get(i).getCityName());
            }
        }
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (drawer.isDrawerOpen(findViewById(R.id.left_drawer))) {
            drawer.closeDrawers();
        } else {
            ActivityCollector.finishAll();
        }

    }
}
