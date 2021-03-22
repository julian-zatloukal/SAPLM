package com.example.SAPLM.settingsActivities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;

import java.util.List;

public class SettingsLayoutAdapter extends RecyclerView.Adapter<SettingsLayoutAdapter.viewHolder> {

    private List<AvailableSetting> settingsList;

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView setting_title, setting_subtitle;
        public ImageView setting_icon;

        public viewHolder(View view) {
            super(view);
            setting_title = (TextView) view.findViewById(R.id.setting_title);
            setting_subtitle = (TextView) view.findViewById(R.id.setting_subtitle);
            setting_icon = (ImageView) view.findViewById(R.id.setting_icon);
        }
    }


    public SettingsLayoutAdapter(List<AvailableSetting> settingsList) {
        this.settingsList = settingsList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_layout_style, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        AvailableSetting setting = settingsList.get(position);
        holder.setting_title.setText(setting.getTitle());
        holder.setting_subtitle.setText(setting.getSubtitle());

        Drawable icon = Application.getContext().getDrawable(setting.getIconResourceID());
        icon.setColorFilter(Application.getContext().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.setting_icon.setImageDrawable(icon);
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }
}
