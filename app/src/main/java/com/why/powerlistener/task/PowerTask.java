package com.why.powerlistener.task;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.why.powerlistener.domain.MessageType;
import com.why.powerlistener.helper.PowerHelper;

public class PowerTask extends Task {
    private final static String TAG = PowerTask.class.getName();

    public final static String BOOLEAN_IS_CHARGING = "BOOLEAN_IS_CHARGING";

    public PowerTask(Handler handler, Context context) {
        super(handler, context);
    }

    @Override
    public void execute(Handler handler, Context context) {
        Message message = handler.obtainMessage();
        message.what = MessageType.POWER_TASK.getCode();
        Bundle bundle = new Bundle();
        bundle.putBoolean(BOOLEAN_IS_CHARGING, PowerHelper.isCharging(context));
        message.setData(bundle);
        handler.sendMessage(message);
        handler.postDelayed(this, 500);
    }
}
