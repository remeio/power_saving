package com.why.powerlistener.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerOkReceiver extends BroadcastReceiver {
    private final Callback callback;

    public PowerOkReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "电量正常 :)", Toast.LENGTH_SHORT).show();
        // 查询所有节电模式APP，可选择恢复正常
        callback.run();
    }
}
