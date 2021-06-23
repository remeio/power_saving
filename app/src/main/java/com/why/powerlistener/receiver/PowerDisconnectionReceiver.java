package com.why.powerlistener.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class PowerDisconnectionReceiver extends BroadcastReceiver {
    private final Callback callback;

    public PowerDisconnectionReceiver(Callback callback) {
        this.callback = callback;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "已断开连接电源，停止充电", Toast.LENGTH_SHORT).show();
        // 震动
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        // 查询所有未节电模式APP，可选择节电
        callback.run();
    }
}
