package com.why.powerlistener.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.why.powerlistener.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DBHelper extends SQLiteOpenHelper {
    private final Context context;

    public DBHelper(Context context) {
        super(context, "db_power_saving", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE table_app_info(id int, name VARCHAR(300), saving bit)";
        db.execSQL(sql);
        initDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 更新所有 APP INFO
     *
     * @param isSave 省电与否
     */
    public void updateAllAppInfo(boolean isSave) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("saving", isSave ? 1 : 0);
        db.update("table_app_info", values, null, null);
        db.close();
    }

    /**
     * 获取 APP INFO
     * @param isSave 省电与否
     * @return /
     */
    public List<AppInfo> getAppInfo(boolean isSave) {
        List<AppInfo> appInfoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("table_app_info", null, "saving = ?", new String[]{isSave ? "1" : "0"}, null, null, null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                boolean saving = cursor.getInt(cursor.getColumnIndex("saving")) == 1;
                appInfoList.add(new AppInfo(id, name, saving));
            }
        }
        cursor.close();
        db.close();
        return appInfoList;
    }

    /**
     * 更新单个 APP INFO
     *
     * @param id     主键
     * @param isSave 省电与否
     */
    public void updateAppInfo(int id, boolean isSave) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("saving", isSave ? 1 : 0);
        db.update("table_app_info", values, "id = ?", new String[]{id + ""});
        db.close();
    }

    /**
     * 初始化数据库数据，清除需删除 APP，或删除缓存
     */
    private void initDatabase(SQLiteDatabase db) {
        AtomicInteger id = new AtomicInteger(1);
        getAllAppNames(context).forEach(name -> {
            ContentValues values = new ContentValues();
            values.put("id", id.getAndIncrement());
            values.put("name", name);
            values.put("saving", 0);
            db.insert("table_app_info", null, values);
        });
    }

    /**
     * 获取 APP 名称
     *
     * @param context /
     * @return APP 名称
     */
    private List<String> getAllAppNames(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager
                .getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                .stream()
                .map(e -> e.loadLabel(packageManager))
                .map(CharSequence::toString)
                .collect(Collectors.toList());
    }
}