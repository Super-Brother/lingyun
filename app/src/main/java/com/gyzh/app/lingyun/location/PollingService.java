package com.gyzh.app.lingyun.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.location.LocationClientOption;
import com.gyzh.app.lingyun.utils.MyApplication;

/**
 * @author Zero
 * @Create 2015-06-07
 */
public class PollingService extends Service {

    public static final String ACTION = "com.gyzh.app.lingyun.location.PollingService";
    private MyApplication application;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        application = (MyApplication) getApplication();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        getLocaiton();
    }

    /**
     * 网络定位
     */
    private void getLocaiton() {
        LocationClientOption option = new LocationClientOption();
        // 是否打开gps进行定位
        option.setOpenGps(false);
        // 设置定位模式 Battery_Saving 低功耗模式 Device_Sensors 仅设备(Gps)模式 Hight_Accuracy 高精度模式
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);// 低能耗
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);//反地理编码
        application.mLocationClient.setLocOption(option);
        application.mLocationClient.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

}
