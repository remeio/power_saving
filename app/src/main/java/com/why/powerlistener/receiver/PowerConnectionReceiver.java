package com.why.powerlistener.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.why.powerlistener.helper.DBHelper;

public class PowerConnectionReceiver extends BroadcastReceiver {
    private final Callback callback;

    public PowerConnectionReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "已连接电源，关闭省电模式，设置所有应用为正常", Toast.LENGTH_SHORT).show();
        // 播放铃声
        defaultMediaPlayer(context);
        // 将所有应用置为正常
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.updateAllAppInfo(false);
        callback.run();
    }

    /**
     * 播放系统默认提示音
     */
    public void defaultMediaPlayer(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }
}
