package com.example.SAPLM.settingsActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;

import java.util.ArrayList;
import java.util.List;

public class settings_layout extends AppCompatActivity {

    private static Toolbar toolbar;

    public static List<AvailableSetting> settingsList = new ArrayList<>();
    private RecyclerView recyclerView;
    public static SettingsLayoutAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Application.setActivity(this);
        Application.setContext(this);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        recyclerView = (RecyclerView) findViewById(R.id.settingsRecyclerView);

        mAdapter = new SettingsLayoutAdapter(settingsList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AvailableSetting setting = settingsList.get(position);

                Context currentContext = Application.getContext();
                Activity currentActivity = Application.getActivity();
                Intent i = new Intent(currentContext, setting.getTargetActivity());
                currentContext.startActivity(i);
                //currentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setupSettingsList() {

        settingsList.add(new AvailableSetting(
            Application.getContext().getString(R.string.internet_conectivity_title),
                Application.getContext().getString(R.string.internet_conectivity_subtitle),
            R.drawable.ic_setting_internet_connectivity,
            InternetConectivityActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.bluetooth_settings_title),
                Application.getContext().getString(R.string.bluetooth_settings_subtitle),
                R.drawable.ic_setting_bluetooth,
                BluetoothSettingsActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.voice_recognition_settings_title),
                Application.getContext().getString(R.string.voice_recognition_settings_subtitle),
                R.drawable.ic_setting_voice_recognition,
                VoiceRecognitionSettingsActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.commands_settings_title),
                Application.getContext().getString(R.string.commands_settings_subtitle),
                R.drawable.ic_setting_commands_3,
                CommandsSettingsActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.contacts_communication_title),
                Application.getContext().getString(R.string.contacts_communication_subtitle),
                R.drawable.ic_setting_contacts_and_communication,
                ContactsCommunicationActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.appearance_miscellaneous_title),
                Application.getContext().getString(R.string.appearance_miscellaneous_subtitle),
                R.drawable.ic_setting_appearance_and_miscellaneous,
                AppearanceMiscellaneousActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.developer_console_activity_title),
                Application.getContext().getString(R.string.developer_console_subtitle),
                R.drawable.ic_setting_developer_console,
                DeveloperConsoleActivity.class
        ));

        settingsList.add(new AvailableSetting(
                Application.getContext().getString(R.string.information_help_title),
                Application.getContext().getString(R.string.information_help_subtitle),
                R.drawable.ic_setting_information_and_help,
                InformationHelpActivity.class
        ));

        // notify adapter about data set changes
        // so that it will render the list with new data
        //mAdapter.notifyDataSetChanged();
    }


}
