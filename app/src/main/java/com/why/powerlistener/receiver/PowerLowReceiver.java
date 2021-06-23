package com.why.powerlistener.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.why.powerlistener.helper.DBHelper;

public class PowerLowReceiver extends BroadcastReceiver {
    private final Callback callback;

    public PowerLowReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "电量过低 :(", Toast.LENGTH_SHORT).show();
        // 弹出弹框让用户确认是否要将所有应用置为节电
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("是否将所有应用设为省电？")
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    DBHelper dbHelper = new DBHelper(context);
                    dbHelper.updateAllAppInfo(true);
                    callback.run();
                })
                .setNegativeButton("取消", (dialog1, which) -> {
                    callback.run();
                })
                .create();
        dialog.show();
    }
}
