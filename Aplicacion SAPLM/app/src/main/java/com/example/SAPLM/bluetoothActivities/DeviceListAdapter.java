package com.example.SAPLM.bluetoothActivities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.viewHolder> {

    private List<AvailableDevice> deviceList;

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView textView_device_name;
        public ImageView imgView_device_state_icon;

        public viewHolder(View view) {
            super(view);
            textView_device_name = (TextView) view.findViewById(R.id.textView_bluetooth_device_name);
            imgView_device_state_icon = (ImageView) view.findViewById(R.id.imageView_bluetooth_device_state_icon);
        }
    }


    public DeviceListAdapter(List<AvailableDevice> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_list_layout_style, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        AvailableDevice device = deviceList.get(position);
        holder.textView_device_name.setText(device.getDeviceName());
        holder.textView_device_name.setTextAppearance(device.getTextAppearance());

        int iconID = device.getDeviceStateIconID();

        if (iconID!=0) {
            Drawable icon = Application.getContext().getDrawable(iconID);
            icon.setColorFilter(Application.getContext().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            holder.imgView_device_state_icon.setImageDrawable(icon);
        } else {
            holder.imgView_device_state_icon.clearColorFilter();
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            holder.imgView_device_state_icon.setImageDrawable(transparentDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
}
