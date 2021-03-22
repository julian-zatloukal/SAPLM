package com.example.SAPLM.bluetoothActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.SAPLM.Application;
import com.example.SAPLM.R;
import com.example.SAPLM.settingsActivities.BluetoothSettingsActivity;
import com.example.SAPLM.settingsActivities.RecyclerTouchListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    private static Toolbar toolbar;


    public static List<AvailableDevice> deviceList = new ArrayList<>();
    private RecyclerView recyclerView;
    public static DeviceListAdapter mAdapter;

    public static ImageView imgView_bluetoothStatusIcon;
    public static TextView textView_bluetoothState;
    public static ProgressBar loading_progressBar;

    public static Boolean guiInitialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);
        Application.setActivity(this);
        Application.setContext(this);

        toolbar = this.findViewById(R.id.toolbar);
        textView_bluetoothState = this.findViewById(R.id.textView_btStatus);
        imgView_bluetoothStatusIcon = this.findViewById(R.id.imgView_btStatusIcon);
        loading_progressBar = this.findViewById(R.id.loading_progressBar);

        Sprite fadingCircle = new Circle();
        fadingCircle.setColor(Color.WHITE);
        loading_progressBar.setIndeterminateDrawable(fadingCircle);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        recyclerView = (RecyclerView) findViewById(R.id.deviceListRecyclerView);

        mAdapter = new DeviceListAdapter(deviceList);

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
                AvailableDevice device = deviceList.get(position);

                BluetoothConnection.connectBluetoothDeviceAsync(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        guiInitialized = true;

        BluetoothConnection.listAvailableBluetoothDevices();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void updateDevicesList(){
        if (guiInitialized) {
            Application.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BluetoothActivity.mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void configBtn_onClick(View view){
        Context currentContext = Application.getContext();
        Intent i = new Intent(currentContext, BluetoothSettingsActivity.class);
        currentContext.startActivity(i);
    }


    public enum bluetoothConnectionState { SYSTEM_DISCONNECTED, SYSTEM_CONNECTED, BLUETOOTH_DISABLED, WAITING, BT_TURNING_OFF, BT_TURNING_ON, UNDEFINED}
    public static bluetoothConnectionState currentBluetoothState = bluetoothConnectionState.UNDEFINED;

    public static void setBluetoothStateGui(bluetoothConnectionState state){
        if (guiInitialized) {
            currentBluetoothState = state;

            if (Looper.myLooper() == Looper.getMainLooper()) {
                switch (currentBluetoothState) {
                    case SYSTEM_CONNECTED:
                        textView_bluetoothState.setText(R.string.system_connected_message);
                        imgView_bluetoothStatusIcon.setImageResource(R.drawable.ic_done_icon);
                        imgView_bluetoothStatusIcon.setVisibility(View.VISIBLE);
                        loading_progressBar.setVisibility(View.GONE);
                        break;
                    case BLUETOOTH_DISABLED:
                        textView_bluetoothState.setText(R.string.bluetooth_disabled_message);
                        imgView_bluetoothStatusIcon.setImageResource(R.drawable.ic_bluetooth_disabled);
                        imgView_bluetoothStatusIcon.setVisibility(View.VISIBLE);
                        loading_progressBar.setVisibility(View.GONE);
                        break;
                    case BT_TURNING_ON:
                        textView_bluetoothState.setText(R.string.bluetooth_initializing_adapater);
                        imgView_bluetoothStatusIcon.setVisibility(View.GONE);
                        loading_progressBar.setVisibility(View.VISIBLE);
                        break;
                    case BT_TURNING_OFF:
                        textView_bluetoothState.setText(R.string.bluetooth_closing_adapter);
                        imgView_bluetoothStatusIcon.setVisibility(View.GONE);
                        loading_progressBar.setVisibility(View.VISIBLE);
                        break;
                    case SYSTEM_DISCONNECTED:
                        textView_bluetoothState.setText(R.string.system_disconnected_message);
                        imgView_bluetoothStatusIcon.setImageResource(R.drawable.ic_close_icon);
                        imgView_bluetoothStatusIcon.setVisibility(View.VISIBLE);
                        loading_progressBar.setVisibility(View.GONE);
                        break;
                    case WAITING:
                        textView_bluetoothState.setText(R.string.bluetooth_attempting_connection_message);
                        imgView_bluetoothStatusIcon.setVisibility(View.GONE);
                        loading_progressBar.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                Application.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (currentBluetoothState) {
                            case SYSTEM_CONNECTED:
                                textView_bluetoothState.setText(R.string.system_connected_message);
                                imgView_bluetoothStatusIcon.setImageResource(R.drawable.ic_done_icon);
                                imgView_bluetoothStatusIcon.setVisibility(View.VISIBLE);
                                loading_progressBar.setVisibility(View.GONE);
                                break;
                            case BLUETOOTH_DISABLED:
                                textView_bluetoothState.setText(R.string.bluetooth_disabled_message);
                                imgView_bluetoothStatusIcon.setImageResource(R.drawable.ic_bluetooth_disabled);
                                imgView_bluetoothStatusIcon.setVisibility(View.VISIBLE);
                                loading_progressBar.setVisibility(View.GONE);
                                break;
                            case BT_TURNING_ON:
                                textView_bluetoothState.setText(R.string.bluetooth_initializing_adapater);
                                imgView_bluetoothStatusIcon.setVisibility(View.GONE);
                                loading_progressBar.setVisibility(View.VISIBLE);
                                break;
                            case BT_TURNING_OFF:
                                textView_bluetoothState.setText(R.string.bluetooth_closing_adapter);
                                imgView_bluetoothStatusIcon.setVisibility(View.GONE);
                                loading_progressBar.setVisibility(View.GONE);
                                break;
                            case SYSTEM_DISCONNECTED:
                                textView_bluetoothState.setText(R.string.system_disconnected_message);
                                imgView_bluetoothStatusIcon.setImageResource(R.drawable.ic_close_icon);
                                imgView_bluetoothStatusIcon.setVisibility(View.VISIBLE);
                                loading_progressBar.setVisibility(View.GONE);
                                break;
                            case WAITING:
                                textView_bluetoothState.setText(R.string.bluetooth_attempting_connection_message);
                                imgView_bluetoothStatusIcon.setVisibility(View.GONE);
                                loading_progressBar.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
            }
        }
    }


}
