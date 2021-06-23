package com.why.powerlistener.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.why.powerlistener.R;
import com.why.powerlistener.domain.AppInfo;

import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<AppInfo> appInfoList;

    public AppInfoAdapter(List<AppInfo> appInfoList, Context context) {
        this.appInfoList = appInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.app_info_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppInfo appInfo = appInfoList.get(position);
        ((MyViewHolder) holder).tvName.setText(appInfo.getName());
        ((MyViewHolder) holder).ivIsSaving.setVisibility(appInfo.isSave() ? View.VISIBLE : View.GONE);
        ((MyViewHolder) holder).ivIsNotSaving.setVisibility(!appInfo.isSave() ? View.VISIBLE : View.GONE);
        ((MyViewHolder) holder).vCard.setOnLongClickListener(v -> {
            DBHelper dbHelper = new DBHelper(context);
            dbHelper.updateAppInfo(appInfo.getId(), !appInfo.isSave());
            appInfoList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "已将 " + appInfo.getName() + (appInfo.isSave() ? " 设置为正常模式" : " 设置为省电模式"), Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public View vCard;
    public ImageView ivIsSaving;
    public ImageView ivIsNotSaving;

    public MyViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        vCard = itemView.findViewById(R.id.vCard);
        ivIsSaving = itemView.findViewById(R.id.ivIsSaving);
        ivIsNotSaving = itemView.findViewById(R.id.ivIsNotSaving);
    }
}
