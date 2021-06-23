package com.why.powerlistener.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class PowerHelper {
    /**
     * 是否在充电
     *
     * @param context /
     * @return /
     */
    public static boolean isCharging(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
    }

    /**
     * 获取当前电量，0~100
     * @return 当前电量
     */
    public static int getBattery(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //当前剩余电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //电量最大值
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //电量百分比
        int batteryPct = (int) (level / (double) scale * 100);
        return (int) batteryPct;
    }

}
