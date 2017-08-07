package com.bluedon.gsm.detector;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Author: Keith
 * Date: 2017/7/31
 */

public class GSMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}

