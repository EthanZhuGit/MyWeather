package com.example.myweather.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyx10 on 2016/12/22 0022.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity a :
                activities) {
            if (!a.isFinishing()) {
                a.finish();
            }
        }
    }
}
