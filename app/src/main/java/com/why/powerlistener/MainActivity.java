package com.why.powerlistener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.why.powerlistener.domain.AppInfo;
import com.why.powerlistener.domain.MessageType;
import com.why.powerlistener.domain.MyIntent;
import com.why.powerlistener.helper.AppInfoAdapter;
import com.why.powerlistener.helper.DBHelper;
import com.why.powerlistener.helper.PowerHelper;
import com.why.powerlistener.receiver.PowerConnectionReceiver;
import com.why.powerlistener.receiver.PowerDisconnectionReceiver;
import com.why.powerlistener.receiver.PowerLowReceiver;
import com.why.powerlistener.receiver.PowerOkReceiver;
import com.why.powerlistener.task.PowerTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = MainActivity.class.getName();

    private TextView tvPowerStatus;

    private TextView tvPowerSaveMode;

    private TextView tvPowerTip;

    private TextView tvBattery;

    private ImageView ivQs;

    private RecyclerView rvAppInfo;

    private AppInfoAdapter appInfoAdapter;

    private DBHelper dbHelper;

    private final List<AppInfo> appInfoList = new ArrayList<>();

    private MainActivity mainActivity;


    // MOCK
    private TextView tvMockNormal;
    private TextView tvMockLow;
    private TextView tvMockConnect;
    private TextView tvMockDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        // 绑定组件
        bind();
        // 数据库
        dbHelper = new DBHelper(this);
        // 显示列表
        appInfoAdapter = new AppInfoAdapter(appInfoList, this);
        appInfoAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mainActivity.setTvBattery();
            }
        });
        rvAppInfo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAppInfo.setAdapter(appInfoAdapter);
        //添加动画
        rvAppInfo.setItemAnimator(new DefaultItemAnimator());
        // 注册广播
        registerReceivers();
        // 运行获取充电状态定时任务
        runPowerTask();
    }

    /**
     * 绑定组件
     */
    private void bind() {
        tvPowerStatus = (TextView) findViewById(R.id.tvPowerStatus);
        tvPowerSaveMode = (TextView) findViewById(R.id.tvPowerSaveMode);
        tvPowerTip = (TextView) findViewById(R.id.tvPowerTip);
        tvBattery = (TextView) findViewById(R.id.tvBattery);
        rvAppInfo = (RecyclerView) findViewById(R.id.rvAppInfo);
        ivQs = (ImageView) findViewById(R.id.ivQs);
        tvMockNormal = (TextView) findViewById(R.id.tvMockNormal);
        tvMockConnect = (TextView) findViewById(R.id.tvMockConnect);
        tvMockDisconnect = (TextView) findViewById(R.id.tvMockDisconnect);
        tvMockLow = (TextView) findViewById(R.id.tvMockLow);
        mock();
    }

    private void mock() {
        tvMockNormal.setOnClickListener(e -> {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(MyIntent.ACTION_BATTERY_OKAY);
            this.sendBroadcast(intent);
        });
        tvMockConnect.setOnClickListener(e -> {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(MyIntent.ACTION_POWER_CONNECTED);
            this.sendBroadcast(intent);
        });
        tvMockDisconnect.setOnClickListener(e -> {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(MyIntent.ACTION_POWER_DISCONNECTED);
            this.sendBroadcast(intent);
        });
        tvMockLow.setOnClickListener(e -> {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(MyIntent.ACTION_BATTERY_LOW);
            this.sendBroadcast(intent);
        });
    }

    private void setTvBattery() {
        int isSaving = dbHelper.getAppInfo(true).size();
        int isNotSaving = dbHelper.getAppInfo(false).size();
        tvBattery.setText(String.format("节电比:%d/%d", isSaving, isSaving + isNotSaving));
    }

    /**
     * 注册广播
     */
    public void registerReceivers() {
        // 初始化广播映射
        Map<String, BroadcastReceiver> broadcastReceiverMap = new HashMap<>(4);
        PowerLowReceiver powerLowReceiver = new PowerLowReceiver(() -> {
            tvPowerSaveMode.setText("电量过低");
            tvPowerTip.setText("超级省电");
            appInfoList.clear();
            appInfoAdapter.notifyDataSetChanged();
            mainActivity.setTvBattery();
            ivQs.setVisibility(View.VISIBLE);
        });
        PowerDisconnectionReceiver powerDisconnectionReceiver = new PowerDisconnectionReceiver(() -> {
            tvPowerSaveMode.setText("断开电源");
            tvPowerTip.setText("自定义省电");
            appInfoList.clear();
            appInfoList.addAll(dbHelper.getAppInfo(false));
            appInfoAdapter.notifyDataSetChanged();
            mainActivity.setTvBattery();
            ivQs.setVisibility(View.GONE);
            if (appInfoList.size() == 0) {
                ivQs.setVisibility(View.VISIBLE);
            }
        });
        PowerOkReceiver powerOkReceiver = new PowerOkReceiver(() -> {
            tvPowerSaveMode.setText("电量正常");
            tvPowerTip.setText("自定义正常");
            appInfoList.clear();
            appInfoList.addAll(dbHelper.getAppInfo(true));
            appInfoAdapter.notifyDataSetChanged();
            mainActivity.setTvBattery();
            ivQs.setVisibility(View.GONE);
            if (appInfoList.size() == 0) {
                ivQs.setVisibility(View.VISIBLE);
            }
        });
        PowerConnectionReceiver powerConnectionReceiver = new PowerConnectionReceiver(() -> {
            tvPowerSaveMode.setText("接上电源");
            tvPowerTip.setText("关闭省电");
            appInfoList.clear();
            appInfoAdapter.notifyDataSetChanged();
            mainActivity.setTvBattery();
            ivQs.setVisibility(View.VISIBLE);
        });
        broadcastReceiverMap.put(Intent.ACTION_POWER_DISCONNECTED, powerDisconnectionReceiver);
        broadcastReceiverMap.put(Intent.ACTION_BATTERY_LOW, powerLowReceiver);
        broadcastReceiverMap.put(Intent.ACTION_BATTERY_OKAY, powerOkReceiver);
        broadcastReceiverMap.put(Intent.ACTION_POWER_CONNECTED, powerConnectionReceiver);
        broadcastReceiverMap.put(MyIntent.ACTION_POWER_DISCONNECTED, powerDisconnectionReceiver);
        broadcastReceiverMap.put(MyIntent.ACTION_BATTERY_LOW, powerLowReceiver);
        broadcastReceiverMap.put(MyIntent.ACTION_BATTERY_OKAY, powerOkReceiver);
        broadcastReceiverMap.put(MyIntent.ACTION_POWER_CONNECTED, powerConnectionReceiver);
        // 批量动态注册广播
        for (String key : broadcastReceiverMap.keySet()) {
            BroadcastReceiver receiver = broadcastReceiverMap.get(key);
            IntentFilter intentFilter = new IntentFilter(key);
            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPowerTask();
    }

    private Handler powerHandler;

    private static boolean isCheck = false;

    public void initPowerSaveMode(boolean isCharging, int battery) {
        // 仅判断一次
        if (isCheck) {
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isCharging) {
            intent.setAction(MyIntent.ACTION_POWER_CONNECTED);
        } else if (battery < 15) {
            intent.setAction(MyIntent.ACTION_BATTERY_LOW);
        } else {
            intent.setAction(MyIntent.ACTION_BATTERY_OKAY);
        }
        this.sendBroadcast(intent);
        isCheck = true;
    }


    public void runPowerTask() {
        powerHandler = new Handler(msg -> {
            int what = msg.what;
            if (what == MessageType.POWER_TASK.getCode()) {
                boolean isCharging = msg.getData().getBoolean(PowerTask.BOOLEAN_IS_CHARGING);
                int battery = PowerHelper.getBattery(this);
                tvPowerStatus.setText(isCharging ? "充电中 " + battery + "%" : "未充电 " + battery + "%");
                // 对于第一次检测结果，进行发送广播
                initPowerSaveMode(isCharging, battery);
            }
            return false;
        });
        powerHandler.post(new PowerTask(powerHandler, this));
    }

    public void stopPowerTask() {
        powerHandler.removeCallbacksAndMessages(null);
    }

}