package com.example.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zyx10 on 2016/12/25 0025.
 */

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context, long time) {
        Intent intent = new Intent(context, AutoUpdateService.class);
        intent.putExtra("time", time);
        context.startService(intent);
        Log.d(TAG, "startService: ");
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, AutoUpdateService.class);
        context.stopService(intent);
        Log.d(TAG, "stopService: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long intervalTime= intent.getLongExtra("time",0);
        Log.d(TAG, "onStartCommand: "+intervalTime);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent("UPDATE_BACKGROUND");
        intent1.putExtra("interval", intervalTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(),intervalTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
