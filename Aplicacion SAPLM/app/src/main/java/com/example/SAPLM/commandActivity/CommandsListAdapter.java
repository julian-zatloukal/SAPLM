package com.example.SAPLM.commandActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;
import com.example.SAPLM.settingsActivities.AvailableSetting;
import com.example.SAPLM.settingsActivities.SettingsLayoutAdapter;

import java.util.List;

public class CommandsListAdapter extends RecyclerView.Adapter<CommandsListAdapter.viewHolder> {

    private List<CommandItem> commandsList;

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView command_title, command_state_text;
        public ImageView command_icon, arrow_list;
        public ProgressBar status_progress_bar;
        public Guideline main_guide;

        public viewHolder(View view) {
            super(view);
            command_title = (TextView) view.findViewById(R.id.command_title_textView);
            status_progress_bar = (ProgressBar) view.findViewById(R.id.command_state_progressBar);
            command_state_text = (TextView) view.findViewById(R.id.command_state_textView);
            command_icon = (ImageView) view.findViewById(R.id.command_icon);
            arrow_list = (ImageView) view.findViewById(R.id.arrow_list_imageButton);
            main_guide = (Guideline) view.findViewById(R.id.guideline_main);
        }
    }


    public CommandsListAdapter(List<CommandItem> cmdList) {
        this.commandsList = cmdList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commands_layout_child_item_style, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        CommandItem item = commandsList.get(position);
        holder.command_title.setText(item.getItemName());

        if (item.getItemType()== CommandItem.ItemType.PARENT){
            holder.arrow_list.setVisibility(View.VISIBLE);
            if (item.getGroupState()== CommandItem.BoardGroupState.UNFOLDED)
            {
                Drawable icon = Application.getContext().getDrawable(R.drawable.icons_downward_arrow);
                icon.setColorFilter(Application.getContext().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                holder.arrow_list.setImageDrawable(icon);
            }else {
                Drawable icon = Application.getContext().getDrawable(R.drawable.icons_upward_arrow);
                icon.setColorFilter(Application.getContext().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                holder.arrow_list.setImageDrawable(icon);
            }
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.main_guide.getLayoutParams();
            params.guidePercent = 0.05f;
            holder.main_guide.setLayoutParams(params);
            holder.status_progress_bar.setVisibility(View.GONE);
            holder.command_state_text.setVisibility(View.GONE);
            holder.command_title.setTypeface(holder.command_title.getTypeface(), Typeface.BOLD);
        }else {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.main_guide.getLayoutParams();
            holder.command_state_text.setText(item.getDeviceStateSubtitle());
            params.guidePercent = 0.15f;
            holder.main_guide.setLayoutParams(params);
            holder.arrow_list.setVisibility(View.GONE);
            holder.command_state_text.setText(item.getDeviceStateSubtitle());
            holder.command_state_text.setVisibility(View.VISIBLE);
            holder.status_progress_bar.setIndeterminate(false);
            holder.status_progress_bar.setProgress(item.getProgressBarValue());
            holder.status_progress_bar.setVisibility(View.VISIBLE);

        }


        Drawable cmdIcon = Application.getContext().getDrawable(item.getDeviceStateIconID());
        cmdIcon.setColorFilter(Application.getContext().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        holder.command_icon.setImageDrawable(cmdIcon);
    }

    @Override
    public int getItemCount() {
        return commandsList.size();
    }
}